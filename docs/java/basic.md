- 캡슐화
  - 목적 : 내부는 숨기고, 필요한 것만 공개해서 시스템을 안정적으로 유지하는 것
    - 기능
        1. 데이터 보호(무분별한 수정 방지)
        2. 객체 상태를 일관되게 유지
        3. 내부 구현을 숨겨서 변경에 강한 구조
        4. 보안, 안정성 증가
      ```java
      public class Animal {
          
        private String name;
        private int age;
          
        Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }
          
        public void setAge(int age) {
            if(checkAge(age)) {
                this.age = age;
            } else {
                System.out.println("0 이상의 나이로만 설정할 수 있습니다.");
            }
        }
          
        private boolean checkAge(int age) {
            return age >= 0;
        }
      }
      ```
- 다형성
   - 목적 : 코드를 유현하게 만들고, 변경에 강하게 만들고, 재사용성을 높이기 위해서
   - 기능
       1. 코드 확장성이 좋아진다.
       2. 구현을 숨기고 역할만 바라보게 설계가 가능하다.
       3. 한 타입으로 여러 객체를 다룰 수 있어 코드가 단순해 진다.
       4. 중복 코드 제거
- 추상화(Abstract, Interface)
   - 목적 : 구현이 아닌, 역할 중심으로 설계를 하여 변경에 강하고, 확장에 유연한 구조를 만들기 위해
   - 기능
       - OCP(Open-Closed Principle)
           - 오픈 개방 원칙
           - (Open for extension)새로운 기능의 추가나 변경 사항이 생겼을 때, 기존 코드는 확장할 수 있어야 한다.
           - (Closed for modification)기존의 코드는 수정되지 않아야 한다.
```java
  public class Main {
    public static void main(String[] args){
        Animal[] animals = {new Dog(), new Cat()};
        for(Animal animal : animals) {
            animal.move();
            if(animal instanceof Dog) {
                ((Dog) animal).sound();
            }
        }
    }
  }
  interface AnimalInterface { 
    void move();
  }
  class Animal implements AnimalInterface {
    @Override
    public void move() {
        System.out.println("동물이 움직입니다.");
    }
  }
  class Dog extends Animal {
    @Override
    public void move() {
        System.out.println("강아지가 움직입니다.");
    }
    public void sound() {
        System.out.println("멍멍");
    }
  }
  class Cat extends Animal {
    @Override
    public void move() {
        System.out.println("고양이가 움직입니다.");
    }
  }
```