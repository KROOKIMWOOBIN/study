package javacore.io.streams;

import java.io.*;

public class DataStreamEtcMain {

    public static void main(String[] args) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("temp/data.dat"));
        dos.writeUTF("회원A");
        dos.writeInt(10);
        dos.writeDouble(10.5);
        dos.writeBoolean(true);
        dos.close();

        DataInputStream dis = new DataInputStream(new FileInputStream("temp/data.dat"));
        System.out.println(dis.readUTF());
        System.out.println(dis.readInt());
        System.out.println(dis.readDouble());
        System.out.println(dis.readBoolean());
        dis.close();
    }

}
