# 프로젝트 설명

SpringBoot 송금 서비스로 계좌의 등록,삭제 / 입,출금 / 계좌이체 / 거래내역 조회 기능을 제공함.

## 기술 스택

- Java 17
- SpringBoot 3.3.0
- JPA
- QueryDsl
- H2 Database
- Redis
- Docker & Docker Compose

## 프로젝트 설명

- 확장성을 고려한 설계
  - 헥사고날 아키텍처를 사용하여 외부기술과 인프라로부터 핵심 비즈니스 로직을 분리하여 확장성 및 유지보수가 용이하도록 설계함.
- 동시성 이슈
  - Redis & Redisson 라이브러리를 활용하여 분산락 처리.
    - Wait time : 10s (락 대기시간 10초 설정)
    - Lease time : 30s (락 획득시 유효시간 30초 설정)
    - 비즈니스 로직이 만약에 30초 이상 걸리더라도 Redisson에서 제공하는 Watchdog 기능으로 인해 자동으로 락시간을 연장해주어 오류를 방지함.
- API 명세서
  - Swagger 사용 : http://localhost:8080/swagger-ui/index.html
- GlobalExceptionHandler를 이용한 Exception들을 정의하여 에러 발생시 빠르게 확인 및 처리 가능.

## 실행 방법

- docker-compose up --build 명령어 사용