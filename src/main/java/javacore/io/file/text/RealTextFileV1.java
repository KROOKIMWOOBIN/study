package javacore.io.file.text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.*;

public class RealTextFileV1 {

    private static final String PATH = "temp/hello2.txt";

    public static void main(String[] args) throws IOException {
        String writerString = "abc\n가나다";
        System.out.println("== Write String ==");
        System.out.println(writerString);

        Path path = Path.of(PATH);

        // 쓰기
        Files.writeString(path, writerString, UTF_8);

        // 읽기
        String readString = Files.readString(path, UTF_8);
        System.out.println("== Read String ==");
        System.out.println(readString);

        List<String> lines = Files.readAllLines(path, UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            System.out.println(i + ") " + lines.get(i));
        }

        try (Stream<String> lineStream = Files.lines(path, UTF_8)) {
            lineStream.forEach(System.out::println);
        }

    }

}
