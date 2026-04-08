## 스타일 표현 가이드

노트 작성 시 사용할 수 있는 시각적 표현 수단 목록.
`docs/stylesheets/extra.css` 와 `mkdocs.yml` 의 markdown_extensions 에 의해 지원된다.

---

### 1. 인라인 텍스트 색상

```html
<span class="text-red">위험·금지·오류</span>
<span class="text-blue">핵심 개념·용어</span>
<span class="text-green">권장·정답</span>
<span class="text-orange">주의·경고</span>
<span class="text-purple">추가 정보·팁</span>
<span class="text-gray">보조 설명</span>
```

---

### 2. 하이라이트 (==문법==)

```markdown
==중요한 단어== 또는 ==핵심 문장==
```

노란 배경이 적용된다.

---

### 3. 개념·정보 박스

```markdown
<div class="concept-box" markdown="1">
**핵심**: 내용
</div>

<div class="warning-box" markdown="1">
**주의**: 내용
</div>

<div class="danger-box" markdown="1">
**위험**: 내용
</div>

<div class="success-box" markdown="1">
**권장**: 내용
</div>

<div class="tip-box" markdown="1">
**팁**: 내용
</div>
```

| 클래스 | 색상 | 용도 |
|---|---|---|
| `concept-box` | 파란 | 핵심 개념 정의 |
| `warning-box` | 주황 | 주의해야 할 점 |
| `danger-box` | 빨간 | 위험·금지·안티패턴 |
| `success-box` | 초록 | 권장 방법·정답 |
| `tip-box` | 보라 | 추가 팁·참고 정보 |

> `markdown="1"` 속성을 반드시 붙여야 박스 안에서 마크다운 문법(코드블록, 굵게 등)이 동작한다.

---

### 4. Before / After 비교 그리드

```markdown
<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad — 나쁜 방법**

```java
// 코드 예시
```
</div>
<div class="after" markdown="1">
**Good — 좋은 방법**

```java
// 코드 예시
```
</div>
</div>
```

- `before`: 빨간 상단 테두리 (잘못된 방법)
- `after`: 초록 상단 테두리 (올바른 방법)
- 모바일에서는 자동으로 세로로 전환됨

---

### 5. MkDocs Admonition (기본 제공 박스)

```markdown
!!! note "참고"
    내용

!!! warning "주의"
    내용

!!! danger "위험"
    내용

!!! tip "팁"
    내용

!!! info "정보"
    내용
```

접을 수 있는 박스 (기본 열림):
```markdown
??? note "접을 수 있는 참고"
    내용
```

접을 수 있는 박스 (기본 닫힘):
```markdown
???+ note "기본으로 열린 접기 박스"
    내용
```
