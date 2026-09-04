# 🎓 EduSphere AI — Academic Intelligence Platform

EduSphere AI is a modular academic management and intelligence platform designed for colleges. The system combines traditional academic management with AI-powered insights, personalized academic assistance, performance analysis, certificate management, and conversational interfaces.

---

## 🎯 Phase 1 — Project Setup (Completed)

Phase 1 establishes the core backend foundation, Maven dependencies, environment variable mapping for PostgreSQL, development profiles, and project structure.

### 🛠️ Technology Stack
- **Java**: OpenJDK 17 / 21
- **Framework**: Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Spring Security, Spring Validation)
- **Database**: PostgreSQL (Production/Staging), H2 (In-Memory Dev Fallback)
- **Build Tool**: Apache Maven
- **Utilities**: Lombok, JWT (jjwt 0.11.5)

---

## ⚙️ Environment Configuration

PostgreSQL database connections are configured using environment variables in `src/main/resources/application.properties`:

| Environment Variable | Description | Default Fallback |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://localhost:5432/eduspheredb` |
| `SPRING_DATASOURCE_USERNAME` | Database User Name | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database User Password | `postgres` |
| `SPRING_PROFILES_ACTIVE` | Active Spring Profile (`dev` / `prod`) | `dev` |
| `JWT_SECRET` | Secret Key for Signing JWT Tokens | High-Entropy Secret Key |
| `APP_UPLOAD_DIR` | Storage directory for certificates | `./uploads/certificates` |

---

## 🚀 Quick Start & Building

### 1. Clean & Build Package
Navigate to the `backend/` directory and execute:
```bash
./mvnw clean install
```
*(On Windows Command Prompt / PowerShell, use `./mvnw.cmd clean install`)*

### 2. Run Application
```bash
./mvnw spring-boot:run
```
*(Or `./mvnw.cmd spring-boot:run`)*

---

## 🗺️ Project Roadmap

- [x] **Phase 1 — Project Setup** (Spring Boot, PostgreSQL Environment Config, Maven, Profiles, `.gitignore`)
- [ ] **Phase 2 — Authentication & Security** (User, Role, JWT, BCrypt, Security Config)
- [ ] **Phase 3 — Department Isolation** (Department Admin & Data Scoping)
- [ ] **Phase 4 — Academic Structure** (Academic Years, Classes, Subjects)
- [ ] **Phase 5 — Faculty Management**
- [ ] **Phase 6 — Faculty Allocation** (Faculty → Subject → Class → Academic Year)
- [ ] **Phase 7 — Tutor Management** (Tutor Assignment & Tutor Portal)
- [ ] **Phase 8 — Advisor Management** (Advisor Assignment & Class Analytics)
- [ ] **Phase 9 — Student Management**
- [ ] **Phase 10 — Internal Marks** (Internal 1, 2, 3 Entry & Validation)
- [ ] **Phase 11 — Semester Marks & CGPA Logic** (GPA/CGPA Calculation Engine)
- [ ] **Phase 12 — Certificate Management** (Multipart Uploads, Verification Workflow)
- [ ] **Phase 13 — Dashboards & Analytics** (Department, Advisor, Tutor Dashboards)
- [ ] **Phase 14 — Student Promotion** (Archive Records & Academic History)
- [ ] **Phase 15 — AI Intelligence Layer** (SQL Agent, RAG, Academic Advisor, Early Warning System)
- [ ] **Phase 16 — WhatsApp Integration** (Twilio Webhook & Communication Layer)