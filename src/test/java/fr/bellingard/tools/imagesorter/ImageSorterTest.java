package fr.bellingard.tools.imagesorter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ImageSorterTest {

    @Rule
    public TemporaryFolder targetFolder = new TemporaryFolder();

    private ImageSorter sorter;

    @Before
    public void createImageSorter() {
        sorter = new ImageSorter(Paths.get(targetFolder.getRoot().toURI()));
    }

    @Test
    public void should_move_jpg_to_dest_folder() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(
                getPath("should_move_jpg_to_dest_folder/20150314_100153.jpg")).toURI());

        String message = sorter.handleFile(image);

        assertThat(message).contains("20150314_100153.jpg");
        assertThat(message).contains("copied to");
        assertThat(message).contains("2015/2015-03-14");

        Path copiedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "2015", "2015-03-14", image.getFileName().toString());
        assertThat(Files.exists(copiedImage));
    }

    @Test
    public void should_move_jpg_with_no_obvious_date() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(
                getPath("should_move_jpg_with_no_obvious_date/DSC_3220.JPG")).toURI());

        String message = sorter.handleFile(image);

        assertThat(message).contains("DSC_3220.JPG");
        assertThat(message).contains("copied to");
        assertThat(message).contains("2015/2015-07-26");

        Path copiedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "2015", "2015-07-26", image.getFileName().toString());
        assertThat(Files.exists(copiedImage));
    }

    @Test
    public void should_move_any_timestamped_file() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(
                getPath("should_move_any_timestamped_file/20140105_123456.wav")).toURI());

        String message = sorter.handleFile(image);

        assertThat(message).contains("20140105_123456.wav");
        assertThat(message).contains("copied to");
        assertThat(message).contains("2014/2014-01-05");

        Path copiedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "2014", "2014-01-05", image.getFileName().toString());
        assertThat(Files.exists(copiedImage));
    }

    @Test
    public void should_move_any_timestamped_file_with_prefix() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(
                getPath("should_move_any_timestamped_file/VID_20140105_123456.wav")).toURI());

        String message = sorter.handleFile(image);

        assertThat(message).contains("VID_20140105_123456.wav");
        assertThat(message).contains("copied to");
        assertThat(message).contains("2014/2014-01-05");

        Path copiedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "2014", "2014-01-05", image.getFileName().toString());
        assertThat(Files.exists(copiedImage));
    }

    @Test
    public void should_move_any_timestamped_file_but_wrong_timestamp() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(
                getPath("should_move_any_timestamped_file/20142105_wrong-timestamp.wav")).toURI());

        String message = sorter.handleFile(image);

        assertThat(message).contains("Could not find creation date from Exif or file name. Archiving");
        assertThat(message).contains("20142105_wrong-timestamp.wav");

        Path copiedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "2014", "2014-21-05", image.getFileName().toString());
        assertThat(Files.notExists(copiedImage));

        Path archivedImage = Paths.get(targetFolder.getRoot().getAbsolutePath(),
                "non-sorted", image.getFileName().toString());
        assertThat(Files.exists(archivedImage));
    }

    private String getPath(String fileName) {
        return getClass().getSimpleName() + "/" + fileName;
    }

}
