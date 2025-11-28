## Object
### 설명
1. 최상위 클래스
2. 모든 클래스를 품어줄 수 있다.
### 예시
```java
public class ObjMain {
    public static void main(String[] args){
        Object[] objects = {new Dog(), new Cat()};
        for(Object obj : objects) {
            obj.sound();
        }
    }
}
class Dog {
    public void sound() {
        System.out.println("멍멍");
    }
}
class Cat{
    public void sound() {
        System.out.println("야옹");
    }
}
```