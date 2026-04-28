<!-- Redis 비교와 선택 기준 용어 -->

*[Local Cache]: 애플리케이션 프로세스 내부 캐시. 가장 빠르지만 서버 간 정합성 문제가 생길 수 있다.
*[local cache]: 애플리케이션 프로세스 내부 캐시. 가장 빠르지만 서버 간 정합성 문제가 생길 수 있다.
*[Redis Cache]: 여러 서버가 공유하는 외부 Redis 기반 캐시.
*[Distributed Cache]: 여러 애플리케이션 서버가 함께 사용하는 외부 캐시.
*[Message Broker]: 메시지를 저장하거나 전달해 producer와 consumer를 분리하는 시스템.
*[Message Queue]: 메시지를 큐에 보관했다가 consumer가 가져가 처리하는 메시징 구조.
*[Document DB]: JSON 문서 중심으로 데이터를 저장하는 NoSQL 데이터베이스.
*[NoSQL]: 관계형 테이블 모델 밖의 다양한 데이터 저장소를 통칭하는 말.
*[Memcached]: 단순 key-value 캐시에 집중한 메모리 캐시 시스템.
*[Caffeine]: Java 애플리케이션 내부에서 많이 쓰는 고성능 local cache 라이브러리.
*[Kafka]: partitioned commit log 기반 메시지 플랫폼. 긴 보관, 재처리, 대규모 이벤트 스트림에 강하다.
*[topic]: Kafka에서 메시지를 분류하는 논리 이름.
*[partition]: Kafka topic을 나눠 저장하고 병렬 처리하는 단위.
*[offset commit]: consumer가 어디까지 처리했는지 저장하는 Kafka 처리 위치 기록.
