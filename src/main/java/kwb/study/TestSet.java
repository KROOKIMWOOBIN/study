package kwb.study;

import java.util.HashSet;
import java.util.Set;

public class TestSet {

    Set<String> set;

    TestSet(){
        set = new HashSet<>();
    }

    void addSet(String data) {
        try {
            this.set.add(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void printSet() {
        System.out.println(this.set);
    }

}
