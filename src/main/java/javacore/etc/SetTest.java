package javacore.etc;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetTest {

    Set<String> set;

    SetTest(){
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

    /**
     * @author 김우빈
     * @param obj
     * @return 주소 및 값이 같은 검증한 결과 리턴
     * @log equles는 커스텀이 가능하여, 용도에 따라 변환하여 사용할 수 있다.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SetTest testSet = (SetTest)obj;
        return Objects.equals(this.set, testSet.set);
    }

}
