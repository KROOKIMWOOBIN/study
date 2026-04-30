<!-- Kafka 공통 용어 -->

*[Kafka]: 이벤트를 topic에 append-only log로 저장하고 consumer group이 offset 기준으로 읽는 분산 이벤트 스트리밍 플랫폼.
*[Topic]: Kafka에서 이벤트를 논리적으로 분류하는 이름.
*[topic]: Kafka에서 이벤트를 논리적으로 분류하는 이름.
*[Partition]: topic을 나누어 저장하는 append-only log 단위. Kafka 순서 보장의 기본 단위다.
*[partition]: topic을 나누어 저장하는 append-only log 단위. Kafka 순서 보장의 기본 단위다.
*[Offset]: partition 안에서 record의 위치를 나타내는 번호.
*[offset]: partition 안에서 record의 위치를 나타내는 번호.
*[Record]: Kafka에 저장되는 메시지 단위. key, value, headers, timestamp를 가진다.
*[record]: Kafka에 저장되는 메시지 단위. key, value, headers, timestamp를 가진다.
*[Broker]: Kafka 서버 한 대.
*[broker]: Kafka 서버 한 대.
*[Cluster]: 여러 broker가 함께 동작하는 Kafka 묶음.
*[cluster]: 여러 broker가 함께 동작하는 Kafka 묶음.
*[Replication]: partition 복제본을 여러 broker에 두는 것.
*[replication]: partition 복제본을 여러 broker에 두는 것.
*[Replica]: partition 복제본.
*[replica]: partition 복제본.
*[Leader]: partition의 읽기와 쓰기를 담당하는 대표 replica.
*[leader]: partition의 읽기와 쓰기를 담당하는 대표 replica.
*[Follower]: leader 데이터를 따라 복제하는 replica.
*[follower]: leader 데이터를 따라 복제하는 replica.
*[ISR]: In-Sync Replicas. leader와 충분히 동기화된 replica 집합.
*[KRaft]: Kafka Raft metadata mode. Kafka가 ZooKeeper 없이 metadata quorum을 관리하는 방식.
*[Retention]: Kafka가 record를 보관하는 기간이나 크기 기준.
*[retention]: Kafka가 record를 보관하는 기간이나 크기 기준.
