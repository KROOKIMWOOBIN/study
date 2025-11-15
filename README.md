# 기초부터 시작하는 개발자 생활

## 자바

### 1. 김영한의 실전 자바 - 기본편
- 핵심
  - 캡슐화
  - 다형성
  - 추상화(Abstract, Interface)
- 용어
  - OCP(Open-Closed Principle)
      - 오픈 개방 원칙
      - (Open for extension)새로운 기능의 추가나 변경 사항이 생겼을 때, 기존 코드는 확장할 수 있어야 한다.
      - (Closed for modification)기존의 코드는 수정되지 않아야 한다.
  - 전략 패턴(Strategy Pattern)
    - 알고리즘을 클라이언트의 코드 변경 없이, 쉽게 교체할 수 있다.

### 2. 김영한의 실전 자바 - 중급 1편
- 용어
  - Object
    - 최상위 클래스
    - 모든 클래스를 품어줄 수 있다.
  - 불변 객체 VS 가변 객체
  - String 
    - 불변 객체
    - new 키워드를 사용하지 않은 중복된 문자열은 문자열 풀을 사용한다. (Tip! 문자열 풀은 힙 영역을 사용한다. Tip! 해시 알고리즘을 사용하여 문자열을 빠르게 찾아간다.)
    - 문자열을 더할 때 새로운 인스턴스를 만들어야 한다. 
  - StringBuilder
    - 가변 객체
    - 메서드 체이닝 기법 사용
    - 문자열을 더할 때 새로운 인스턴스를 만들기 싫어 위 클래스를 사용한다.
    - 사이드 이펙트를 방지하기 위해 마지막에 toString()을 사용해 String(불변) 인스턴스에 값을 담아준다.
  - 메서드 체이닝
    - 메서드에서 자기 참조값을 반환하면 연속하여 메서드를 호출할 수 있다.
  - 래퍼 클래스
    - AutoBoxing 지원한다.
    - 기본형보단 속도는 느리다.
  - Class class
    - 클래스의 메타 정보를 가져올 수 있다. Tip! /javacore/intermediate/ex11/ClassMetaMain 참고
    - 주요기능 : 모든 필드, 메서드, 부모, 인터페이스 조회

## 스프링

### 1. 스프링의 핵심 원리 - 기본편
- 용어
  - SOLID
    - SRP(Single Responsibility Principle)
      - 단일 책임 원칙
      - 한 클래스는 하나의 책임만 가져야 한다.
    - OCP(Open/Closed Principle)
      - 개방-폐쇄 원칙
      - 확장에는 열려 있으나, 변경에는 닫혀 있어야 한다.
    - LSP(Liskov Substitution Principle)
      - 리스코프 치환 원칙
      - 인터페이스로 설계한 기능을 위반하지 않아야 한다. (예시 : 자동차를 앞으로 가는 기능을 만들었으면 느리더라도 앞으로 가는 기능이어야 한다.)
    - ISP(Interface segregation Principle)
      - 인터페이스 분리 원칙
      - 클라이언트를 위한 범용 인터페이스보다 여러 개의 인터페이스가 낫다.
    - DIP(Dependency Inversion Principle)
      - 의존관 역전 원칙
      - 구현 클래스에 의존하지 않고 인터페이스를 의존해야 한다.