package javacore.io.streams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import static java.nio.charset.StandardCharsets.*;

public class PrintStreamEtcMain {

    public static void main(String[] args) throws FileNotFoundException {
        PrintStream ps = new PrintStream(new FileOutputStream("temp/print.txt"), true, UTF_8);
        ps.println("Hello Java");
        ps.println(10);
        ps.println(true);
        ps.printf("hello %s", "world");
        ps.close();
    }

}
