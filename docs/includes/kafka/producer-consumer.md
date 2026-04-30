<!-- Kafka Producer/Consumer 용어 -->

*[Producer]: Kafka topic에 record를 발행하는 client.
*[producer]: Kafka topic에 record를 발행하는 client.
*[Consumer]: Kafka topic에서 record를 읽는 client.
*[consumer]: Kafka topic에서 record를 읽는 client.
*[Consumer Group]: 같은 일을 나누어 처리하는 consumer 묶음. group마다 offset을 독립적으로 가진다.
*[consumer group]: 같은 일을 나누어 처리하는 consumer 묶음. group마다 offset을 독립적으로 가진다.
*[Consumer Lag]: consumer group이 아직 처리하지 못한 record 수.
*[consumer lag]: consumer group이 아직 처리하지 못한 record 수.
*[Lag]: consumer group이 아직 처리하지 못한 record 수.
*[lag]: consumer group이 아직 처리하지 못한 record 수.
*[Offset Commit]: consumer group이 다음에 읽을 위치를 Kafka에 저장하는 작업.
*[offset commit]: consumer group이 다음에 읽을 위치를 Kafka에 저장하는 작업.
*[Rebalance]: consumer group 안에서 partition 할당이 다시 나뉘는 과정.
*[rebalance]: consumer group 안에서 partition 할당이 다시 나뉘는 과정.
*[DLQ]: Dead Letter Queue. 계속 처리 실패하는 메시지를 격리하는 topic이나 저장소.
*[Dead Letter Queue]: 계속 처리 실패하는 메시지를 격리하는 topic이나 저장소.
*[Poison Pill]: 특정 메시지가 계속 실패해 consumer 진행을 막는 상황.
*[poison pill]: 특정 메시지가 계속 실패해 consumer 진행을 막는 상황.
*[Idempotence]: 같은 작업을 여러 번 수행해도 결과가 한 번 수행한 것과 같게 만드는 성질.
*[idempotence]: 같은 작업을 여러 번 수행해도 결과가 한 번 수행한 것과 같게 만드는 성질.
*[At-least-once]: 최소 한 번 처리. 유실은 줄이지만 중복 처리가 가능하다.
*[At-most-once]: 최대 한 번 처리. 중복은 줄이지만 유실 가능성이 있다.
*[Exactly-once]: 특정 조건에서 결과 중복을 막는 처리 보장. Kafka transaction과 sink 설계가 함께 필요하다.
