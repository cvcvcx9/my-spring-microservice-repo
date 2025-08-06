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
*   **Spring Cloud Gateway**: API 게이트웨이
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

### 4. Docker Compose를 이용한 모든 서비스 실행

모든 마이크로서비스와 Kafka, Zookeeper를 Docker 컨테이너로 한 번에 빌드하고 실행합니다.

1.  프로젝트 루트 디렉토리(`my-spring-microservices`)에서 다음 명령어를 실행합니다:
    ```bash
    docker compose up --build
    ```
    이 명령은 모든 `Dockerfile`을 기반으로 이미지를 빌드하고, `docker-compose.yml`에 정의된 순서대로 컨테이너를 시작합니다.

2.  컨테이너 중지 및 삭제:
    ```bash
    docker compose down
    ```

### 5. 서비스 확인

모든 서비스가 성공적으로 실행되면 다음을 확인할 수 있습니다:

*   **Eureka Server 대시보드:** `http://localhost:8761`
    *   `API-GATEWAY`, `CONFIG-SERVER`, `EUREKA-SERVER`, `NOTIFICATION-SERVICE`, `USER-SERVICE`가 `UP` 상태로 등록되어 있는지 확인합니다.
*   **Config Server 설정 확인:** `http://localhost:8090/user-service/default`
    *   `user-service.yml`의 내용이 JSON 형태로 반환되는지 확인합니다. `jwt.secret`과 같은 암호화된 값은 복호화되어 표시됩니다.
*   **API Gateway를 통한 서비스 접근:**
    *   `user-service` 테스트: `http://localhost:8080/user-service/test` (User Service의 `/users/test` 엔드포인트 호출)
    *   `auth-service` 테스트: `http://localhost:8080/auth-service/auth/test-user-service` (Auth Service의 `/auth/test-user-service` 엔드포인트 호출, 이는 다시 User Service를 호출)
    *   Kafka 메시지 발행 테스트: `http://localhost:8080/user-service/register-user/newuser` (User Service의 `/users/register-user/{username}` 엔드포인트 호출)
        *   이후 `notification-service` 컨테이너의 로그(`docker compose logs -f notification-service`)에서 메시지 수신을 확인합니다.

## 문제 해결

*   **`Connection refused` 오류:** 대상 서비스(Eureka, Config Server 등)가 실행 중인지, 포트가 올바른지, 방화벽이 차단하고 있지 않은지 확인합니다.
*   **`not authorized` 오류 (Config Server):** Git 저장소의 `username`과 `password` (PAT)가 올바른지, 환경 변수가 제대로 설정되었는지, PAT에 충분한 권한이 있는지 확인합니다.
*   **`No encryption for FailsafeTextEncryptor` 오류:** Config Server의 `encrypt.key` 설정이 올바른지, 암호화된 값을 생성할 때 사용한 키와 일치하는지 확인합니다.
*   **`bad padding` 오류:** 암호화된 값이 손상되었거나, 암호화 키가 불일치할 때 발생합니다. 암호화된 값을 다시 생성하여 적용합니다.
*   **`Could not resolve placeholder` 오류:** 해당 플레이스홀더에 해당하는 설정 값이 Config Server의 Git 저장소에 없거나, Config Server가 이를 클라이언트 서비스에 제대로 전달하지 못했을 때 발생합니다.
*   **`YAML parsing error`:** Git 저장소의 `.yml` 파일에 YAML 문법 오류(특히 들여쓰기나 따옴표 누락)가 있는지 확인합니다.
