# 🎓 EduSphere AI — Student Module Documentation

Welcome to the **EduSphere AI — Student Module**. This comprehensive guide details all features implemented in the Student Portal, the repository folder structure, full system architecture, and how the underlying Text-to-SQL database engine and academic services work.

---

## 📁 Repository Folder Structure

```text
College_management/
├── backend/                                   # Spring Boot Backend Project
│   ├── pom.xml                                # Maven dependencies (Spring Boot 3, Spring Security, JPA, H2, Groq REST)
│   ├── mvnw.cmd                               # Maven Wrapper for Windows
│   └── src/
│       ├── main/
│       │   ├── java/com/edusphere/
│       │   │   ├── ai/                        # AI & Text-to-SQL Core Engine
│       │   │   │   ├── controller/
│       │   │   │   │   └── StudentAiController.java     # Endpoint: /api/v1/student/ai/chat & /history
│       │   │   │   ├── dto/
│       │   │   │   │   ├── AiChatRequest.java
│       │   │   │   │   ├── AiChatResponse.java
│       │   │   │   │   └── StudentAiContext.java
│       │   │   │   ├── enums/
│       │   │   │   │   └── AiIntent.java                # Academic intents (GPA, Internal Marks, etc.)
│       │   │   │   └── service/
│       │   │   │       ├── AcademicAnalysisService.java
│       │   │   │       ├── AiConversationMemoryService.java
│       │   │   │       ├── AiRateLimiterService.java
│       │   │   │       ├── AiRouterService.java
│       │   │   │       ├── GroqService.java             # LLM Text-to-SQL & Synthesis Engine
│       │   │   │       ├── StudentAiService.java        # Main student chat handler
│       │   │   │       ├── StudentContextService.java   # Builds verified student context
│       │   │   │       └── TextToSqlService.java        # Converts natural language -> H2 SQL -> Executes safely
│       │   │   ├── config/
│       │   │   │   ├── DataInitializer.java            # Seed data (Departments, Classes, Marks, Certificates, Risk Alerts)
│       │   │   │   └── SecurityConfig.java             # Spring Security JWT & CORS setup
│       │   │   ├── controller/
│       │   │   │   ├── AiAssistantController.java
│       │   │   │   ├── AuthController.java             # Login & Authentication
│       │   │   │   └── StudentController.java          # Endpoint: /api/v1/student/...
│       │   │   ├── dto/                               # Data Transfer Objects
│       │   │   │   ├── AcademicOverviewResponse.java
│       │   │   │   ├── CertificateDTO.java
│       │   │   │   ├── InternalMarkAnalysisResponse.java
│       │   │   │   ├── SemesterComparisonResponse.java
│       │   │   │   └── StudentProfileDTO.java
│       │   │   ├── entity/                            # JPA Database Entities
│       │   │   │   ├── AcademicRecord.java
│       │   │   │   ├── AcademicRiskAlert.java
│       │   │   │   ├── AcademicYear.java
│       │   │   │   ├── AiChatMessage.java
│       │   │   │   ├── Certificate.java
│       │   │   │   ├── ClassEntity.java
│       │   │   │   ├── Department.java
│       │   │   │   ├── FacultyProfile.java
│       │   │   │   ├── InternalMark.java
│       │   │   │   ├── Notification.java
│       │   │   │   ├── Role.java
│       │   │   │   ├── SemesterMark.java
│       │   │   │   ├── StudentProfile.java
│       │   │   │   ├── Subject.java
│       │   │   │   ├── SubjectAllocation.java
│       │   │   │   └── User.java
│       │   │   ├── repository/                        # Spring Data JPA Repositories
│       │   │   └── service/                           # Core Business Logic Services
│       │   │       ├── AcademicReportPdfService.java  # iText PDF Report Generator
│       │   │       ├── AiAssistantService.java
│       │   │       ├── CertificateService.java        # Certificate upload & verification
│       │   │       ├── StudentAcademicService.java    # Marks & GPA processing
│       │   │       └── coding/                        # LeetCode & GitHub integrators
│       │   │           ├── GitHubService.java
│       │   │           └── LeetCodeService.java
│       │   └── resources/
│       │       └── application.yml                    # H2 DB config, JWT secret, server port 8080
│
└── frontend/                                  # React (Vite) Frontend Application
    ├── package.json                           # Dependencies (React 18, Vite, Lucide-React icons)
    ├── vite.config.js                         # Development server config (Port 5173 / Proxy)
    ├── index.html
    └── src/
        ├── App.jsx                            # Root component & Routing (Role-based dashboards)
        ├── main.jsx                           # React DOM entrypoint
        ├── index.css                          # Modern design system (CSS variables, glassmorphism)
        ├── components/
        │   ├── AiAdvisorChat.jsx              # AI Chat UI with Text-to-SQL Markdown Table Renderer
        │   ├── LoginForm.jsx                  # Student/Faculty/Admin authentication modal
        │   ├── Navbar.jsx                     # Top navigation bar & User profile dropdown
        │   ├── Sidebar.jsx                    # Left navigation drawer with student tabs
        │   ├── StatCard.jsx                   # Metric summary cards (CGPA, Marks, Certificates)
        │   └── StudentDashboard.jsx           # Main Student Hub (Tabs: Overview, Academics, Certificates, AI Chat, Profile)
        └── services/
            └── api.js                         # Axios/Fetch API client with JWT bearer tokens
```

