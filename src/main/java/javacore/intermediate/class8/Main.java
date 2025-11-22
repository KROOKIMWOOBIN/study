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
    static class NetworkService {
        public void sendMessage(String data) {
            String address = "http://example.com";
            NetworkClient client = new NetworkClient(address);
            client.initError(data);
            String connectResult = client.connect();
            if(isError(connectResult)) {
                System.out.println("[네트워크 오류 발생] 오류 코드: " + connectResult);
            } else {
                String sendResult = client.send(data);
                if(isError(sendResult)) {
                    System.out.println("[네트워크 오류 발생] 오류 코드: " + sendResult);
                }
            }
            client.disconnect();
        }
        private boolean isError(String result) {
            return !"success".equals(result);
        }
        static class NetworkClient {
            private final String address;
            private boolean connectError;
            private boolean sendError;
            public
            NetworkClient(String address) {
                this.address = address;
            }
            public String connect() {
                if(connectError) {
                    System.out.println(address + " 서버 연결 실패");
                    return "connectError";
                }
                System.out.println(address + " 서버 연결 성공");
                return "success";
            }
            public String send(String data) {
                if(sendError) {
                    System.out.println(address + " 서버에 데이터 전송 실패: " + data);
                    return "sendError";
                }
                System.out.println(address + " 서버에 데이터 전송: " + data);
                return "success";
            }
            public void disconnect() {
                System.out.println(address + " 서버 연결 해제");
            }
            public void initError(String data) {
                if("error1".contains(data)) {
                    connectError = true;
                }
                if("error2".contains(data)) {
                    sendError = true;
                }
            }
        }
    }
}


