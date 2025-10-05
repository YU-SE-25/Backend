## 📝 커밋 메시지 작성 규칙

- 작성 방법 : ```[카테고리]``` 작업 내용 요약

|카테고리	|설명|
|--|--|
|추가|	새로운 기능 추가
|수정|	기존 기능 수정, 버그 수정
|삭제|	불필요한 내용 삭제, 기능 삭제
|문서|	코드 편집X, 관련 문서 업로드 할 때
|테스트|	테스트 코드 작업 시
|환경|	빌드, 설정 파일 등 수정, DB 연결 작업

## ⚙️ 개발 환경 정보 (Environment)

| 항목 | 버전 | 비고 |
|------|------|------|
| **Java** | 17 | 프로젝트 소스 컴파일 대상(Java 17 언어 수준) |
| **JDK** | 22.0.2 (Temurin) | 실제 로컬 실행 환경 |
| **Spring Boot** | 3.5.6 | `build.gradle.kts` 기준 |
| **Gradle** | 8.14.3 | Wrapper 사용 (`./gradlew`로 실행) |
| **MySQL** | 8.4.6 | 로컬 DB 또는 Docker 환경 |

---

## 💻 실행 방법

```bash
# 1. 레포지토리 복사
git clone https://github.com/YU-SE-25/Backend.git

# 2. 의존성 다운로드 및 빌드
./gradlew clean build

# 3. 서버 실행
./gradlew bootRun
