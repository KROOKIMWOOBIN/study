package javacore.mid.mid1.class8;

public class UncheckedExceptionMain {

    public static void main(String[] args) {
        Service service = new Service();
        service.callCatch();
        System.out.println("정상 종료");
        service.callThorw();
        System.out.println("정상 종료");
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면, 자동으로 밖에 던진다.
     */
    static class Service {
        Client client = new Client();
        /**
         * 필요한 경우 예외를 잡아서 처리할 수 있다.
         */
        public void callCatch() {
            try {
                client.call();
            } catch (MyUncheckedException e) {
                System.out.println("예외 처리, message: " + e.getMessage());
            }
            System.out.println("정상 로직");
        }
        public void callThorw() {
            client.call();
        }
    }

    static class Client {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
    /**
     * RunTimeException 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        MyUncheckedException(String message) {
            super(message);
        }
    }
}
