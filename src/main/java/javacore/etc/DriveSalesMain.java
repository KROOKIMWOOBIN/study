package javacore.etc;

import java.util.Random;

/*
LV1. 운행 매출 확인
기점에서 시작해서 종점까지의 정류장 수 stations, 승객별 요금표 price와 정류장 별 탑승 승객 passengers이 주어질 때,
종점까지 운행을 마친 후 승객을 통해 벌어들인 운행 매출을 리턴하는 함수를 작성하시오.

조건
- 0 <= 탑승 인원 <= 5
- 0 <= 하차 인원 <= 3
- 탑승 후 3정거장 뒤 부터 3정거장 당 100원 추가

param
- int stations
- int[] price
- int[][][] passengers

정류장 수
3 <= stations <= 50
승객
passengers[정류장][탑승인원][하차인원]
승객 요금표
50 <= price[0] <= 100 : 어른
30 <= price[1] <= 80 : 청소년
10 <= price[2] <= 50 : 아이

설계 방식: 같은 역에서 타고 내린 후, 더 이상 변화가 없다는 조건부로 아래에 코드 작성 만약 매 번 같은 역에서 탄 사람이 다른 역에서 내릴 수 있다고 전제하면 또 다른 파라미터 필요
 */
public class DriveSalesMain {

    private final static Random random = new Random();
    private static int stations = random.nextInt(3, 51);
    private static int[] price = {random.nextInt(50, 101), random.nextInt(30, 81), random.nextInt(10, 51)};
    private static int[][][] passengers = new int[stations][price.length][2];

    public static void main(String[] args) {
        System.out.println("전체 정거장 수: " + stations);
        System.out.println("[요금제]\n성인: " + price[0] + ", 학생: " + price[1] + ", 어린이: " + price[2]);
        setDrive();
        System.out.println("[전체 금액]\n" + getDriveSalesTotal());
    }

    private static void setDrive() {
        for (int i = 0; i < stations; i++) {
            for (int j = 0; j < price.length; j++) {
                passengers[i][j][0] = random.nextInt(0, 6);
                passengers[i][j][1] = random.nextInt(0, 4);
            }
        }
    }

    private static int getDriveSalesTotal() {
        int total = 0;
        for (int i = 0; i < stations; i++) {
            for (int j = 0; j < price.length; j++) {
                int users = passengers[i][j][0] - passengers[i][j][1];
                total += (users * price[j]) + (users * (100 * (i/3)));
            }
        }
        return total;
    }

}
