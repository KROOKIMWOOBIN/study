package javacore.network.tcp.v1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static javacore.util.MyLogger.log;

public class ServerV1 {

    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        log("서버 시작");
        ServerSocket serverSocket = new ServerSocket(PORT);
        log("서버 소켓 시작 - 리스닝 포트 : " + PORT);

        Socket socket = serverSocket.accept();
        log("소켓 연결 : " + socket);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        String received = input.readUTF();
        log("client -> server : " + received);

        String toSend = received + " World!";
        output.writeUTF(toSend);
        log("client <- server : " + toSend);

        log("연결 종료 : " + socket);
        input.close();
        output.close();
        socket.close();
        serverSocket.close();
    }

}
