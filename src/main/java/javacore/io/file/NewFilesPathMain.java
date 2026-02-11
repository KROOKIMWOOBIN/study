package javacore.io.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class NewFilesPathMain {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("temp/..");
        System.out.println("path = " + path);

        // 절대 경로
        System.out.println("path.toAbsolutePath() = " + path.toAbsolutePath());

        // 정규 경로
        System.out.println("path.toRealPath() = " + path.toRealPath());

        Stream<Path> pathStream = Files.list(path);
        List<Path> list = pathStream.toList();
        pathStream.close();
        for (Path p : list) {
            System.out.println((Files.isRegularFile(p) ? "F" : "D") + " | " + p.getFileName());
        }
    }

}
