package javacore.was;

import javacore.was.v3.HttpServerV3;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        // HttpServerV1 httpServer = new HttpServerV1(12345);
        // HttpServerV2 httpServer = new HttpServerV2(12345);
        HttpServerV3 httpServer = new HttpServerV3(12345);
        httpServer.start();
    }

}
