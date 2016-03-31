package fr.bellingard.tools.imagesorter;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;

/**
 *
 */
public class MetadataReader {

    private static final int ORIGINAL_DATE = 36867;

    private Path image;
    private Date originalDate;

    private MetadataReader() {
    }

    public static MetadataReader on(Path image) throws IOException, ImageProcessingException {
        MetadataReader reader = new MetadataReader();
        reader.image = image;
        reader.analyse();
        return reader;
    }

    private MetadataReader analyse() throws IOException, ImageProcessingException {
        Metadata metadata = JpegMetadataReader.readMetadata(image.toFile(), Collections.singletonList(new ExifReader()));

        if (metadata == null) {
            throw new ImageProcessingException("No JPEG metadata found on " + image.toString());
        } else {
            for (Directory directory : metadata.getDirectoriesOfType(ExifSubIFDDirectory.class)) {
                originalDate = directory.getDate(ORIGINAL_DATE);
            }
        }

        return this;
    }

    public Date getOriginalDate() {
        return originalDate;
    }
}
