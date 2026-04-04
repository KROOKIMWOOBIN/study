> [← 홈](/study/) · [Java](/java/java/)

# Java 중급 1편

`Object`, 래퍼 클래스, Enum, 중첩 클래스, 예외처리 등 Java 표준 클래스와 언어 메커니즘.

---

## 핵심 클래스

| 주제 | 한 줄 설명 |
|------|-----------|
| [Object](./Object.md) | 모든 클래스의 최상위 부모 — `equals`, `hashCode`, `toString` |
| [불변객체 VS 가변객체](./불변객체VS가변객체.md) | `String`(불변) vs `StringBuilder`(가변) |
| [래퍼 클래스](./래퍼클래스.md) | 기본형(int, boolean...)을 객체로 감싸는 클래스 |
| [Class](./Class.md) | 런타임 클래스 메타정보 (`reflection` 기반) |
| [System Class](./SystemClass.md) | `System.out`, `System.exit`, `System.currentTimeMillis` |
| [Math Class](./Math.md) | `abs`, `max`, `min`, `pow`, `sqrt` 등 수학 유틸리티 |
| Random Class | 난수 생성 |

---

## 언어 메커니즘

| 주제 | 한 줄 설명 |
|------|-----------|
| [Enum](./Enum.md) | 상수 집합을 타입으로 정의하는 특별한 클래스 |
| 날짜와 시간 | `LocalDate`, `LocalDateTime`, `Duration` |
| [중첩/내부클래스](./중첩내부클래스.md) | 정적 중첩 / 내부 / 지역 / 익명 클래스 |
| [예외처리](./예외처리.md) | Checked / Unchecked, `try-with-resources` |
