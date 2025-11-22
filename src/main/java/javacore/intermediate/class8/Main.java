package javacore.intermediate.class8;

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
            service.sendMessage(input);
            System.out.println();
        }
    }
    private enum NetworkErrorCode {
        CONNECT("connectError", "서버 연결 실패"), SEND("sendError", "전송 실패");
        private final String errorCode;
        private final String message;
        private static String address;
        private static String data;
        NetworkErrorCode(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public void setData(String data) {
            this.data = data;
        }
        public void printInfo() {
            System.out.println(address + " " + message + ": " + data);
        }
        public String getErrorCode() {
            return errorCode;
        }
    }

    private static class NetworkClientException extends Exception {

        private final NetworkErrorCode errorCode;

        NetworkClientException(NetworkErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }

    private static class NetworkService {
        public void sendMessage(String data) {
            String address = "http://example.com";
            NetworkClient client = new NetworkClient(address);
            client.initError(data);
            try {
                client.connect();
                client.send(data);
            } catch (NetworkClientException e) {
                e.errorCode.setAddress(address);
                e.errorCode.setData(data);
                System.out.println("[네트워크 오류 발생] 오류 코드: " + e.errorCode.getErrorCode());
                e.errorCode.printInfo();
            }
            client.disconnect();
        }

        private static class NetworkClient {
            private final String address;
            private boolean connectError;
            private boolean sendError;
            public
            NetworkClient(String address) {
                this.address = address;
            }
            public void connect() throws NetworkClientException {
                if(connectError) {
                    throw new NetworkClientException(NetworkErrorCode.CONNECT);
                }
                System.out.println(address + " 서버 연결 성공");
            }
            public void send(String data) throws NetworkClientException {
                if(sendError) {
                    throw new NetworkClientException(NetworkErrorCode.SEND);
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
        }
    }
}


