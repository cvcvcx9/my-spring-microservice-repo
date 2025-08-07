# My Spring Microservices

이 프로젝트는 Spring Boot와 Spring Cloud를 사용하여 마이크로서비스 아키텍처를 구축하는 예제입니다. 서비스 디스커버리(Eureka), 중앙 집중식 설정 관리(Config Server), API Gateway, 서비스 간 통신(FeignClient), 비동기 메시징(Kafka) 등의 핵심 컴포넌트를 포함합니다.

## 프로젝트 구조

이 프로젝트는 Gradle 멀티 모듈 프로젝트로 구성되어 있으며, 각 마이크로서비스는 독립적인 모듈로 관리됩니다.

```
my-spring-microservices/
├── eureka-server/          # 서비스 디스커버리 (Spring Cloud Netflix Eureka Server)
├── config-server/          # 중앙 집중식 설정 관리 (Spring Cloud Config Server)
├── api-gateway/            # API 게이트웨이 (Spring Cloud Gateway)
├── user-service/           # 사용자 관리 서비스 (예시 마이크로서비스)
├── auth-service/           # 인증 서비스 (예시 마이크로서비스, FeignClient 사용)
├── notification-service/   # 알림 서비스 (Kafka Consumer 예시)
├── build.gradle            # 루트 프로젝트 Gradle 빌드 파일
├── settings.gradle         # Gradle 멀티 모듈 설정 파일
└── docker-compose.yml      # 모든 서비스를 Docker 컨테이너로 실행하기 위한 설정
```

## 주요 기술 스택

*   **Spring Boot 3.5.4**
*   **Spring Cloud 2025.0.0** 
*   **Gradle**
*   **Java 17**
*   **Spring Cloud Netflix Eureka Server**: 서비스 디스커버리
*   **Spring Cloud Config Server**: 중앙 집중식 설정 관리 (Git 백엔드)
*   **Spring Cloud Gateway (WebFlux 기반)**: API 게이트웨이
*   **Spring Cloud OpenFeign**: 선언적 REST 클라이언트 (서비스 간 동기 통신)
*   **Spring Kafka**: 비동기 메시징
*   **Docker & Docker Compose**: 컨테이너화 및 오케스트레이션
*   **Spring Boot Actuator**: 모니터링 및 관리 엔드포인트

## 설정 및 실행 방법

### 1. 사전 준비

*   **Java 17 이상** 설치
*   **Gradle** 설치 (또는 `gradlew` 스크립트 사용)
*   **Docker Desktop** 설치 및 실행
*   **Git 저장소 준비:**
    *   Config Server가 참조할 **프라이빗 Git 저장소**를 생성합니다 (예: `https://github.com/cvcvcx9/private-config-settings.git`).
    *   이 저장소에 `user-service.yml` 파일을 생성하고, 필요한 설정(예: `jwt.secret` - **암호화된 값으로**)을 추가합니다.
    *   **주의:** 민감한 정보는 반드시 암호화하여 저장하거나, 환경 변수로 관리해야 합니다.

### 2. 프로젝트 클론 및 초기 설정

1.  이 프로젝트를 클론합니다:
    ```bash
    git clone https://github.com/YOUR_GITHUB_USERNAME/my-spring-microservices.git
    cd my-spring-microservices
    ```
    (만약 기존 `user-service` 레포지토리에서 전환하는 경우, `.git` 디렉토리를 삭제하고 `git init` 후 새 원격 레포지토리를 연결해야 합니다.)

2.  루트 디렉토리에서 Gradle Wrapper를 생성합니다 (Windows):
    ```bash
    gradlew wrapper
    ```
    (Linux/macOS의 경우 `./gradlew wrapper`)

### 3. Config Server 환경 변수 설정

Config Server가 프라이빗 Git 저장소에 접근하고 암호화/복호화를 수행하기 위해 환경 변수를 설정해야 합니다.

