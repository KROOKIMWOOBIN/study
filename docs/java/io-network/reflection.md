> [← 홈](/index.md) · [Java](/java/java.md) · [I/O·네트워크·리플렉션](/java/io-network.md)

## Reflection
- 런타임 시점에 클래스 구조를 분석하고, 동적으로 제어
```markdown
Reflection은 일반적인 정적 호출(컴파일 타임 결정)이 아니라,
실행 중(Runtime)에 클래스 메타정보를 읽고 조작하는 메커니즘이다.
```

## 클래스 메타데이터
- Class 객체는 JVM이 로딩한 타입의 메타정보 컨테이너다.
```markdown
Class<?> clazz = String.class;
Class<?> clazz2 = Class.forName("java.lang.String");
```
### 얻을 수 있는 정보
- 클래스 이름
- 패키지
- 상속 구조
- 구현 인터페이스
- 생성자 목록
- 메서드 목록
- 필드 목록
- 애노테이션 정보

## 메서드 탐색과 동적 호출
```markdown
런타임에 특정 메서드를 찾고
객체에 대해 동적으로 호출
```

### 메서드 탐색
```markdown
Method method = clazz.getDeclaredMethod("hello", String.class);
```
- 첫 번째 파라미터 → 메서드명
- 이후 파라미터 → 타입 목록

### 동적 호출
```markdown
Object result = method.invoke(targetObject, "개발자");
```

### private 메서드 접근
```markdown
method.setAccessible(true);
```
- 접근 제어 무시

### 필드 조회
```markdown
Field field = clazz.getDeclaredField("name");
```

### 값 조회
```markdown
field.setAccessible(true);
Object value = field.get(target);
```

### 값 변경
```markdown
field.set(target, "newName");
```