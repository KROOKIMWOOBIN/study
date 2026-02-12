package javacore.io.file.copy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateCopyFile {

    private final static int FILE_SIZE = 1024 * 1024 * 2;

    public static void main(String[] args) throws IOException {
        String fileName = "temp/copy.dat";
        long startTime = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] bytes = new byte[FILE_SIZE];
        fos.write(bytes);
        fos.close();
        long endTime = System.currentTimeMillis();
        System.out.println("fileName = " + fileName);
        System.out.println("FILE_SIZE = " + (FILE_SIZE / 1024 / 1024) + "mb");
        System.out.println("Time Taken = " + (endTime - startTime) + "ms");
    }

}