---

## 🚀 Complete Implemented Student Features

The Student Page is structured into **5 major interactive tabs** accessible via the sidebar and top navigation:

### 1. 📊 Student Dashboard Overview Tab
- **Key Metric Stat Cards**:
  - **Cumulative CGPA**: Live CGPA score (e.g. `8.65 / 10.0`) with performance trend tag (`EXCELLENT`, `IMPROVING`, `STABLE`, `NEEDS_ATTENTION`).
  - **Internal Assessment Average**: Average percentage across all subjects (e.g. `93.0%`).
  - **Verified Certificates**: Number of approved certificates vs pending uploads (e.g. `1 Verified / 1 Pending`).
  - **Academic Risk Status**: Real-time risk status (`CLEAR` or `HIGH ADVISORY RISK`).
- **Academic Risk Advisory Warning Banner**:
  - Displays high-risk warnings (e.g. if CGPA falls below threshold or internal marks drop) alongside recommended remediation actions.
- **Student Notifications Feed**:
  - Real-time alerts for mark updates, certificate status verification, and faculty advisory messages.
  - Interactive "Mark as Read" action.
- **Quick PDF Academic Report Download**:
  - One-click PDF download generating an official academic summary transcript.

### 2. 📚 Academic Performance & Marks Breakdown Tab
- **Internal Assessment Marks**:
  - Subject-by-subject breakdown of Internal Test 1, Internal Test 2, Assignments, and Quizzes with obtained marks and maximum marks.
- **Semester Exam Results**:
  - Final semester exam marks, total credit points, letter grades (`A+`, `O`, `B`, etc.), and grade points.
- **Semester Progression & GPA Comparison**:
  - Semester-by-semester GPA progression history showing credit totals and academic growth trend.
- **Strength & Weakness Analysis**:
  - Highlighted badges for **Top Strength Subjects** and **Subjects Needing Attention**.

### 3. 📜 Certificate Verification & Upload Tab
- **Certificate List & Verification Status**:
  - Lists all uploaded certificates (NPTEL, Hackathons, Internships, Online Courses).
  - Badges for verification status:
    - 🟢 `APPROVED` (with faculty reviewer comments)
    - 🟡 `PENDING` (under review by tutor/advisor)
    - 🔴 `REJECTED` (with revision notes)
- **Direct Certificate Submission Form**:
  - Form allowing students to submit title, category, issuing organization, and certificate URL/document link for faculty review.

### 4. 🤖 EduSphere AI Advisor & Text-to-SQL Database Engine Tab
- **Natural Language to SQL Conversion**:
  - Students can type questions in plain natural language (e.g., *"What are my internal marks in Java?"*, *"Show my approved certificates"*, *"Who is the faculty for Computer Networks?"*).
  - The backend converts the query into a safe H2 SQL query, executes it against live database tables, and synthesizes the output into a neat AI-generated Markdown report.
- **Structured Markdown Table & Formatting Renderer**:
  - Displays SQL query results in responsive Markdown tables with striped rows, bold metrics, and code containers.
- **Quick Natural Language SQL Prompts**:
  - Chips for instant queries:
    - *"What are my internal marks?"*
    - *"What is my CGPA & semester GPA?"*
    - *"Show all subjects and credits"*
    - *"List my certificates & status"*
    - *"Who are the IT department faculty?"*
    - *"Compare my semester performance"*

### 5. 👤 Student Profile Tab
- **Academic Profile Details**:
  - Student Name, Register Number, Department, Assigned Class, Tutor Name, Advisor Name, and Current Academic Year.
- **Editable Contact Information**:
  - Update Phone Number, Gender, and Date of Birth with live backend persistence.
- **Coding Profiles Integration**:
  - Displays linked **LeetCode** handle (problems solved by difficulty, contest rating) and **GitHub** handle (public repos, follower count, recent repositories).

---

## 🛠️ How the System Works (Architecture & Data Flow)

### 1. Request Security & Student Data Isolation
Every API request sent from the frontend includes a JWT Bearer Token in the `Authorization` header:

```text
HTTP Header:
Authorization: Bearer <jwt_token>
```

- Spring Security extracts the student's email from the validated token.
- All student queries (profile, marks, certificates, risk alerts, notifications) strictly scope SQL `WHERE` clauses to `LOWER(u.email) = LOWER(:email)`.
- Students can **never** view or alter another student's academic data.

