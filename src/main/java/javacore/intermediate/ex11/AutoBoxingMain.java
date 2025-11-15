package javacore.intermediate.ex11;

import jakarta.persistence.criteria.CriteriaBuilder;

public class AutoBoxingMain {
    public static void main(String[] args) {
        Integer box;
        int unbox;

        box = 100;
        unbox = box + 5;
        System.out.println(unbox);
    }
}
