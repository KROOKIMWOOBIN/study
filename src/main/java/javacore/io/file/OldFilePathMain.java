package javacore.io.file;

import java.io.File;
import java.io.IOException;

public class OldFilePathMain {

    public static void main(String[] args) throws IOException {
        File file = new File("temp/..");
        System.out.println("file.getPath() = " + file.getPath());
        System.out.println("file.getAbsolutePath() | 절대 경로 = " + file.getAbsolutePath());
        System.out.println("file.getCanonicalPath() | 정규 경로 = " + file.getCanonicalPath());

        File[] files = file.listFiles();
        for (File f : files) {
            System.out.println((f.isFile() ? "F" : "D") + " | " + f.getName()) ;
        }
    }

}