---

### 2. Text-to-SQL AI Database Query Workflow

When a student submits a question in the AI Chat:

```text
┌────────────────────────────────────────────────────────┐
│  Student natural language query                       │
│  e.g., "What are my internal test marks for Sem 3?"    │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│  GroqService / TextToSqlService (Backend)              │
│  - Inspects DB Schema Metadata                        │
│  - Translates natural language prompt into SELECT SQL  │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│  Security Sanitizer & Scoping                          │
│  - Enforces SELECT-only (Blocks DROP/DELETE/INSERT)    │
│  - Injects WHERE email = studentEmail                  │
│  - Appends LIMIT 50                                    │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│  JdbcTemplate Execution on H2 Database                │
│  - Executes SQL query on tables:                       │
│    internal_marks, subjects, student_profiles, users   │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│  AI Synthesis & Response Formatting                   │
│  - Synthesizes raw JSON rows into AI Markdown Report   │
│  - Formats tables, bold key metrics, actionable tips  │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│  Frontend (AiAdvisorChat.jsx)                          │
│  - FormattedMessage component renders Markdown table   │
└────────────────────────────────────────────────────────┘
```

---

## 🗄️ Database Entity Schema (H2 / JPA)

| Entity / Table | Description & Key Columns |
| :--- | :--- |
| `users` | `id`, `full_name`, `email`, `password`, `enabled` |
| `roles` | `id`, `name` (`ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`) |
| `departments` | `id`, `code`, `name`, `description` |
| `classes` | `id`, `name`, `semester`, `section`, `department_id`, `tutor_id`, `advisor_id` |
| `student_profiles` | `id`, `user_id`, `register_number`, `current_semester`, `cgpa`, `department_id`, `current_class_id`, `leetcode_username`, `github_username` |
| `subjects` | `id`, `code`, `name`, `credits`, `semester`, `department_id` |
| `internal_marks` | `id`, `student_id`, `subject_id`, `mark_type`, `marks_obtained`, `max_marks`, `semester` |
| `semester_marks` | `id`, `student_id`, `subject_id`, `semester`, `grade_points`, `letter_grade`, `marks_obtained`, `max_marks` |
| `academic_records` | `id`, `student_id`, `academic_year_id`, `semester`, `gpa`, `total_credits`, `passed_subjects`, `failed_subjects` |
| `certificates` | `id`, `student_id`, `title`, `category`, `issue_organization`, `file_url`, `status`, `reviewer_comments`, `uploaded_at` |
| `academic_risk_alerts` | `id`, `student_id`, `risk_level`, `reason`, `recommended_action`, `resolved` |
| `notifications` | `id`, `student_id`, `title`, `message`, `type`, `is_read`, `created_at` |

---

## 🔌 Primary Student API Endpoints Reference

| HTTP Method | Endpoint Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Authenticate user & receive JWT token |
| `GET` | `/api/v1/student/home` | Overview dashboard stat cards & summary |
| `GET` | `/api/v1/student/profile` | Detailed student profile details |
| `PUT` | `/api/v1/student/profile` | Update phone, gender, and date of birth |
| `GET` | `/api/v1/student/marks/summary` | Subject-wise internal & semester marks breakdown |
| `GET` | `/api/v1/student/marks/internal-analysis` | Detailed internal assessment performance |
| `GET` | `/api/v1/student/marks/semester-comparison` | Semester-by-semester GPA comparison |
| `GET` | `/api/v1/student/certificates/my` | View student certificates & status |
| `POST` | `/api/v1/student/certificates/upload` | Submit new certificate for verification |
| `GET` | `/api/v1/student/alerts/my-risk-alerts` | View active academic advisory risk alerts |
| `GET` | `/api/v1/student/notifications` | View student notifications |
| `PUT` | `/api/v1/student/notifications/{id}/read` | Mark notification as read |
| `POST` | `/api/v1/student/ai/chat` | AI Chat & Text-to-SQL natural language query |
| `GET` | `/api/v1/student/ai/history` | Retrieve student AI chat history |
| `GET` | `/api/v1/student/report/download-pdf` | Download official academic PDF report |

---

## 💻 Running the Application Locally

### Prerequisites
- **Java JDK 17+**
- **Node.js 18+** & **npm**

### 1. Start Spring Boot Backend
From the root directory:
```bash
./mvnw.cmd spring-boot:run -f backend/pom.xml
```
*Backend runs on:* `http://localhost:8080` (H2 console enabled at `http://localhost:8080/h2-console`)

### 2. Start React Frontend
From the `frontend` directory:
```bash
cd frontend
npm install
npm run dev
```
*Frontend runs on:* `http://localhost:5173`

### Default Student Credentials (Pre-seeded):
- **Email**: `student@edusphere.edu`
- **Password**: `student123`
