package javacore.was;

import javacore.was.v1.HttpServerV1;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        HttpServerV1 httpServerV1 = new HttpServerV1(12345);
        httpServerV1.start();
    }

}
