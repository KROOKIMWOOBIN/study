<!-- Redis 메시징 용어 -->

*[Pub/Sub]: publisher가 channel에 메시지를 발행하면 구독 중인 subscriber가 즉시 받는 Redis 메시징 방식.
*[pub/sub]: publisher가 channel에 메시지를 발행하면 구독 중인 subscriber가 즉시 받는 Redis 메시징 방식.
*[publisher]: Pub/Sub에서 메시지를 발행하는 주체.
*[subscriber]: Pub/Sub에서 channel을 구독해 메시지를 받는 주체.
*[Channel]: Pub/Sub 메시지를 주고받는 이름.
*[channel]: Pub/Sub 메시지를 주고받는 이름.
*[Keyspace Notification]: key 만료, 삭제, 변경 같은 이벤트를 Pub/Sub으로 알려주는 Redis 기능.
*[Consumer Group]: Stream 메시지를 여러 consumer가 나누어 읽기 위한 그룹.
*[consumer group]: Stream 메시지를 여러 consumer가 나누어 읽기 위한 그룹.
*[consumer]: Stream이나 메시지 큐에서 메시지를 읽고 처리하는 주체.
*[ACK]: Acknowledgement. 메시지를 정상 처리했음을 알리는 확인 응답.
*[PEL]: Pending Entries List. Stream에서 consumer에게 전달됐지만 아직 ACK되지 않은 메시지 목록.
*[Pending Entries List]: Stream에서 consumer에게 전달됐지만 아직 ACK되지 않은 메시지 목록.
*[pending]: 처리 완료 확인이 아직 끝나지 않은 대기 상태.
*[claim]: 오래 pending 된 메시지를 다른 consumer가 가져와 다시 처리하는 동작.
*[DLQ]: Dead Letter Queue. 반복 실패한 메시지를 격리해 전체 처리를 막지 않게 하는 큐.
*[trim]: Stream이나 List 길이를 제한하기 위해 오래된 항목을 잘라내는 작업.
*[append-only log]: 새 항목을 뒤에 계속 추가하는 로그 구조. Redis Stream과 AOF 설명에 자주 등장한다.