*   **IntelliJ IDEA에서 설정하는 경우:**
    *   `Run` -> `Edit Configurations...` -> `ConfigServerApplication` 선택
    *   `Environment variables` 필드 옆의 `...` 버튼 클릭
    *   다음 환경 변수를 추가합니다:
        *   `GIT_USERNAME`: 당신의 GitHub 사용자 이름
        *   `GIT_PASSWORD`: 당신의 GitHub 개인 액세스 토큰 (PAT) - `repo` 스코프 권한 필요
        *   `CONFIG_ENCRYPTION_KEY`: Config Server의 암호화 키 (예: `my-super-secret-encryption-key-12345`)

*   **터미널에서 설정하는 경우 (Windows CMD/PowerShell):**
    ```bash
    set GIT_USERNAME=your_github_username
    set GIT_PASSWORD=your_personal_access_token
    set CONFIG_ENCRYPTION_KEY=my-super-secret-encryption-key-12345
    # 이후 서비스 실행 명령어를 같은 터미널에서 실행
    ```
    (Linux/macOS의 경우 `export` 사용)

### 4. 서비스 빌드 및 실행

모든 마이크로서비스를 빌드하고 순서대로 실행합니다.

1.  **전체 프로젝트 클린 빌드:** 프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다.
    ```bash
    gradlew.bat clean build
    ```

2.  **각 서비스 순서대로 실행:** 각 서비스는 별도의 터미널에서 실행하거나, 백그라운드 실행(`&`)을 사용해야 합니다.
    *   **`eureka-server` 시작:**
        ```bash
        cd eureka-server
        gradlew.bat bootRun
        ```
    *   **`config-server` 시작:**
        ```bash
        cd config-server
        gradlew.bat bootRun
        ```
    *   **`user-service` 시작:**
        *   **참고:** API Gateway 테스트를 위해 `user-service/src/main/java/com/cvcvcx/user_service/controller/AuthController.java` 파일에 `@GetMapping("/api/auth/test")` 엔드포인트가 추가되었습니다.
        ```bash
        cd user-service
        gradlew.bat bootRun
        ```
    *   **`api-gateway` 시작:**
        ```bash
        cd api-gateway
        gradlew.bat bootRun
        ```

### 5. 서비스 확인

모든 서비스가 성공적으로 실행되면 다음을 확인할 수 있습니다:

*   **Eureka Server 대시보드:** `http://localhost:8761`
    *   `API-GATEWAY`, `CONFIG-SERVER`, `EUREKA-SERVER`, `USER-SERVICE`가 `UP` 상태로 등록되어 있는지 확인합니다.
*   **Config Server 설정 확인:** `http://localhost:8090/user-service/default`
    *   `user-service.yml`의 내용이 JSON 형태로 반환되는지 확인합니다. `jwt.secret`과 같은 암호화된 값은 복호화되어 표시됩니다.
*   **API Gateway를 통한 서비스 접근 테스트:**
    *   `user-service` 테스트 엔드포인트 호출: `http://localhost:8080/user-service/api/auth/test`
        *   **예상 결과:** `user-service`에서 반환하는 "Hello from User Service via API Gateway!" 메시지를 받게 될 것입니다.

## 문제 해결

*   **`Cannot decrypt key: jwt.secret` 또는 `bad padding` 오류 (Config Server):**
    *   `CONFIG_ENCRYPTION_KEY` 환경 변수가 `jwt.secret` 값을 암호화할 때 사용한 키와 정확히 일치하는지 확인합니다.
    *   `config-server`가 실행 중인 상태에서 `curl -X POST http://localhost:8090/encrypt -d YOUR_SECRET_KEY_VALUE` 명령어를 사용하여 `jwt.secret`의 원본 값을 다시 암호화하고, 그 결과로 나온 암호화된 문자열을 `config-file/user-service.yml`에 업데이트합니다.
