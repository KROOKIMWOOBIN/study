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

- 클래스의 메타 정보를 가져올 수 있다.
    - 주요기능 : 모든 필드, 메서드, 부모, 인터페이스 조회
    - 클래스의 정보를 읽고 사용하는 것을 리플렉션이라고 한다.