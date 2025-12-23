# JVM
1. [JVM 개요](#JVM-개요)
   - [역할과 목적](#역할과-목적)
   - [JDK/JRE/JVM 차이](#jdk--jre--jvm-차이)
   - [Java의 플랫폼 독립성 원리](#java의-플랫폼-독립성-원리)
   - [JVM 기반 언어 개요 (Kotlin, Scala 등)](#jvm-기반-언어-개요-kotlin-scala-등)
2. [Java 실행 과정](#java-실행-과정)
   - [.java → .class 컴파일 과정]
   - [Bytecode 구조 개요]
   - [JVM 실행 흐름]
   - [Class Loading]
   - [Linking]
   - [Initialization]
   - [main(String[] args)의 의미와 실행 시점]
3. [클래스 로더]
   - [클래스 로더의 필요성]
   - [Class Loader 종류]
   - [Bootstrap]
   - [Platform (Extension)]
   - [Application]
   - [Delegation Model (위임 모델)]
   - [클래스 중복 로딩 방지 원리]
   - [커스텀 ClassLoader 개념]
4. [메모리 구조]
   - [JVM 메모리 전체 구조 개요]
   - [Method Area (Metaspace)]
   - [클래스 정보]
   - [상수 풀]
   - [Heap]
   - [Young / Old Generation]
   - [Eden / Survivor]
   - [Stack]
   - [Stack Frame 구조]
   - [지역 변수, Operand Stack]
   - [PC Register]
   - [Native Method Stack]
5. [객체 생성과 메모리 할당]
   - [객체 생성 과정 (new)]
   - [힙 메모리 할당 방식]
   - [TLAB]
   - [Bump Pointer]
   - [객체의 생명주기]
   - [Escape Analysis 개요]
   - [Stack Allocation 가능성]
6. [JVM 실행 엔진]
   - [Interpreter]
   - [JIT Compiler]
   - [C1 / C2]
   - [Tiered Compilation]
   - [HotSpot VM 구조]
   - [인라이닝(Inlining)]
   - [루프 최적화, Dead Code Elimination]
7. [Garbage Collection]
   - [GC의 필요성]
   - [Reachability 개념]
   - [Minor GC / Major GC / Full GC]
   - [GC 알고리즘]
   - [Mark & Sweep]
   - [Mark & Compact]
   - [Copying]
   - [Stop-The-World(STW)]
8. [GC 종류]
   - [Serial GC]
   - [Parallel GC]
   - [CMS GC (Deprecated)]
   - [G1 GC]
   - [ZGC / Shenandoah (저지연 GC)]
   - [각 GC의 사용 시나리오 비교]
9. [JVM 옵션과 튜닝]
   - [JVM 옵션 분류]
   - [-X]
   - [-XX]
   - [Heap 크기 조절]
   - [-Xms / -Xmx]
   - [GC 관련 옵션]
   - [GC 로그 분석 기초]
   - [OOM 유형별 원인 분석]
   - [Java heap space]
   - [Metaspace]
   - [GC overhead limit exceeded]
10. [스레드와 동시성(JVM 관점)]
    - [Java Thread vs OS Thread]
    - [JVM 스레드 모델]
    - [Stack 메모리와 스레드 관계]
    - [Context Switching 비용]
    - [synchronized와 JVM]
    - [volatile의 메모리 가시성]
11. [성능 분석과 문제 진단]
    - [성능 이슈의 JVM 원인 유형]
    - [Heap Dump 분석 개요]
    - [Thread Dump 읽는 법]
    - [GC 병목 판단 기준]
    - [실무에서 자주 발생하는 JVM 이슈 사례]
12. [JVM과 운영 환경]
    - [JVM과 OS 메모리 관계]
    - [컨테이너(Docker) 환경에서 JVM]
    - [CPU 코어 수와 JVM 스레드]
    - [서버 JVM vs 클라이언트 JVM]

## JVM 개요

### 역할과 목적 한 문장 요약
- JVM은 자바 바이트코드를 실행하면서 메모리·스레드·보안을 관리하고, OS와 하드웨어 차이를 추상화하여 플랫폼 독립성과 안정적인 실행 환경을 제공하기 위한 런타임이다.

### JDK / JRE / JVM 차이 한 문장 요약
- JVM: 자바 바이트코드를 실행하는 가상 머신
- JRE: 자바 프로그램을 실행하기 위한 환경
- JDK: 자바 프로그램을 개발하기 위한 도구 모음

### Java의 플랫폼 독립성 원리 한 문장 요약
- Java의 플랫폼 독립성은 소스를 OS별로 컴파일하지 않고, 플랫폼 중립적인 바이트코드를 생성한 뒤 각 운영체제에 맞게 구현된 JVM이 이를 실행함으로써 달성된다. 이 과정에서 OS 의존성은 JVM과 표준 라이브러리가 흡수한다.

### JVM 기반 언어 개요 (Kotlin, Scala 등) 한 문장 요약
- JVM 기반 언어는 각기 다른 문법과 패러다임을 제공하지만, 최종적으로 JVM 바이트코드로 컴파일되어 동일한 JVM 위에서 실행되며 Java 생태계와 런타임을 공유한다.

### 역할과 목적

#### 역할
- JVM(Java Virtual Machine)은 자바 프로그램을 실행하기 위한 가상 실행 환경이다.
  핵심 역할은 다음 세 가지로 정리할 수 있다.
1. 바이트코드 실행 (JVM은 자바 바이트코드를 해석하거나(JIT 포함) 실행하는 엔진이다.)
   - 자바 소스는 직접 실행되지 않는다.
   - javac 컴파일러가 .java → .class(Bytecode)로 변환
   - JVM은 이 바이트코드만을 입력으로 받아 실행한다.
2. 메모리 관리 (메모리 할당·해제·정리를 JVM이 책임진다.)
   - 객체 생성 및 배치 (Heap)
   - 메서드, 클래스 정보 관리 (Method Area / Metaspace / )
   - 스레드별 스택 관리
   - Garbage Collection 수행
3. 실행 환경 추상화 (개발자는 이 코드는 리눅스인지 윈도우인지 몰라도 된다.)
   - OS별 시스템 콜 차이 은닉
   - CPU 아키텍처 차이 은닉
   - 스레드, 메모리 모델을 일관되게 제공
#### 목적
- JVM의 목적은 단순히 “자바를 실행한다”가 아니다.
1. 플랫폼 독립성 제공 (의존성은 코드가 아니라 JVM이 가진다.)
   - 자바 코드는 OS에 직접 의존하지 않음
   - OS마다 JVM만 있으면 동일한 .class 실행 가능
2. 안정성과 보안 확보 (C/C++에서 흔한 메모리 오류를 구조적으로 차단)
   - 직접 메모리 접근 불가
   - 포인터 연산 불가
   - Bytecode Verifier
     - 잘못된 타입 접근 방지
     - 스택 오버플로우 구조 검증
3. 성능 최적화의 중앙 집중화 (개발자는 비즈니스 로직에 집중, 성능은 JVM이 책임)
   - JIT 컴파일
   - Hot Code 감지
   - 인라이닝, 루프 최적화
   - CPU 특화 최적화
4. 대규모 서버 환경에 적합한 실행 모델 (웹 서버, WAS, 대규모 백엔드에서 표준 실행 환경이 됨)
   - 멀티스레드 지원
   - 대용량 메모리 관리
   - 다양한 GC 전략 제공
   - 장시간 실행 프로세스 안정성

### JDK / JRE / JVM 차이

#### JVM (Java Virtual Machine) - 실행 엔진 + 메모리 관리자
- .class 바이트코드 실행
- 메모리 관리 (Heap, Stack, GC)
- 스레드 관리
- JIT 컴파일
- OS/하드웨어 추상화

#### JRE (Java Runtime Environment) - 실행만 가능한 환경 <- 현재는 사용하지 않는 개념 왜? 기동으로만 서비스가 돌아가지 않아서
- JVM
- Java 표준 클래스 라이브러리 (java.lang, java.util 등)
- 리소스 파일

#### JDK (Java Development Kit) - 개발 + 실행 + 분석까지 가능한 풀세트 <- 배포 및 개발에는 이 레벨 사용
- JRE 전부
- 컴파일러 (javac)
- 디버거 (jdb)
- 문서 도구 (javadoc)
- 패키징 도구 (jar)
- 기타 개발/진단 툴 (jconsole, jmap, jstack 등)

### Java의 플랫폼 독립성 원리
- Java는 소스를 OS에 맞게 컴파일하지 않고, 중간 산출물인 바이트코드를 생성한 뒤 각 플랫폼별 JVM이 이를 실행함으로써 플랫폼 독립성을 확보한다.
```markdown
Java 소스
 → javac
 → Bytecode (.class)
 → JVM (플랫폼별 구현)
 → OS / Hardware
```

### JVM 기반 언어 개요 (Kotlin, Scala 등)

#### JVM 기반 언어란?
- JVM 기반 언어란, 소스 코드는 다르지만 최종 산출물이 JVM이 실행할 수 있는 바이트코드(.class)로 컴파일되는 언어를 말한다.

#### 왜 JVM 위에 여러 언어가 올라가는가?
1. JVM이 이미 완성된 런타임이기 때문 => 새로운 언어가 이걸 다시 만들 필요 없음
2. Java 생태계를 그대로 사용 가능 => 수십 년 누적된 라이브러리
3. 플랫폼 독립성 자동 확보
   - JVM만 있으면 실행 가능
   - OS/CPU 이식성 확보
```markdown
[ Kotlin / Scala / Groovy ]
            ↓
      JVM Bytecode
            ↓
          JVM
            ↓
      OS / Hardware
```

## [홈으로](#JVM)