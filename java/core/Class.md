## Class (java.lang.Class)
- `Class`는 `JVM`에서 로딩된 클래스의 메타데이터를 표현하는 객체다.
- 자바에서 모든 클래스는 런타임에 Class 객체로 관리된다.
  - .class 파일이 ClassLoader에 의해 JVM에 로딩
  - JVM 내부에 해당 클래스의 메타정보 구조 생성
  - 그 정보를 Class 객체로 접근 가능

### Class 객체가 담고 있는 정보 (메타데이터)
| 메타 정보  | 설명                          |
| ------ | --------------------------- |
| 클래스 이름 | `getName()`                 |
| 패키지    | `getPackage()`              |
| 필드     | `getDeclaredFields()`       |
| 메서드    | `getDeclaredMethods()`      |
| 생성자    | `getDeclaredConstructors()` |
| 부모 클래스 | `getSuperclass()`           |
| 인터페이스  | `getInterfaces()`           |
| 어노테이션  | `getAnnotations()`          |
```markdown
Class<?> clazz = String.class;

System.out.println(clazz.getName());          // java.lang.String
System.out.println(clazz.getSuperclass());    // Object

for (Method method : clazz.getDeclaredMethods()) {
    System.out.println(method.getName());
}
```

### Class 객체 얻는 방법
1. .class
```markdown
Class<String> clazz = String.class;
```
2. getClass()
```markdown
String str = "hello";
Class<?> clazz = str.getClass();
```
3. Class.forName()
```markdown
Class<?> clazz = Class.forName("java.lang.String");
```

### 리플렉션
- 리플렉션은 런타임에 클래스 정보를 분석하고 조작하는 기술이다.
- 컴파일 시점이 아니라 실행 중에 클래스 구조를 확인하고 사용
  - 필드 조회 / 값 변경
  - 메서드 호출
  - 생성자 호출
  - 접근 제어자 무시 (private 접근)

#### 예시 (리플렉션으로 메서드 실행)
```markdown
Class<?> clazz = Class.forName("java.lang.String");

Method method = clazz.getMethod("length");

String str = "hello";
int result = (int) method.invoke(str);

System.out.println(result); // 5
```

#### private 필드 접근
```markdown
Field field = clazz.getDeclaredField("value");
field.setAccessible(true);
-> setAccessible(true) → 접근 제어 무시
```

#### 리플렉션이 사용되는 실제 기술
| 기술         | 사용 이유        |
| ---------- | ------------ |
| Spring DI  | Bean 생성      |
| Spring AOP | 프록시 메서드 호출   |
| JPA        | Entity 매핑    |
| Jackson    | JSON ↔ 객체 변환 |
| Lombok     | 코드 생성        |

#### 리플렉션의 단점
| 문제        | 설명            |
| --------- | ------------- |
| 성능        | 일반 호출보다 느림    |
| 캡슐화 파괴    | private 접근 가능 |
| 컴파일 체크 없음 | 런타임 에러 발생 가능  |

#### 핵심 구조 (JVM 관점)
```markdown
.class 파일
      ↓
ClassLoader
      ↓
JVM Method Area (메타정보 저장)
      ↓
java.lang.Class 객체 생성
      ↓
Reflection으로 접근
```