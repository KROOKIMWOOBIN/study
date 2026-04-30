<!-- Kafka 비교와 선택 기준 용어 -->

*[RabbitMQ]: exchange와 queue 기반 메시지 브로커. 작업 큐, 라우팅, 명령성 메시징에 자주 사용한다.
*[Redis Stream]: Redis 안에서 append-only stream과 consumer group을 제공하는 자료구조.
*[Redis Pub/Sub]: Redis의 실시간 publish/subscribe 기능. 메시지를 보관하지 않는다.
*[SQS]: AWS의 managed message queue. 운영 부담을 줄이고 단순 큐 처리에 적합하다.
*[Outbox Pattern]: DB 변경과 이벤트 발행의 불일치를 줄이기 위해 DB에 outbox row를 저장한 뒤 별도 publisher가 발행하는 패턴.
*[CDC]: Change Data Capture. DB 변경 내역을 이벤트로 캡처하는 방식.
