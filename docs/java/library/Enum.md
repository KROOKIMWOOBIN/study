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

### 장점
- 생성자를 `private`로 막아 외부에서 객체 생성 불가
- 정적 상수로 미리 생성된 객체만 사용 가능
- 타입이 `Grade`이기 때문에 다른 타입이 들어올 수 없음

### 단점
- 코드가 번거롭다 -> 상수마다 객체를 직접 만들어야 한다.
- 상수 목록을 자동으로 조회할 수 없다
```markdown
// GOLD, DIAMOND 목록 조회 불가능
```
- switch 문 사용 불가
```markdown
switch(grade) { } // 사용 불편
```
- 직렬화 / 비교 / 이름 관리 기능 부족

## Enum
- `Enum`은 서로 관련된 상수들의 집합을 타입으로 정의하는 특별한 클래스이다.

### 예시
```markdown
enum Grade {
    GOLD(10),
    DIAMOND(20);

    private int point;

    Grade(int point) {
        this.point = point;
    }

    public int getPoint() {
        return this.point;
    }
}

Grade grade = Grade.GOLD;
int point = grade.getPoint();
``` 

### 장점
- 타입 안정성 (Type Safety)
```markdown
Grade grade = Grade.GOLD;
```
- `Enum`은 클래스이다
```markdown
class Grade extends Enum<Grade> {

    public static final Grade GOLD = new Grade("GOLD",0,10);
    public static final Grade DIAMOND = new Grade("DIAMOND",1,20);

}
```
- `Enum`은 싱글톤 객체이다
```markdown
Grade a = Grade.GOLD;
Grade b = Grade.GOLD;

System.out.println(a == b); // true
```
- `Enum`은 여러 유용한 메서드를 제공한다.
```markdown
for (Grade g : Grade.values()) {
    System.out.println(g);
}
```