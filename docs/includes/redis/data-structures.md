<!-- Redis 자료구조 용어 -->

*[String]: Redis의 가장 기본 자료구조. 단일 값, JSON 문자열, 카운터, 토큰 저장에 많이 쓴다.
*[List]: 삽입 순서를 유지하는 목록 자료구조. 간단한 queue나 최근 N개 보관에 쓴다.
*[Set]: 중복 없는 집합 자료구조. 포함 여부, 태그, 좋아요 사용자 목록에 적합하다.
*[Sorted Set]: member마다 score를 붙여 정렬하는 집합. 랭킹, 우선순위, 시간 정렬에 자주 쓴다.
*[Hash]: key 하나 아래 여러 field-value를 저장하는 자료구조. 객체 일부 필드 갱신에 유용하다.
*[Bitmap]: bit 단위로 boolean 값을 저장하는 방식. 출석 체크처럼 대량 true/false 저장에 효율적이다.
*[HyperLogLog]: 고유 개수를 근사치로 추정하는 자료구조. 정확한 목록은 필요 없고 UV 같은 수치만 필요할 때 쓴다.
*[Stream]: Redis의 append-only 메시지 로그 자료구조. ID가 붙은 메시지를 저장하고 consumer group으로 나눠 읽을 수 있다.
*[Geospatial]: 위치 좌표를 저장하고 거리 기반 검색을 하는 Redis 기능.
*[Geospatial Index]: 좌표 기반 검색을 위한 Redis 기능. 주변 매장 찾기 같은 반경 검색에 쓴다.
*[Cardinality]: 한 key나 집합 안에 들어 있는 원소 수. 너무 커지면 big key 위험이 있다.
*[field]: Redis Hash 안에서 하나의 속성을 가리키는 이름.
*[member]: Set, Sorted Set, Geospatial 안에 들어가는 개별 원소.
*[score]: Sorted Set에서 member를 정렬하는 숫자 값.
*[offset]: Bitmap에서는 특정 bit 위치, Kafka나 Stream에서는 메시지 위치를 가리키는 번호.
*[UV]: Unique Visitor. 중복을 제거한 방문자 수.
