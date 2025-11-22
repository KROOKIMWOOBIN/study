package javacore.intermediate.class8;

public class CheckedExceptionMain {
    public static void main(String[] args) throws MyCheckedException {
        Service service = new Service();
        service.callCatch();
        System.out.println("정상 종료");
        service.catchThrow();
        System.out.println("정상 종료");
    }

    private static class Service {
        Client client = new Client();
        /**
         * 체크 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                client.call();
            } catch (MyCheckedException e) {
                System.out.println("예외 처리, message: " + e.getMessage());
            }
            System.out.println("정상 흐름");
        }
        /**
         * 체크 예외를 밖으로 던지는 코드
         */
        public void catchThrow() throws MyCheckedException {
            client.call();
        }
    }

    /**
     * throw로 예외를 발생시킨 후 throws로 예외를 밖으로 던질 수 있다.
     */
    private static class Client {
        public void call() throws MyCheckedException {
            // 문제 사항
            throw new MyCheckedException("ex");
        }
    }

    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */
    private static class MyCheckedException extends Exception {
        MyCheckedException(String message) {
            super(message);
        }
    }
}
