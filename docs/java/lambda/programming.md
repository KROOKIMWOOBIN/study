## 명령어 프로그래밍 VS 선언적 프로그래밍

### 명령형 (Imperative)
- 어떻게(how) 할지를 직접 기술
- 상태 변화, 반복문, 인덱스 중심
```markdown
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.length() > 3) {
        result.add(s.toUpperCase());
    }
}
```

#### 특징
1. 제어 흐름 직접 관리
2. 가변 상태 많음
3. 코드 길고 버그 가능성↑

### 선언형 (Declarative)
- 무엇(what) 을 원하는지만 표현
- 내부 동작은 라이브러리가 처리
```markdown
list.stream()
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .toList();
```

#### 특징
1. 로직 의도가 명확
2. 내부 구현 숨김
3. 병렬 처리 최적화 쉬움
