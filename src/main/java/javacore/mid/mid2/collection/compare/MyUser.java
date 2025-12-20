package javacore.mid.mid2.collection.compare;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MyUser implements Comparable<MyUser> {
    private String id;
    private int age;
    public MyUser(String id, int age) {
        this.id = id;
        this.age = age;
    }

    @Override
    public int compareTo(MyUser o) {
        return (this.age < o.age ? -1 : (this.age == o.age) ? 0 : 1);
    }
}
