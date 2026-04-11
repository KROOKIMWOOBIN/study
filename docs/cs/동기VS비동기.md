# 동기 VS 비동기

<div class="concept-box" markdown="1">

- **동기(Synchronous)**: 요청한 쪽이 결과를 직접 받아야 다음 흐름으로 진행한다.
- **비동기(Asynchronous)**: 요청만 하고, 작업 완료 통지는 콜백/이벤트 등으로 전달된다.
- **블로킹(Blocking)**: 작업이 끝날 때까지 현재 스레드가 멈춰 있다.
- **논블로킹(Non-Blocking)**: 작업이 끝나지 않았더라도 즉시 제어권을 돌려받는다.

</div>

동기/비동기는 **결과를 누가 확인하는가**, 블로킹/논블로킹은 **제어권이 어디에 있는가**의 문제다. 두 개념은 독립적이므로 4가지 조합이 가능하다.

## 코드 예시

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">

**동기 + 블로킹**

```java
// 결과가 올 때까지 현재 스레드가 대기
String result = httpClient.get("https://api.example.com/data");
System.out.println(result); // result가 와야 실행됨
```

</div>
<div class="after" markdown="1">

**비동기 + 논블로킹**

```java
// 요청만 하고 즉시 다음 라인 실행
CompletableFuture<String> future =
    httpClient.getAsync("https://api.example.com/data");
future.thenAccept(result -> System.out.println(result));
System.out.println("요청 후 바로 실행됨"); // 먼저 출력됨
```

</div>
</div>

## 4가지 조합

| 상태 | 상황 | 특징 |
| --- | --- | --- |
| 동기 + 블로킹 | 카운터 앞에서 주문 후 음식 나올 때까지 그 자리에서 대기 | 결과를 직접 기다림, 대기 중 아무것도 못 함 |
| 동기 + 논블로킹 | 주문 후 창구를 반복해서 힐끔힐끔 확인 | 결과는 내가 직접 확인, 대기 중 움직일 수는 있음 |
| 비동기 + 블로킹 | 진동벨을 받았지만 테이블에서 아무것도 안 하고 대기 | 결과는 알림으로 받음, 스스로 행동은 멈춤 |
| 비동기 + 논블로킹 | 진동벨 받고 자리에서 다른 일 하다가 벨 울리면 수령 | 결과는 알림, 대기 중 자유 |

## 장점 / 단점

| 구분 | 장점 | 단점 |
| --- | --- | --- |
| 동기 | 흐름이 직관적, 디버깅 쉬움, 에러 처리 간단 | I/O 대기 중 스레드 낭비 |
| 비동기 | I/O 대기 중 다른 작업 처리 가능, 처리량 향상 | 코드 복잡도 증가, 에러 처리 어려움 |

## 언제 쓰는지

| 방식 | 적합한 상황 |
| --- | --- |
| 동기 | 순서가 중요한 비즈니스 로직, 트랜잭션 처리, 응답을 즉시 사용해야 하는 경우 |
| 비동기 | 이메일 발송, 파일 업로드, 외부 API 호출 등 응답 대기가 긴 작업 |

## 주의할 점

- 비동기 코드는 예외 처리를 별도로 해야 한다. `CompletableFuture`는 예외가 콜백 체인을 타고 흘러 놓치기 쉽다.
- 비동기 논블로킹이 항상 빠른 것은 아니다. 단순 계산 작업은 오히려 동기가 더 빠를 수 있다.
- Spring에서 `@Async`를 사용할 때 <span class="text-orange">같은 클래스 내부 호출은 프록시를 통하지 않아</span> 비동기로 동작하지 않는다.
