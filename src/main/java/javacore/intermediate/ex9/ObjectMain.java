package javacore.intermediate.ex9;

public class ObjectMain {
    public static void main(String[] args) {
        Common common = new Common();
        Object[] objects = {new Object(), new Object()};
        System.out.println("배열 사이즈 : " + common.size(objects));

        Object dog = new Dog("진돗개", 5);
        common.print(dog);
        common.printHashcode(dog);

        User user1 = new User("100");
        User user2 = new User("100");
        System.out.println("== Result : " + (user1 == user2));
        System.out.println("equles Result : " + (user1.equals(user2)));
    }
}
// Object 사용하면서 다형적 참조과 오버라이딩 메서드가 가능하다. OCP
class Common {
    // 배열 길이 출력
    public int size(Object[] objects) {
        return objects.length;
    }
    // 객체 출력
    public void print(Object object) {
        System.out.println(object.toString());
    }
    // 객체 Hashcode 출력
    public void printHashcode(Object object) {
        System.out.println(Integer.toHexString(System.identityHashCode(object)));
    }
}
class Dog {
    String name;
    int age;

    Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
class User {
    String id;
    public User(String id) {
        this.id = id;
    }
    @Override
    public boolean equals(Object obj) {
        User user = (User)obj;
        return id.equals(user.id);
    }
}