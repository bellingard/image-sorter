package fr.bellingard.tools.imagesorter;

import com.drew.imaging.ImageProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ImageSorter {

    private static final Pattern datePattern = Pattern.compile("(2\\d{3})([0-1]\\d)([0-3]\\d).*");

    private Path targetFolder;
    private String year;
    private String month;
    private String day;

    public ImageSorter(Path targetFolder) {
        this.targetFolder = targetFolder;
    }

    public String handleFile(Path image) {
        String message;

        try {
            findDateInformation(image);
            if (year == null) {
                message = "Could not find creation date from Exif or file name. Skipping " + image.toString();
            } else {
                Path targetFolderWithDate = copyFileToTargetFolder(image);
                message = image.getFileName() + " copied to " + targetFolderWithDate;
            }
        } catch (IOException e) {
            message = "Could not move " + image.toString() + "./n=> Reason is: " + e.getMessage();
        }

        return message;
    }

    private Path copyFileToTargetFolder(Path image) throws IOException {
        Path targetFolderWithDate = targetFolder.resolve(year).resolve(year + "-" + month + "-" + day);
        if (Files.notExists(targetFolderWithDate)) {
            Files.createDirectories(targetFolderWithDate);
        }

        Files.copy(image, targetFolderWithDate.resolve(image.getFileName()));
        return targetFolderWithDate;
    }

    private void findDateInformation(Path image) throws IOException {
        extractDateInformationFromExif(image);
        if (year == null) {
            // Exif could not be read, try to guess from the file nam
            guessDateInformationFromFileName(image);
        }
    }

    private void guessDateInformationFromFileName(Path image) {
        String name = image.getFileName().toString();
        Matcher matcher = datePattern.matcher(name);
        if (matcher.matches()) {
            year = matcher.group(1);
            month = matcher.group(2);
            day = matcher.group(3);
        }
    }

    private void extractDateInformationFromExif(Path image) throws IOException {
        try {
            Date originalDate = MetadataReader.on(image).getOriginalDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(originalDate);

            year = Integer.toString(cal.get(Calendar.YEAR));

            int rawMonth = cal.get(Calendar.MONTH) + 1;
            month = (rawMonth < 10) ? "0" + rawMonth : Integer.toString(rawMonth);

            int rawDay = cal.get(Calendar.DAY_OF_MONTH);
            day = (rawDay < 10) ? "0" + rawDay : Integer.toString(rawDay);
        } catch (ImageProcessingException e) {
            // this is not a JPEG, date information has to be guessed differently
        }
    }

}
