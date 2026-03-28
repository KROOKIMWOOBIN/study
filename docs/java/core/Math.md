> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 1편](/docs/java/core/core.md)

## Math Class
- Java Standard Library에 포함된 수학 연산 전용 유틸리티 클래스
- 모든 기능이 `static`으로 제공되며, 객체 생성 없이 사용

### 왜 사용하는가?
- 수학 연산(절댓값, 제곱, 루트, 난수 등)을 직접 구현 없이 바로 사용하기 위해
- 오버플로우 방지 연산 등 안전한 수치 계산이 필요할 때

### 특이점
- `final` 클래스 → 상속 불가
- 생성자가 `private` → 인스턴스 생성 불가
- 모든 메서드 `static` → 클래스명으로 직접 호출

### 기본 연산 / 비교
| 메서드 | 설명 | 예시 | 결과 |
| --- | --- | --- | --- |
| `Math.abs(x)` | 절댓값 | `Math.abs(-10)` | `10` |
| `Math.max(a, b)` | 최대값 | `Math.max(3, 7)` | `7` |
| `Math.min(a, b)` | 최소값 | `Math.min(3, 7)` | `3` |

### 반올림 / 올림 / 내림
| 메서드 | 설명 | 예시 | 결과 |
| --- | --- | --- | --- |
| `Math.round(x)` | 반올림 (long 반환) | `Math.round(3.6)` | `4` |
| `Math.ceil(x)` | 올림 | `Math.ceil(3.1)` | `4.0` |
| `Math.floor(x)` | 내림 | `Math.floor(3.9)` | `3.0` |

### 거듭제곱 / 루트
| 메서드 | 설명 | 예시 | 결과 |
| --- | --- | --- | --- |
| `Math.pow(a, b)` | a의 b제곱 | `Math.pow(2, 3)` | `8.0` |
| `Math.sqrt(x)` | 제곱근 | `Math.sqrt(16)` | `4.0` |

### 삼각 함수 (라디안 기준)
| 메서드 | 설명 | 예시 | 결과 |
| --- | --- | --- | --- |
| `Math.sin(x)` | 사인 | `Math.sin(Math.PI/2)` | `1.0` |
| `Math.cos(x)` | 코사인 | `Math.cos(0)` | `1.0` |
| `Math.tan(x)` | 탄젠트 | `Math.tan(Math.PI/4)` | `1.0` |

### 로그 / 지수
| 메서드 | 설명 | 예시 | 결과 |
| --- | --- | --- | --- |
| `Math.log(x)` | 자연로그 (ln) | `Math.log(10)` | 약 `2.302` |
| `Math.log10(x)` | 상용로그 | `Math.log10(100)` | `2.0` |
| `Math.exp(x)` | e^x | `Math.exp(1)` | `2.718...` |

### 난수
| 메서드 | 설명 | 범위 |
| --- | --- | --- |
| `Math.random()` | 랜덤 double | `0.0 ≤ x < 1.0` |
```java
int num = (int)(Math.random() * 10); // 0 ~ 9
int range = (int)(Math.random() * (max - min + 1)) + min; // min ~ max
```

### 상수
| 상수 | 설명 | 값 |
| --- | --- | --- |
| `Math.PI` | 원주율 | 3.141592... |
| `Math.E` | 자연상수 | 2.71828... |

### 오버플로우 안전 연산 (실무 중요)
| 메서드 | 설명 |
| --- | --- |
| `Math.addExact(a, b)` | 덧셈 (오버플로우 시 `ArithmeticException`) |
| `Math.subtractExact(a, b)` | 뺄셈 |
| `Math.multiplyExact(a, b)` | 곱셈 |

### 기타 유용 메서드
| 메서드 | 설명 |
| --- | --- |
| `Math.signum(x)` | 부호 반환 (-1.0, 0.0, 1.0) |
| `Math.cbrt(x)` | 세제곱근 |
| `Math.hypot(x, y)` | √(x² + y²) |
| `Math.toRadians(x)` | 도 → 라디안 |
| `Math.toDegrees(x)` | 라디안 → 도 |

### 어떻게 사용하는가?
```java
// 절댓값
int abs = Math.abs(-5); // 5

// 최대/최소
int max = Math.max(10, 20); // 20

// 반올림
long rounded = Math.round(3.7); // 4

// 난수 (0~99)
int random = (int)(Math.random() * 100);

// 오버플로우 안전 연산
try {
    int result = Math.addExact(Integer.MAX_VALUE, 1);
} catch (ArithmeticException e) {
    System.out.println("오버플로우 발생");
}
```

### 어떨 때 많이 쓰는가?
- 알고리즘 문제에서 절댓값, 최대/최솟값 계산 → `abs()`, `max()`, `min()`
- 페이지 계산, 올림/내림이 필요한 경우 → `ceil()`, `floor()`, `round()`
- 간단한 난수 생성 → `random()` (복잡한 경우 `Random` 또는 `ThreadLocalRandom` 사용)
- 금융 계산 등 오버플로우 민감한 연산 → `addExact()` 등
