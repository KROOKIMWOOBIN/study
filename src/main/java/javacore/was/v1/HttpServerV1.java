package javacore.was.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import static javacore.was.Common.*;
import static java.nio.charset.StandardCharsets.*;

public class HttpServerV1 {

    private final int port;

    public HttpServerV1(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("서버 시작 포트 : " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                process(socket);
            }
        }
    }

    private void process(Socket socket) throws IOException {
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), false, UTF_8))
        {
            String requestString = requestToString(reader);
            if(requestString.contains("/favicon.ico")) {
                log("favicon 요청");
                return;
            }
            log("HTTP 요청 정보 출력");
            System.out.println(requestString);
            log("HTTP 응답 생성중...");
            sleep(5);
            responseToClient(writer);
            log("HTTP 응답 전달 완료");
        }
    }

    private void responseToClient(PrintWriter writer) {
        String body = "<h1>Hello World</h1>";
        int length = body.getBytes(UTF_8).length;
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append("\r\n")
                .append("Content-Type: text/html").append("\r\n")
                .append("Content-length: ").append(length).append("\r\n")
                .append("\r\n")
                .append(body);
        log("HTTP 응답 정보 출력");
        System.out.println(sb);
        writer.println(sb);
        writer.flush();
    }

    private static String requestToString(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

}
