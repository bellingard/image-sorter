package fr.bellingard.tools.imagesorter;

import com.drew.imaging.jpeg.JpegProcessingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class MetadataReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_read_original_date_from_image() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(getPath("20150314_100153.jpg")).toURI());

        assertThat(MetadataReader.on(image).getOriginalDate().get().getTime()).isEqualTo(1426327313000L);
    }

    @Test
    public void try_with_jpg_file_with_no_creation_date() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(getPath("jpg_file_with_no_creation_date.jpg")).toURI());

        assertThat(MetadataReader.on(image).getOriginalDate().isPresent()).isFalse();
    }

    @Test
    public void try_with_no_jpg_file() throws Exception {
        exception.expect(JpegProcessingException.class);

        Path image = Paths.get(ClassLoader.getSystemResource(getPath("no_jpg_file.png")).toURI());

        MetadataReader.on(image);
    }

    private String getPath(String fileName) {
        return getClass().getSimpleName() + "/" + fileName;
    }

}
