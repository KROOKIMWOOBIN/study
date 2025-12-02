package javacore.etc;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> intList = new ArrayList<>();
        Batch batch = new Batch(intList);
    }
    private static class Batch {
        private List<?> list;
        Batch(List<?> list) {
            this.list = list;
        }
    }
}
