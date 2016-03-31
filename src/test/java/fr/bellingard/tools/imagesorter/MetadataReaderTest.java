package fr.bellingard.tools.imagesorter;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class MetadataReaderTest {

    @Test
    public void read_original_date_from_image() throws Exception {
        Path image = Paths.get(ClassLoader.getSystemResource(getPath("20150314_100153.jpg")).toURI());

        assertThat(MetadataReader.on(image).getOriginalDate().getTime()).isEqualTo(1426327313000L);
    }

    private String getPath(String fileName) {
        return getClass().getSimpleName() + "/" + fileName;
    }

}
