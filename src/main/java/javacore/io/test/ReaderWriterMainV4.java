package javacore.io.test;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javacore.io.test.TextConst.BUFFER_SIZE;
import static javacore.io.test.TextConst.FILE_NAME;

public class ReaderWriterMainV4 {

    public static void main(String[] args) throws IOException {
        String writeString = "ABC\n가나다";
        System.out.println("== Write String ==");
        System.out.println(writeString);
        BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, UTF_8), BUFFER_SIZE);
        bw.write(writeString);
        bw.close();

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME, UTF_8), BUFFER_SIZE);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        System.out.println("== Read String ==");
        System.out.println(sb);
    }

}
