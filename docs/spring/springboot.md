## Spring Boot

### 왜 쓰는가?

<div class="concept-box" markdown="1">

Spring Framework는 강력하지만 XML 설정, 서버 설정, 의존성 버전 관리가 복잡했다. Spring Boot는 이를 **자동화**해 개발자가 비즈니스 로직에 집중하게 한다.

</div>

| 항목 | Spring Framework | Spring Boot |
|------|-----------------|-------------|
| 설정 | XML 또는 Java Config 직접 작성 | Auto Configuration |
| 서버 | 외부 톰캣 설치·배포 | 내장 Tomcat (jar 실행) |
| 의존성 | 버전 직접 관리 | Starter로 검증된 버전 묶음 제공 |
| 시작 방법 | 복잡한 초기 설정 | `start.spring.io`에서 바로 생성 |

### Auto Configuration

<div class="tip-box" markdown="1">

`@SpringBootApplication` 내부에 `@EnableAutoConfiguration`이 있다. classpath에 라이브러리가 있으면 자동으로 Bean을 등록한다.

</div>

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

<div class="warning-box" markdown="1">

| 상황 | 문제 | 해결 |
|------|------|------|
| Auto Configuration 오동작 | 의도치 않은 Bean 자동 등록 | `@SpringBootApplication(exclude = ...)` |
| `ddl-auto: create` 운영 사용 | DB 테이블 초기화됨 | 운영은 반드시 `validate` 또는 `none` |
| application.yml에 비밀번호 평문 저장 | 보안 위험 | 환경 변수 또는 Secret Manager 사용 |
| 모든 설정을 yml에 | 관리 복잡 | Profile별 yml 분리 |

</div>

---

## 내부 동작 원리

### Auto Configuration 메커니즘

`@EnableAutoConfiguration`이 실제로 어떻게 클래스패스를 스캔해 Bean을 자동 등록하는지 단계별로 설명한다.

```
① @SpringBootApplication
      ↓ 포함
   @EnableAutoConfiguration
      ↓ import
   AutoConfigurationImportSelector
      ↓ loadFactoryNames()
   META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
      ↓ 수백 개의 AutoConfiguration 클래스 목록
   예: DataSourceAutoConfiguration, JpaAutoConfiguration, SecurityAutoConfiguration ...
      ↓ @Conditional 조건 평가
   조건 통과한 것만 Bean 등록
```

<div class="concept-box" markdown="1">

**핵심**: Spring Boot 3.x 기준으로 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 파일에 자동 설정 클래스 목록이 있다. (이전 버전은 `spring.factories`)
`AutoConfigurationImportSelector`가 이 파일을 읽어 등록 후보 목록을 만들고, 각 클래스의 `@Conditional` 조건을 검사해 실제 등록 여부를 결정한다.

</div>

### @Conditional 조건 체계

Auto Configuration이 충돌하지 않는 핵심 이유 — 모든 자동 설정은 `@Conditional`로 조건을 건다.

| 어노테이션 | 의미 |
|-----------|------|
| `@ConditionalOnClass` | 특정 클래스가 클래스패스에 있을 때만 등록 |
| `@ConditionalOnMissingBean` | 해당 타입의 Bean이 없을 때만 등록 (개발자가 직접 등록하면 자동 등록 스킵) |
| `@ConditionalOnProperty` | 특정 프로퍼티 값이 설정됐을 때만 등록 |
| `@ConditionalOnWebApplication` | 웹 애플리케이션일 때만 등록 |

```java
// DataSourceAutoConfiguration 내부 (실제 소스 단순화)
@Configuration
@ConditionalOnClass(DataSource.class)          // JDBC 드라이버가 클래스패스에 있을 때
@ConditionalOnMissingBean(DataSource.class)    // DataSource Bean이 없을 때 (직접 정의 시 스킵)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
```

→ `spring-boot-starter-data-jpa`를 추가하면 `HikariCP`가 클래스패스에 들어오고, `DataSourceAutoConfiguration`이 조건을 통과해 `HikariDataSource`가 자동 등록된다.

### SpringApplication.run() 부트스트랩 순서

```
SpringApplication.run(Application.class, args)
  ① 환경(Environment) 준비
     → application.yml 로딩
     → 시스템 환경 변수, 커맨드라인 인수 통합
  ② ApplicationContext 생성
     → 웹 환경 감지 → AnnotationConfigServletWebServerApplicationContext
  ③ ApplicationContext.refresh() 실행
     → @ComponentScan으로 @Component 탐색
     → AutoConfiguration 클래스들 @Conditional 평가 후 Bean 등록
     → 내장 Tomcat 시작 (onRefresh() 단계)
  ④ ApplicationRunner / CommandLineRunner 실행
  ⑤ 준비 완료 → ApplicationReadyEvent 발행
```

### @SpringBootApplication 분해

```java
@SpringBootApplication
// 사실상 아래 세 가지를 합친 것:

@SpringBootConfiguration   // = @Configuration (이 클래스가 설정 클래스임을 명시)
@EnableAutoConfiguration   // Auto Configuration 활성화
@ComponentScan             // 현재 패키지 하위를 컴포넌트 스캔
public class Application { ... }
```

<div class="tip-box" markdown="1">

**내가 직접 Bean을 등록하면?** `@ConditionalOnMissingBean` 덕분에 자동 설정이 스킵된다.
예: 내가 `DataSource` Bean을 직접 정의 → `DataSourceAutoConfiguration`은 조건 실패 → 자동 등록 안 함.
이것이 "Auto Configuration은 개발자 설정에 지지 않는다"는 원칙이다.

</div>
