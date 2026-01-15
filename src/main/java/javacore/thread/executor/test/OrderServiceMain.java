package javacore.thread.executor.test;

import java.util.List;
import java.util.concurrent.*;

import static javacore.thread.util.MyLogger.log;

public class OrderServiceMain {

    public static void main(String[] args) {
        OrderService orderService = new OrderService();
        String orderNumber = "order#9123";
        orderService.order(orderNumber);
    }

    private static class OrderService {

        private final ExecutorService es = Executors.newFixedThreadPool(3);

        private static String orderNumber;

        public void order(String orderNumber) {
            this.orderNumber = orderNumber;

            InventoryWork inventoryWork = new InventoryWork();
            ShoppingWork shoppingWork = new ShoppingWork();
            AccountingWork accountingWork = new AccountingWork();

            List<Future<Boolean>> futures = null;
            try {
                futures = es.invokeAll(List.of(inventoryWork, shoppingWork, accountingWork));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean flag = false;
            for (Future<Boolean> future : futures) {
                try {
                    if (future.get() == flag) {
                        flag = true;
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            if (flag) {
                log("일부 작업이 실패하였습니다.");
                return;
            }
            log("모든 주문 처리가 성공적으로 완료되었습니다.");
            es.shutdown();
        }

        private static class InventoryWork implements Callable<Boolean> {

            @Override
            public Boolean call() throws Exception {
                log("재고 업데이트: " + orderNumber);
                return true;
            }
        }

        private static class ShoppingWork implements Callable<Boolean> {
            @Override
            public Boolean call() throws Exception {
                log("배송 시스템 알림: " + orderNumber);
                return true;
            }
        }

        private static class AccountingWork implements Callable<Boolean> {
            @Override
            public Boolean call() throws Exception {
                log("회계 시스템 업데이트: " + orderNumber);
                return true;
            }
        }

    }

}
