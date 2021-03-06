package fr.bellingard.tools.imagesorter;

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
public class ProgramTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void unexisting_source_folder() throws Exception {
        Path sourceFolder = Paths.get("/foo");
        assertThat(Program.run(sourceFolder, null)).isEqualTo(Program.EXIT_CODE_UNEXISTING_SOURCE_FOLDER);

        sourceFolder = Paths.get(ClassLoader.getSystemResource(getPath("DSC_3220.JPG")).toURI());
        assertThat(Files.exists(sourceFolder)).isTrue();
        assertThat(Program.run(sourceFolder, null)).isEqualTo(Program.EXIT_CODE_UNEXISTING_SOURCE_FOLDER);
    }

    @Test
    public void unexisting_target_folder() throws Exception {
        Path sourceFolder = Paths.get(ClassLoader.getSystemResource(getPath(".")).toURI());
        Path targetFolder = Paths.get("/foo");
        assertThat(Program.run(sourceFolder, targetFolder)).isEqualTo(Program.EXIT_CODE_UNEXISTING_TARGET_FOLDER);

        targetFolder = Paths.get(ClassLoader.getSystemResource(getPath("DSC_3220.JPG")).toURI());
        assertThat(Files.exists(targetFolder)).isTrue();
        assertThat(Program.run(sourceFolder, targetFolder)).isEqualTo(Program.EXIT_CODE_UNEXISTING_TARGET_FOLDER);
    }

    @Test
    public void should_sort_files() throws Exception {
        Path sourceFolder = Paths.get(ClassLoader.getSystemResource(getPath(".")).toURI());
        Path targetFolder = Paths.get(tempFolder.getRoot().getAbsolutePath());

        Program.run(sourceFolder, targetFolder);

        // check that the files were copied
        assertThat(Files.exists(targetFolder.resolve("2013").resolve("2013-05-03").resolve("VID_20130503_123.wav")))
                .isTrue();
        assertThat(Files.exists(targetFolder.resolve("2014").resolve("2014-01-05").resolve("20140105_123456.wav")))
                .isTrue();
        assertThat(Files.exists(targetFolder.resolve("2015").resolve("2015-03-14").resolve("20150314_100153.jpg")))
                .isTrue();
        assertThat(Files.exists(targetFolder.resolve("2015").resolve("2015-07-26").resolve("DSC_3220.jpg")))
                .isTrue();

        // check that there's no other folder elsewhere
        assertThat(Files.list(targetFolder).count()).isEqualTo(4);
        assertThat(Files.list(targetFolder.resolve("non-sorted")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2013")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2014")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2015")).count()).isEqualTo(2);
        assertThat(Files.list(targetFolder.resolve("2013").resolve("2013-05-03")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2014").resolve("2014-01-05")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2015").resolve("2015-03-14")).count()).isEqualTo(1);
        assertThat(Files.list(targetFolder.resolve("2015").resolve("2015-07-26")).count()).isEqualTo(1);
    }

    private String getPath(String fileName) {
        return getClass().getSimpleName() + "/" + fileName;
    }

}
