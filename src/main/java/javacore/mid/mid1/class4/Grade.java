package javacore.mid.mid1.class4;

enum Grade {
    BRONZE(10), GOLD(20), DIAMOND(30);
    private final int discountPercent;
    Grade(int discountPercent) {
        this.discountPercent = discountPercent;
    }
    public int getDiscountPercent() {
        return discountPercent;
    }
    public int gradeDiscount(int price) {
        return price * this.getDiscountPercent() / 100;
    }
}
