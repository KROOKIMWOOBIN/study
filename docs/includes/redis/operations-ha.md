<!-- Redis 운영 구조와 고가용성 용어 -->

*[Standalone]: Redis를 단일 인스턴스로 운영하는 구조. 단순하지만 고가용성은 약하다.
*[Replication]: master 데이터를 replica로 복제하는 구조. Redis 복제는 기본적으로 비동기다.
*[replication]: master 데이터를 replica로 복제하는 구조. Redis 복제는 기본적으로 비동기다.
*[Master-Replica]: 쓰기는 master가 받고 replica가 복제본을 유지하는 구조.
*[master]: 쓰기를 받는 주 Redis 노드.
*[replica]: master 데이터를 복제해 보관하는 Redis 노드. 장애 시 승격 대상이 될 수 있다.
*[Sentinel]: Redis master 장애를 감지하고 failover를 자동화하는 구성 요소.
*[Failover]: 장애 난 master 대신 replica를 새 master로 승격해 서비스를 이어가는 절차.
*[failover]: 장애 난 master 대신 replica를 새 master로 승격해 서비스를 이어가는 절차.
*[Cluster]: hash slot을 기준으로 데이터를 여러 master에 나누어 저장하는 Redis 확장 구조.
*[Redis Cluster]: hash slot을 기준으로 데이터를 여러 master에 나누어 저장하는 Redis 확장 구조.
*[Hash Slot]: Redis Cluster가 key를 배치하는 16384개의 논리 슬롯.
*[hash slot]: Redis Cluster가 key를 배치하는 16384개의 논리 슬롯.
*[Hash Tag]: `{}` 안 문자열만 hash해 여러 key를 같은 slot에 배치하는 방법.
*[hash tag]: `{}` 안 문자열만 hash해 여러 key를 같은 slot에 배치하는 방법.
*[Slot Migration]: Redis Cluster에서 slot을 다른 노드로 옮기는 작업.
*[Resharding]: Redis Cluster에서 slot 배치를 다시 조정해 데이터를 다른 노드로 옮기는 작업.
*[rebalancing]: 노드 간 부하나 slot 분포를 맞추기 위해 데이터를 재배치하는 작업.
*[sharding]: 데이터를 여러 노드나 key로 나누어 저장해 부하와 크기를 분산하는 방식.
*[full resync]: replica가 master의 전체 데이터를 다시 받아 복제를 재구성하는 과정.
*[Managed Redis]: 클라우드 제공자가 운영, 백업, 장애 조치 일부를 관리해주는 Redis 서비스.
