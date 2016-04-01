package fr.bellingard.tools.imagesorter;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

/**
 *
 */
public class MetadataReader {

    private static final int ORIGINAL_DATE = 36867;

    private Path image;
    private Date originalDate;

    private MetadataReader() {
    }

    public static MetadataReader on(Path image) throws IOException, JpegProcessingException {
        MetadataReader reader = new MetadataReader();
        reader.image = image;
        reader.analyse();
        return reader;
    }

    private MetadataReader analyse() throws IOException, JpegProcessingException {
        Metadata metadata = JpegMetadataReader.readMetadata(image.toFile(), Collections.singletonList(new ExifReader()));

        Collection<ExifSubIFDDirectory> exifSubIFDDirectories = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);
        if (exifSubIFDDirectories != null) {
            exifSubIFDDirectories.stream()
                    .filter(d -> d.containsTag(ORIGINAL_DATE))
                    .forEach(d -> originalDate = d.getDate(ORIGINAL_DATE));
        }

        return this;
    }


    public Optional<Date> getOriginalDate() {
        return Optional.ofNullable(originalDate);
    }
}
