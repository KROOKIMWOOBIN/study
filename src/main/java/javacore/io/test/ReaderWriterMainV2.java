package javacore.io.test;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javacore.io.test.TextConst.FILE_NAME;

public class ReaderWriterMainV2 {

    public static void main(String[] args) throws IOException {
        String writeString = "ABC";
        System.out.println("write String = " + writeString);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(FILE_NAME), UTF_8);
        osw.write(writeString);
        osw.close();

        InputStreamReader isr = new InputStreamReader(new FileInputStream(FILE_NAME), UTF_8);
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = isr.read()) != -1) {
            sb.append((char) ch);
        }
        isr.close();
        System.out.println("sb = " + sb);
    }

}
