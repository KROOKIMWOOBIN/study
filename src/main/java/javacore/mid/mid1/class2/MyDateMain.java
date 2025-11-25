package javacore.mid.mid1.class2;

public class MyDateMain {
    public static void main(String[] args) {
        MyDate date1 = new MyDate(2025, 11, 13);
        MyDate date2 = date1;
        System.out.println(date1.toString());
        System.out.println(date2.toString());
        date2 = date2.withDay(14);
        System.out.println(date1.toString());
        System.out.println(date2.toString());
    }
}

class MyDate {
    private final int year;
    private final int month;
    private final int day;
    public MyDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public MyDate withYear(int newYear) {
        return new MyDate(newYear, month, day);
    }
    public MyDate withMonth(int newMonth) {
        return new MyDate(year, newMonth, day);
    }
    public MyDate withDay(int newDay) {
        return new MyDate(year, month, newDay);
    }
    @Override
    public String toString() {
        return "year : " + year + ", month : " + month + ", day : " + day;
    }
}
