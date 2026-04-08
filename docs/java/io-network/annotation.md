## Annotation

<div class="concept-box" markdown="1">

==어노테이션(Annotation)==: 코드에 `메타데이터(metadata)`를 붙이는 문법. 컴파일러, 프레임워크, 런타임에 정보를 전달한다.

</div>


### 중요
- @Target
  - 붙어있는 곳 (메서드, 필드, 파라미터 등)
- @Retention
  - 생명주기 (SOURCE[컴파일 시 사라짐], CLASS[class 파일까지], RUNTIME[실행 중 reflection 가능])
- @Repeatable
  - 재사용여부

### 예시
```markdown
@Override
public String toString() {
    return "hello";
}

@Override 자체는 동작을 수행하지 않는다.
하지만 컴파일러에게 "부모 메서드 override 맞는지 검사해라" 라는 메타 정보를 준다.
```

### [Annotation]이 사용되는 3가지 레벨
1. 컴파일 단계
   - @Override
2. 클래스 로딩 / 런타임 단계
   - @Service
   - @Controller
3. Annotation Processor (컴파일 시 코드 생성)
   - @Getter
   - @Setter