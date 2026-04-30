<!-- Kafka 운영 용어 -->

*[High Watermark]: consumer에게 노출 가능한 안전한 offset 경계.
*[Controller]: Kafka cluster metadata와 leader 선출을 관리하는 역할.
*[controller]: Kafka cluster metadata와 leader 선출을 관리하는 역할.
*[Metadata Quorum]: KRaft 모드에서 cluster metadata를 합의로 관리하는 controller 집합.
*[Rack Awareness]: replica를 서로 다른 rack이나 zone에 분산해 장애 영향을 줄이는 설정.
*[Under Replicated Partition]: follower가 leader를 충분히 따라오지 못해 복제가 부족한 partition.
*[Offline Partition]: leader가 없어 읽기/쓰기가 불가능한 partition.
*[Compaction]: 같은 key의 최신 record 중심으로 log를 정리하는 Kafka 보관 정책.
*[compaction]: 같은 key의 최신 record 중심으로 log를 정리하는 Kafka 보관 정책.
*[Tombstone]: compacted topic에서 key 삭제를 표현하기 위해 value를 null로 보낸 record.
*[tombstone]: compacted topic에서 key 삭제를 표현하기 위해 value를 null로 보낸 record.
*[Backpressure]: downstream 처리 속도가 느릴 때 upstream 생산이나 소비 속도를 제한하는 흐름 제어.
*[backpressure]: downstream 처리 속도가 느릴 때 upstream 생산이나 소비 속도를 제한하는 흐름 제어.
