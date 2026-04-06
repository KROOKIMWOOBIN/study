> [← 홈](/study/) · [Spring](/study/spring/basic/)

## Spring Boot

### 왜 쓰는가?

Spring Framework는 강력하지만 XML 설정, 서버 설정, 의존성 버전 관리가 복잡했다. Spring Boot는 이를 **자동화**해 개발자가 비즈니스 로직에 집중하게 한다.

| 항목 | Spring Framework | Spring Boot |
|------|-----------------|-------------|
| 설정 | XML 또는 Java Config 직접 작성 | Auto Configuration |
| 서버 | 외부 톰캣 설치·배포 | 내장 Tomcat (jar 실행) |
| 의존성 | 버전 직접 관리 | Starter로 검증된 버전 묶음 제공 |
| 시작 방법 | 복잡한 초기 설정 | `start.spring.io`에서 바로 생성 |

### Auto Configuration

`@SpringBootApplication` 내부에 `@EnableAutoConfiguration`이 있다. classpath에 라이브러리가 있으면 자동으로 Bean을 등록한다.

```markdown
@SpringBootApplication  // @Configuration + @ComponentScan + @EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

예: `spring-boot-starter-data-jpa`가 있으면 `DataSource`, `EntityManagerFactory`, `TransactionManager`를 자동 등록한다.

### Starter

검증된 의존성 묶음. 버전 호환성을 Spring Boot가 관리한다.

```markdown
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'       // MVC + Tomcat
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'  // JPA + Hibernate
    implementation 'org.springframework.boot:spring-boot-starter-security'  // Spring Security
    implementation 'org.springframework.boot:spring-boot-starter-data-redis' // Redis
    implementation 'org.springframework.boot:spring-boot-starter-validation' // Bean Validation
    testImplementation 'org.springframework.boot:spring-boot-starter-test'  // JUnit5 + Mockito
}
```

### application.yml

```markdown
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate   # 운영: validate, 개발: create-drop
    show-sql: false

server:
  port: 8080

logging:
  level:
    com.myapp: DEBUG
```

### Profile — 환경 분리

```markdown
# application-dev.yml (개발)
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
  datasource:
    url: jdbc:h2:mem:testdb

---
# application-prod.yml (운영)
spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:mysql://prod-db:3306/mydb
```

```markdown
// 실행 시 프로파일 지정
java -jar app.jar --spring.profiles.active=prod
```

### 내장 Tomcat

별도 서버 설치 없이 `java -jar app.jar`로 실행 가능하다. Docker 이미지로 만들기도 간단하다.

### @ConfigurationProperties — 설정값 바인딩

```markdown
@ConfigurationProperties(prefix = "app.jwt")
@Component
public class JwtProperties {
    private String secret;
    private long expiration;
    // getter/setter
}
```

```markdown
# application.yml
app:
  jwt:
    secret: my-secret-key
    expiration: 3600000
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| Auto Configuration 오동작 | 의도치 않은 Bean 자동 등록 | `@SpringBootApplication(exclude = ...)` |
| `ddl-auto: create` 운영 사용 | DB 테이블 초기화됨 | 운영은 반드시 `validate` 또는 `none` |
| application.yml에 비밀번호 평문 저장 | 보안 위험 | 환경 변수 또는 Secret Manager 사용 |
| 모든 설정을 yml에 | 관리 복잡 | Profile별 yml 분리 |
