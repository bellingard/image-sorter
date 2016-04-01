package fr.bellingard.tools.imagesorter;

import com.drew.imaging.jpeg.JpegProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ImageSorter {

    private class CreationDay {
        String year;
        String month;
        String day;
    }

    private static final Pattern datePattern = Pattern.compile("(\\w\\w\\w_)?(2\\d{3})([0-1]\\d)([0-3]\\d)_.*");

    private Path targetFolder;
    private Path nonSortedFolder;

    public ImageSorter(Path targetFolder) {
        this.targetFolder = targetFolder;
        this.nonSortedFolder = targetFolder.resolve("non-sorted");
    }

    public String handleFile(Path image) {
        String message;

        try {
            CreationDay creationDay = findDateInformation(image);
            if (creationDay == null) {
                message = "Could not find creation date from Exif or file name. Archiving "
                        + image.toString()
                        + " to non-sorted folder.";
                archiveFile(image);
            } else {
                Path targetFolderWithDate = copyFileToTargetFolder(image, creationDay);
                message = image.getFileName() + " copied to " + targetFolderWithDate;
            }
        } catch (IOException e) {
            message = "Could not move " + image.toString() + ".\n=> Reason is: " + e.getMessage();
        }

        return message;
    }

    private void archiveFile(Path image) throws IOException {
        if (Files.notExists(nonSortedFolder)) {
            Files.createDirectories(nonSortedFolder);
        }
        Files.copy(image, nonSortedFolder.resolve(image.getFileName()));
    }

    private Path copyFileToTargetFolder(Path image, CreationDay creationDay) throws IOException {
        Path targetFolderWithDate = targetFolder.resolve(creationDay.year)
                .resolve(creationDay.year + "-" + creationDay.month + "-" + creationDay.day);
        if (Files.notExists(targetFolderWithDate)) {
            Files.createDirectories(targetFolderWithDate);
        }

        Files.copy(image, targetFolderWithDate.resolve(image.getFileName()));
        return targetFolderWithDate;
    }

    private CreationDay findDateInformation(Path image) throws IOException {
        CreationDay creationDay = extractDateInformationFromExif(image);
        if (creationDay == null) {
            // Exif could not be read, try to guess from the file nam
            creationDay = guessDateInformationFromFileName(image);
        }
        return creationDay;
    }

    private CreationDay guessDateInformationFromFileName(Path image) {
        CreationDay creationDay = null;

        String name = image.getFileName().toString();
        Matcher matcher = datePattern.matcher(name);
        if (matcher.matches()) {
            creationDay = new CreationDay();
            creationDay.year = matcher.group(2);
            creationDay.month = matcher.group(3);
            creationDay.day = matcher.group(4);
        }

        return creationDay;
    }

    private CreationDay extractDateInformationFromExif(Path image) throws IOException {
        CreationDay creationDay = null;
        try {
            Optional<Date> originalDate = MetadataReader.on(image).getOriginalDate();
            if (originalDate.isPresent()) {
                creationDay = new CreationDay();

                Calendar cal = Calendar.getInstance();
                cal.setTime(originalDate.get());

                creationDay.year = Integer.toString(cal.get(Calendar.YEAR));

                int rawMonth = cal.get(Calendar.MONTH) + 1;
                creationDay.month = (rawMonth < 10) ? "0" + rawMonth : Integer.toString(rawMonth);

                int rawDay = cal.get(Calendar.DAY_OF_MONTH);
                creationDay.day = (rawDay < 10) ? "0" + rawDay : Integer.toString(rawDay);
            }
        } catch (JpegProcessingException e) {
            // this is not a JPEG, date information has to be guessed differently
        }

        return creationDay;
    }

}