*   **`Connection refused` 오류:** 대상 서비스(Eureka, Config Server 등)가 실행 중인지, 포트가 올바른지, 방화벽이 차단하고 있지 않은지 확인합니다.
*   **`not authorized` 오류 (Config Server):** Git 저장소의 `username`과 `password` (PAT)가 올바른지, 환경 변수가 제대로 설정되었는지, PAT에 충분한 권한이 있는지 확인합니다.
*   **`Could not resolve placeholder` 오류:** 해당 플레이스홀더에 해당하는 설정 값이 Config Server의 Git 저장소에 없거나, Config Server가 이를 클라이언트 서비스에 제대로 전달하지 못했을 때 발생합니다.
*   **`YAML parsing error`:** Git 저장소의 `.yml` 파일에 YAML 문법 오류(특히 들여쓰기나 따옴표 누락)가 있는지 확인합니다.
*   **`Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway.` 오류 (API Gateway):**
    *   `api-gateway/build.gradle`에서 `spring-cloud-starter-gateway` 대신 `spring-cloud-starter-gateway-webflux`를 사용하고, `springCloudVersion`이 `2023.0.3`인지 확인합니다.
    *   `api-gateway/src/main/resources/application.yml`에 `spring.main.web-application-type: reactive`가 설정되어 있고, `spring.cloud.gateway` 관련 설정 키들이 `spring.cloud.gateway.server.webflux`로 시작하도록 변경되었는지 확인합니다.

## TODO 리스트 (MSA 전환)

이 프로젝트를 마이크로서비스 아키텍처로 전환하기 위한 TODO 리스트입니다.

1.  **기존 서버 확인:** `config-server`와 `eureka-server`가 올바르게 설정되어 있는지 확인합니다. (완료)
2.  **`user-service` 통합:** `user-service`를 Eureka 클라이언트 및 Config 클라이언트로 구성합니다. (완료)
3.  **API Gateway 구현:** 새로운 API Gateway 서비스를 생성하고 구성합니다. (완료)
4.  **인증 리팩토링 (선택 사항):** 전용 `auth-service`를 고려합니다.
5.  **다른 마이크로서비스 추가:** 필요에 따라 추가 서비스를 생성합니다.
6.  **중앙 집중식 구성:** `config-server`를 위한 구성 저장소를 설정합니다.
7.  **보안 구현:** JWT 기반 보안을 통합합니다.
8.  **컨테이너화:** 서비스를 Docker로 컨테이너화합니다.

**다음 단계 (사용자님께서 진행하실 부분):**

1.  **빌드 및 실행:**
    *   `eureka-server`를 먼저 시작합니다.
    *   그 다음 `config-server`를 시작합니다.
    *   `user-service`를 시작합니다.
    *   마지막으로 `api-gateway`를 시작합니다.
    *   각 서비스는 `./gradlew bootRun` (또는 Windows의 경우 `gradlew.bat bootRun`) 명령어를 사용하여 실행할 수 있습니다.
2.  **테스트:**
    *   Eureka 대시보드 (`http://localhost:8761`)에서 서비스 등록을 확인합니다.
    *   API Gateway 라우팅을 테스트합니다 (예: `http://localhost:8080/user-service/api/auth/test`).
3.  **중앙 집중식 구성:**
    *   `config-server`의 Git 저장소에 `user-service`가 구성을 가져올 수 있도록 `config-file/user-service.yml` 파일이 있는지 확인합니다.
4.  **추가 개발:**
    *   필요에 따라 더 많은 마이크로서비스를 생성하고, 이들을 Eureka/Config 클라이언트로 구성하며, API Gateway 경로를 추가합니다.
5.  **보안:**
    *   전용 `auth-service`를 사용하여 JWT 기반 보안을 구현하는 것을 고려합니다.
6.  **도커화:**
    *   배포를 위해 Docker를 사용하여 서비스를 컨테이너화합니다.

