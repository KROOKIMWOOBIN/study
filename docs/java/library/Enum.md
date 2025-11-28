## Type-safe Enum Pattern
### 설명
1. Enum 클래스가 만들어지기 전 사용했던 방식이다.
### 예시
```java
public class Grade { 
    private int point;
    private Grade(int point) {
        this.point = point;
    }
    private static final Grade GOLD = new Grade(10);
    private static final Grade DIAMOND = new Grade(20);
    public int getPoint() {
        return this.point;
    }
}
```
## Enum
### 예시
```java
enum Grade {
    GOLD(10), 
    DIAMOND(20);
    private int point;
    Grade(int point) {
        this.point;
    }
    public int getPoint() {
        return this.point;
    }
}
``` 