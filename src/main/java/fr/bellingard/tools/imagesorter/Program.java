package fr.bellingard.tools.imagesorter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class Program {

    protected static final int EXIT_CODE_WRONG_PARAMS = 1;
    protected static final int EXIT_CODE_UNEXISTING_SOURCE_FOLDER = 2;
    protected static final int EXIT_CODE_UNEXISTING_TARGET_FOLDER = 3;
    protected static final int EXIT_CODE_UNEXPECTED_ERROR = 4;

    public static void main(String[] args) {
        if (args.length != 2) {
            logMessage("2 parameters must be passed:" +
                    "\n- #1: the source folder - where images/videos are located" +
                    "\n- #2: the target folder - where images/videos will be copied");
            System.exit(EXIT_CODE_WRONG_PARAMS);
        } else {
            Path sourceFolder = Paths.get(args[0]);
            Path targetFolder = Paths.get(args[1]);
            int programCode = run(sourceFolder, targetFolder);
            System.exit(programCode);
        }
    }

    protected static int run(Path sourceFolder, Path targetFolder) {
        if (Files.notExists(sourceFolder) || !Files.isDirectory(sourceFolder)) {
            logMessage("Source folder does not exist");
            return EXIT_CODE_UNEXISTING_SOURCE_FOLDER;
        }
        if (Files.notExists(targetFolder) || !Files.isDirectory(targetFolder)) {
            logMessage("Target folder does not exist");
            return EXIT_CODE_UNEXISTING_TARGET_FOLDER;
        }

        try {
            sortFiles(sourceFolder, targetFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return EXIT_CODE_UNEXPECTED_ERROR;
        }

        return 0;
    }

    private static void sortFiles(Path sourceFolder, Path targetFolder) throws IOException {
        ImageSorter sorter = new ImageSorter(targetFolder);
        Files.list(sourceFolder).forEach(file -> sortFile(sorter, file));
    }

    private static void sortFile(ImageSorter sorter, Path file) {
        String message = sorter.handleFile(file);
        logMessage(message);
    }

    private static void logMessage(String message) {
        System.out.println(message);
    }

}
