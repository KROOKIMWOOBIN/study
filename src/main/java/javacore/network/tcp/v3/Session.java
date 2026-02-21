package javacore.network.tcp.v3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static javacore.util.MyLogger.log;

public class Session implements Runnable {

    private final Socket socket;

    public Session(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String received = input.readUTF();
                log("client -> server : " + received);

                if ("exit".equals(received)) {
                    break;
                }

                String toSend = received + " World!";
                output.writeUTF(toSend);
                log("client <- server : " + toSend);
            }

            log("연결 종료 : " + socket);
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
