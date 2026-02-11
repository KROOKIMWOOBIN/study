package javacore.io.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class OldFileMain {

    public static void main(String[] args) throws IOException {
        File file = new File("temp/example.txt");
        File directory = new File("temp/exampleDir");

        System.out.println("File exists = " + file.exists());

        boolean created = file.createNewFile();
        System.out.println("File created = " + created);

        System.out.println("directory.exists() = " + directory.exists());

        boolean mkdir = directory.mkdir();
        System.out.println("Directory created  = " + mkdir);

        // boolean delete = file.delete();
        // System.out.println("file delete = " + delete);

        System.out.println("file.isFile() = " + file.isFile());
        System.out.println("directory.isDirectory() = " + directory.isDirectory());
        System.out.println("file.getName() = " + file.getName());
        System.out.println("directory.getName() = " + directory.getName());
        System.out.println("file.length() = " + file.length());
        System.out.println("directory.length() = " + directory.length());

        File newFile = new File("temp/newExample.txt");
        boolean renamed = file.renameTo(newFile);
        System.out.println("File renamed = " + renamed);

        long lastModified = newFile.lastModified();
        System.out.println("Last Modified = " + new Date(lastModified));
    }

}
