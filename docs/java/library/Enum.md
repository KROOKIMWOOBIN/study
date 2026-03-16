## Type-safe Enum Pattern

### 설명
1. `Enum`이 등장하기 전에 상수 집합을 표현하기 위해 사용하던 패턴
2. 객체를 생성하고 미리 정적 상수(static final) 로 만들어 사용한다.
3. 타입이 int, String 같은 기본 상수보다 타입 안정성(Type Safety) 이 높다.

### 예시
```markdown
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

### 

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