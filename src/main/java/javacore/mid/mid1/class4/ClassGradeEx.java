package javacore.mid.mid1.class4;

public class ClassGradeEx {
    public static void main(String[] args) {
        int price = 10000;
        System.out.println(ClassGrade.BRONZE.classGradeDiscount(price));
        System.out.println(ClassGrade.GOLD.classGradeDiscount(price));
        System.out.println(ClassGrade.DIAMOND.classGradeDiscount(price));
    }
}

class ClassGrade { // 타입 안전형 열거형 패턴
    public static final ClassGrade BRONZE = new ClassGrade(10);
    public static final ClassGrade GOLD = new ClassGrade(20);
    public static final ClassGrade DIAMOND = new ClassGrade(30);
    private final int discountPercent;
    ClassGrade(int discountPercent) {
        this.discountPercent = discountPercent;
    }
    public int getDiscountPercent() {
        return discountPercent;
    }
    public int classGradeDiscount(int price) {
        return price * this.getDiscountPercent() / 100;
    }
}