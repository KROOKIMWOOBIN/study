package etc;

import java.util.Random;

/*
운행 매출 확인

Lv1
정류장 수
승객 요금표 - 어른, 청소년, 아이
정류장 별 탑승 승객
탑승 후 하차 정류장에 따른 추가 요금 - 탑승 후 3정거장 뒤 부터 3정거장 당 100원 추가
정류장 별 탑승, 하차 승객 배열

param
- int[] stations
- int[] price
- int[][][] passengers

기점에서 시작해서 종점까지의 정류장 수 stations, 승객별 요금표 price와 정류장 별 탑승 승객 passengers이 주어질 때,
종점까지 운행을 마친 후 승객을 통해 벌어들인 운행 매출을 리턴하는 함수를 작성하시오.

정류장 수
 - 3 <= stations <= 50

승객 요금표
- 배열 순서대로 어른, 청소년, 아이 요금
- 50 <= price[0] <= 100
  30 <= price[1] <= 80
  10 <= price[2] <= 50

정류장 별 탑승 승객
- 3 <= passengers[i] == stations의 수 <= 50
- 0 <= passengers[i][0] == passengers[i][1] == passengers[i][2] <= 5
- 최소 탑승 성인 승객 수 <= passengers[i][j][0] <= 3
- 최소 탑승 청소년 승객 수 <= passengers[i][j][0] <= 3
- 최소 탑승 아이 승객 수 <= passengers[i][j][0] <= 3
 */
public class DriveSalesMain {
    private final static Random random = new Random();
    private final static int stations = random.nextInt(3, 50 + 1);
    private final static int prices = Price.values().length;
    private final static int[][][] passengers = new int[stations][prices][1];

    public static void main(String[] args) {
        setDrive();
        System.out.println("요금표");
        System.out.println("성인: " + Price.ADULT.price + ", 학생: " + Price.TEEN.price + ", 어린이: " + Price.CHILD.price + "\n");
        System.out.println("인원수");
        System.out.println("성인: " + Price.ADULT.totalUser + ", 학생: " + Price.TEEN.totalUser + ", 어린이: " + Price.CHILD.totalUser + "\n");
        System.out.println("전체 운행 매출: " + getDriveSalesTotal());
    }

    private static void setDrive() {
        for(int i = 0; i < Price.values().length; i++) {
            passengers[0][i][0] = random.nextInt(0, 3 + 1);
        }
        for(int i = 0; i < stations; i++) {
            for(int j = 0; j < prices; j++) {
                passengers[i][j][0] = random.nextInt(0, 5 + 1);
                Price.values()[j].addTotalUser(passengers[i][j][0]);
            }

        }
    }

    private static int getDriveSalesTotal() {
        int totalPrice = 0;
        for(int i = 0; i < stations; i++) {
            for(int j = 0; j < prices; j++) {
                int users = passengers[i][j][0];
                int price = Price.values()[j].price;
                int distance = (stations - 1) - i;
                int extra = 0;
                if (distance > 3) {
                    extra = ((distance - 3) / 3) * 100;
                }
                totalPrice += (users * price) + (users * extra);
            }
        }
        return totalPrice;
    }

    enum Price {
        ADULT("성인", random.nextInt(50, 100 + 1), 0),
        TEEN("학생", random.nextInt(30, 80 + 1), 0),
        CHILD("어린이", random.nextInt(10, 50 + 1), 0);
        private final String name;
        private final int price;
        private int totalUser = 0;
        Price(String name, int price, int totalUser) {
            this.name = name;
            this.price = price;
            this.totalUser = totalUser;
        }
        private void addTotalUser(int totalUser) {
            this.totalUser += totalUser;
        }
    }
}
