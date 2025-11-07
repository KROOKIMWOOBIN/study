package kwb.study;

import kwb.study.TestSet;

public class Main {
    public static void main(String[] args) {
        TestSet set = new TestSet();
        set.addSet("개");
        set.printSet();
        set.addSet("개");
        set.printSet();
        set.addSet("고양이");
        set.printSet();
    }
}
