package kwb.study;

import static kwb.study.Company.newCompany;

public class Main {
    public static void main(String[] args) {
        newCompany(500);
    }
}

class Company {
    int money;
    private Company(int money) {
        this.money = money;
    }
    public static void newCompany(int money) {
        Company company = new Company(money);
        System.out.println("Company money : " + company.money);
    }
}