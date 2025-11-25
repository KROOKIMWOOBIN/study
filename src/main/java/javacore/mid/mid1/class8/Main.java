package javacore.mid.mid1.class8;

import lombok.Getter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NetworkService service = new NetworkService();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("전송할 문자: ");
            String input = scanner.nextLine();
            if("exit".equals(input)) {
                System.out.println("프로그램을 정상 종료합니다.");
                break;
            }
            try {
                service.sendMessage(input);
            } catch (Exception e) {
                exceptionHandler(e);
            }
            System.out.println();
        }
    }
    private static void exceptionHandler(Exception e) {
        System.out.println("죄송합니다. 알 수 없는 오류가 발생하였습니다.");
        System.out.println("==개발자용 디버깅 메시지==");
        e.printStackTrace(System.out);
        if(e instanceof NetworkClientException networkEx) {
            networkEx.printErrorInfo();
        }
    }
}
class NetworkService {
    public void sendMessage(String data) {
        String address = "http://example.com";
        try (NetworkClient client = new NetworkClient(address)){
            client.initError(data);
            client.connect();
            client.send(data);
        }
    }
}
class NetworkClient implements AutoCloseable {
    private final String address;
    private boolean connectError;
    private boolean sendError;
    public NetworkClient(String address) {
        this.address = address;
    }
    public void connect() {
        if(connectError) {
            throw new ConnectException(NetworkErrorCode.CONNECT, address);
        }
        System.out.println(address + " 서버 연결 성공");
    }
    public void send(String data) {
        if(sendError) {
            throw new SendException(NetworkErrorCode.SEND, data);
        }
        System.out.println(address + " 서버에 데이터 전송: " + data);
    }
    public void disconnect() {
        System.out.println(address + " 서버 연결 해제");
    }
    public void initError(String data) {
        if(data.contains("error1")) {
            connectError = true;
        }
        if(data.contains("error2")) {
            sendError = true;
        }
    }

    @Override
    public void close() {
        disconnect();
    }
}

class NetworkClientException extends RuntimeException {
    private final NetworkErrorCode errorCode;
    NetworkClientException(NetworkErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    public void printErrorInfo() {
        System.out.println("[네트워크 에러 발생]" +
                "\nErrorCode: " + errorCode.getErrorCode() +
                "\nErrorMessage: "  + errorCode.getMessage());
    }
}

class ConnectException extends NetworkClientException {
    private final NetworkErrorCode errorCode;
    private final String address;
    ConnectException(NetworkErrorCode errorCode, String address) {
        super(errorCode);
        this.errorCode = errorCode;
        this.address = address;
    }
    @Override
    public void printErrorInfo() {
        System.out.println("[연결 에러 발생]" +
                "\nErrorCode: " + errorCode.getErrorCode() +
                "\nErrorMessage: "  + errorCode.getMessage() +
                "\nAddress: " + address);
    }
}

class SendException extends NetworkClientException {
    private final NetworkErrorCode errorCode;
    private final String data;
    public SendException(NetworkErrorCode errorCode, String data) {
        super(errorCode);
        this.errorCode = errorCode;
        this.data = data;
    }
    @Override
    public void printErrorInfo() {
        System.out.println("[발송 에러 발셍]" +
                "\nErrorCode: " + errorCode.getErrorCode() +
                "\nErrorMessage: " + errorCode.getMessage() +
                "\nData: " + data);
    }
}

@Getter
enum NetworkErrorCode {
    CONNECT("connectError", "서버 연결 실패"),
    SEND("sendError", "전송 실패");

    private final String errorCode;
    private final String message;

    NetworkErrorCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

