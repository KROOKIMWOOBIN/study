## Sysyem Class
- System 클래스는 JVM과 운영체제 수준의 시스템 기능을 접근하기 위한 유틸리티 클래스다.

- 표준 입출력, 오류 스트림
    - 시간 측정
    - 환경 변수
        - 예시) System.getEnv();
    - 배열 고속 복사
        - 예시) System.arraycopy();
        - 메모리 블록 단위로 이동하여 빠름
    - 시스템 속성
        - 예시) Java version, properties
    - 프로그램 종료
        - 예시) System.exit(0);