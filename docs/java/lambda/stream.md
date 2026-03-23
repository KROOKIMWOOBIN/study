## 스트림
- 데이터 흐름 파이프라인
- 컬렉션을 함수형 스타일로 처리
```markdown
list.stream()
[데이터] → 중간연산 → 최종연산
```
| 단계               | 설명    |
| ---------------- | ----- |
| stream()         | 시작    |
| filter/map       | 중간 연산 |
| toList()/collect | 최종 연산 |

## Static Factory Method
- 생성자를 대신하는 정적 메서드

### 왜 사용하는가?
- 이름으로 의미 표현 가능
```markdown
public class Grade {

    private final String name;

    private Grade(String name) {
        this.name = name;
    }

    public static Grade gold() {
        return new Grade("GOLD");
    }
    
    public static Grade silver() {
        return new Grade("SILVER");
    }

}
```

## 내부 반복 VS 외부 반복
| 구분    | 외부 반복 | 내부 반복 |
| ----- | ----- | ----- |
| 제어    | 개발자   | 라이브러리 |
| 병렬 처리 | 어려움   | 쉬움    |
| 코드    | 장황    | 간결    |

### 내부 반복
- 개발자가 직접 반복 제어
```markdown
list.stream().forEach(...)
```

### 외부 반복
- 라이브러리가 반복 수행
```markdown
for (String s : list)
```
