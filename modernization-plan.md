# Legacy Banking App — Modernization Plan

## Top-Level Overview

Modernize the existing Java 8 Swing desktop banking application into a 3-tier web application while keeping the legacy source code and `legacybank.db` **completely untouched**. All new code lives under `modern/`.

**Goal:** A working web app (React + TypeScript frontend, Java 21 + Spring Boot backend, existing SQLite database) that replicates 100% of the legacy business rules, with a clean demo-ready dashboard.

**Scope:**
- Analyze the legacy app and create GitHub Epics + Stories in the user's `origin` repository
- Build the modernized application Story by Story
- Validate, test, and create a Pull Request to `main`

**Out of scope (by default):** Database migration (SQLite is kept as-is). PostgreSQL migration is optional and must be explicitly requested.

**Guardrails:**
- Never modify legacy source files, `pom.xml`, or `legacybank.db`
- Never push to the facilitator's repository — only push to `origin`
- All new code under `modern/`
- Do not commit `target/` or `legacybank.db`

---

## Legacy Application Summary (read-only reference)

| Aspect | Detail |
|--------|--------|
| Java version | 8 (desktop Swing, JDBC, no layers) |
| Database | SQLite `legacybank.db` — 5 tables: EMPLOYEES, CUSTOMERS, ACCOUNTS, TRANSACTIONS, REFERENCE_SEQUENCE |
| Login | `admin` / `admin123` (plain text in DB) |
| Features | Login, Customer Search, Account Details, Deposit, Withdrawal, Fund Transfer, Transaction History |
| Business rules | Positive amounts ≤ $10M, max 2 decimals, account must be ACTIVE, sufficient funds, atomic transfers |
| Demo data | 1 employee, 10 customers, 15 accounts, 7 transactions |

---

## Implementation Sequence

```
Stage 1 (Plan)   →  GitHub Epics + Stories created
Stage 2 (Implement, in order):
  Sub-task 1: Database / Schema (read SQLite, write init.sh for modern/ layer)
  Sub-task 2: Backend — Spring Boot project scaffold + DB connectivity
  Sub-task 3: Backend — Auth API (JWT)
  Sub-task 4: Backend — Customer & Account APIs
  Sub-task 5: Backend — Transaction APIs (Deposit, Withdrawal, Transfer, History)
  Sub-task 6: Frontend — React + TypeScript scaffold (Vite)
  Sub-task 7: Frontend — Auth screens (Login)
  Sub-task 8: Frontend — Dashboard + navigation
  Sub-task 9: Frontend — Customer Search + Account Detail screens
  Sub-task 10: Frontend — Transaction screens (Deposit, Withdrawal, Transfer, History)
  Sub-task 11: Testing — Backend JUnit 5 + Frontend Vitest
  Sub-task 12: DevOps — Docker Compose + README
Stage 3 (Validate) →  End-to-end validation + Pull Request
```

---

## Sub-Tasks

---

### Sub-Task 1 — Stage 1: Analyze Legacy App and Create GitHub Epics + Stories

**Status:** `[x] done`

**Intent:**
Analyze the full legacy application (already done — see summary above), then create GitHub Issues in the user's `origin` repository: one Epic per component (Backend, Frontend, Database, DevOps) and granular Stories linked to their Epic. Each Story has acceptance criteria. Present the full plan to the user and ask design questions before creating Issues.

**Design Questions to Ask:**
1. **Database:** Keep SQLite (default) or migrate to PostgreSQL?
2. **Auth:** JWT stateless (recommended) or session-based?
3. **UI library:** Carbon Design System (`@carbon/react`, recommended for modern IBM look) or plain Tailwind CSS?
4. **Docker:** Include Docker Compose in scope?

**Expected Outcomes:**
- 4 GitHub Epics created: `Database`, `Backend`, `Frontend`, `DevOps`
- 23+ Stories created as GitHub Issues, each linked to its parent Epic via "Part of #N" in body
- Every Story has acceptance criteria
- Plan approved by user before any code is written

**Story Breakdown by Epic:**

_Database Epic (Issue #1):_
- DB-1: Document existing SQLite schema and seed data — Issue #5
- DB-2: Create `modern/database/init.sh` to set up SQLite for the modern app — Issue #6

_Backend Epic (Issue #2):_
- BE-1: Scaffold Spring Boot 3 / Java 21 project under `modern/backend/` — Issue #7
- BE-2: Configure SQLite datasource + Spring Data JDBC — Issue #8
- BE-3: Implement Employee authentication + JWT token issuance — Issue #9
- BE-4: JWT filter + Spring Security resource server config — Issue #10
- BE-5: Customer API (search by Customer ID / Account Number) — Issue #11
- BE-6: Account API (get account details) — Issue #12
- BE-7: Deposit API — Issue #13
- BE-8: Withdrawal API — Issue #14
- BE-9: Fund Transfer API (atomic) — Issue #15
- BE-10: Transaction History API (filterable by type) — Issue #16
- BE-11: BCrypt-migrate employee passwords on first start (keep DB otherwise intact) — Issue #17
- BE-12: Backend unit + integration tests (JUnit 5, isolated test DB) — Issue #18

_Frontend Epic (Issue #3):_
- FE-1: Scaffold React + TypeScript project under `modern/frontend/` using Vite — Issue #19
- FE-2: Configure API client (Axios + JWT header interceptor) — Issue #20
- FE-3: Login screen — Issue #21
- FE-4: Protected route + AppContext (authenticated session) — Issue #22
- FE-5: Dashboard (KPI tiles: total accounts, customers; quick actions) — Issue #23
- FE-6: Customer Search screen — Issue #24
- FE-7: Account Detail screen — Issue #25
- FE-8: Deposit screen — Issue #26
- FE-9: Withdrawal screen — Issue #27
- FE-10: Fund Transfer screen — Issue #28
- FE-11: Transaction History screen — Issue #29
- FE-12: Frontend component tests (Vitest + Testing Library) — Issue #30

_DevOps Epic (Issue #4):_
- DO-1: `modern/README.md` with local run instructions — Issue #31
- DO-2: Docker Compose (backend + frontend) — Issue #32
- DO-3: SHA-256 integrity check script for `legacybank.db` (confirms legacy unchanged) — Issue #33

**Relevant Context:**
- [`legacy_banking_modernization_workshop.md`](legacy_banking_modernization_workshop.md) — workshop Stage 1 prompt
- [`database/sqlite-schema.sql`](database/sqlite-schema.sql) — schema reference for Stories
- [`database/seed-data.sql`](database/seed-data.sql) — demo data reference
- User's GitHub `origin` repository — target for all Issues

---

### Sub-Task 2 — Backend: Spring Boot Scaffold + SQLite Datasource

**Status:** `[ ] pending`

**Intent:**
Create the Spring Boot 3 / Java 21 project skeleton under `modern/backend/` and wire it to the existing SQLite `legacybank.db`. This is the foundation that all other backend stories build on.

**Expected Outcomes:**
- `modern/backend/pom.xml` with Spring Boot 3, Spring Data JDBC (or JPA), SQLite JDBC, Spring Security, JJWT dependencies
- `application.properties` pointing to `../../legacybank.db` (relative path from backend run directory)
- `mvn spring-boot:run` starts and connects to the database without error
- Health endpoint (`/actuator/health`) returns `UP`
- Schema read-only — no Hibernate `ddl-auto` creates or drops tables

**Todo List:**
1. Create `modern/backend/` directory and `pom.xml` (parent: `spring-boot-starter-parent` 3.x, Java 21)
2. Add dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jdbc`, `spring-boot-starter-security`, `sqlite-jdbc`, `spring-boot-starter-actuator`, `jjwt-api`/`jjwt-impl`/`jjwt-jackson`
3. Create `src/main/resources/application.properties`: datasource URL, driver class, schema validation = none
4. Create main application class `com.modernbank.BankingApiApplication`
5. Verify `mvn spring-boot:run` succeeds; commit on feature branch; update GitHub Issue BE-1 and BE-2

**Relevant Context:**
- [`src/main/resources/db.properties`](src/main/resources/db.properties) — legacy DB URL pattern (`jdbc:sqlite:legacybank.db`)
- [`database/sqlite-schema.sql`](database/sqlite-schema.sql) — tables the JPA entities must map to
- Workshop guardrail: `legacybank.db` must remain at repo root; modern backend references it by relative path

---

### Sub-Task 3 — Backend: Authentication API (JWT)

**Status:** `[ ] pending`

**Intent:**
Implement employee login that validates credentials against the `EMPLOYEES` table and returns a JWT. BCrypt-hash the plain-text password on first login (one-time migration per employee row). Protect all non-auth endpoints with JWT validation.

**Expected Outcomes:**
- `POST /api/auth/login` accepts `{ username, password }`, returns `{ token, employeeName }`
- Passwords stored as BCrypt hashes in `EMPLOYEES.PASSWORD` after first login
- All other `/api/**` routes require a valid Bearer token
- `401 Unauthorized` on bad credentials or missing/expired token

**Todo List:**
1. Create `Employee` model/entity mapping to `EMPLOYEES` table
2. Implement `EmployeeRepository` (JDBC or JPA)
3. Implement `AuthService`: load employee → BCrypt verify (fall back to plain-text check then rehash on match)
4. Implement `AuthController` with `POST /api/auth/login`
5. Configure Spring Security: permit `/api/auth/**`, require auth on all other routes, stateless sessions
6. Add JWT utility (`JwtUtil`): generate token (HS256, 8h expiry), validate, extract claims
7. Add `JwtAuthFilter` as `OncePerRequestFilter`
8. Write JUnit 5 tests for `AuthService` and `AuthController` (MockMvc)
9. Commit + update GitHub Issue BE-3, BE-4

**Relevant Context:**
- Legacy [`LoginFrame`](src/main/java/com/legacybank/LoginFrame.java) — queries `EMPLOYEES` by `USERNAME`; compares password directly
- Legacy [`AppSession`](src/main/java/com/legacybank/AppSession.java) — employee ID, name, username fields to replicate in JWT claims
- `EMPLOYEES` schema: `EMPLOYEE_ID`, `USERNAME`, `PASSWORD`, `EMPLOYEE_NAME`, `STATUS`

---

### Sub-Task 4 — Backend: Customer and Account APIs

**Status:** `[ ] pending`

**Intent:**
Expose REST endpoints that replicate the Customer Search and Account Details screens from the legacy app.

**Expected Outcomes:**
- `GET /api/customers/search?customerId={id}` returns customer + their accounts
- `GET /api/customers/search?accountNumber={num}` returns customer + their accounts
- `GET /api/accounts/{accountNumber}` returns single account details
- Responses include all fields shown in legacy frames (name, email, phone, address, account type, status, balance, created date)
- `404` returned when customer or account not found

**Todo List:**
1. Create `Customer`, `Account` models
2. Implement `CustomerRepository` and `AccountRepository`
3. Implement `CustomerService` with search-by-customerId and search-by-accountNumber logic
4. Implement `CustomerController` and `AccountController`
5. Add input validation (non-blank, format per `BankUtil.checkAccountNumber` rules)
6. Write JUnit 5 tests (service layer with mock repo, controller layer with MockMvc)
7. Commit + update GitHub Issues BE-5, BE-6

**Relevant Context:**
- Legacy [`CustomerSearchFrame`](src/main/java/com/legacybank/CustomerSearchFrame.java) — SQL: `SELECT * FROM CUSTOMERS WHERE CUSTOMER_ID = ?` and join with `ACCOUNTS`
- Legacy [`AccountDetailsFrame`](src/main/java/com/legacybank/AccountDetailsFrame.java) — SQL: `SELECT a.*, c.CUSTOMER_NAME … FROM ACCOUNTS a JOIN CUSTOMERS c …`
- [`BankUtil`](src/main/java/com/legacybank/BankUtil.java) — `checkAccountNumber()` alphanumeric 1–20 chars

---

### Sub-Task 5 — Backend: Transaction APIs

**Status:** `[ ] pending`

**Intent:**
Implement the four financial operations (Deposit, Withdrawal, Transfer, History) with identical business rules to the legacy app, using `@Transactional` and `BigDecimal` for atomic correctness.

**Expected Outcomes:**
- `POST /api/transactions/deposit` — adds funds, records DEPOSIT transaction
- `POST /api/transactions/withdraw` — removes funds, records WITHDRAWAL transaction
- `POST /api/transactions/transfer` — atomically debits source + credits destination, records TRANSFER_DEBIT and TRANSFER_CREDIT with same reference number
- `GET /api/transactions/{accountNumber}?type={optional}` — returns transaction history newest first
- Errors return structured JSON `{ error: "..." }` with `400 Bad Request` for business rule violations
- All amounts use `BigDecimal` scale 2 `HALF_UP`; reference numbers follow `DEP`/`WDR`/`TRF` prefix + sequence number pattern

**Todo List:**
1. Create `Transaction` model; `TransactionRepository`
2. Implement `ReferenceNumberService` using `REFERENCE_SEQUENCE` table (insert row, use `last_insert_rowid()`)
3. Implement `TransactionService` with `deposit()`, `withdraw()`, `transfer()` — all `@Transactional`
4. Implement `TransactionController`
5. Replicate all business rules from legacy frames: amount > 0, ≤ 10M, 2 decimals, account ACTIVE, sufficient balance, source ≠ destination for transfer
6. Write JUnit 5 tests covering happy paths and all error branches; use isolated test SQLite DB
7. Commit + update GitHub Issues BE-7, BE-8, BE-9, BE-10

**Relevant Context:**
- Legacy [`DepositFrame`](src/main/java/com/legacybank/DepositFrame.java), [`WithdrawalFrame`](src/main/java/com/legacybank/WithdrawalFrame.java), [`TransferFrame`](src/main/java/com/legacybank/TransferFrame.java), [`TransactionHistoryFrame`](src/main/java/com/legacybank/TransactionHistoryFrame.java)
- [`BankUtil.parseAmount()`](src/main/java/com/legacybank/BankUtil.java) — validation rules to port exactly
- Schema: `TRANSACTIONS` table, `REFERENCE_SEQUENCE` table, `CHECK (BALANCE >= 0)` constraint

---

### Sub-Task 6 — Frontend: Scaffold + Auth Screens

**Status:** `[ ] pending`

**Intent:**
Create the React + TypeScript project under `modern/frontend/` and implement the Login screen, API client with JWT interceptor, and protected routing.

**Expected Outcomes:**
- `npm run dev` starts Vite dev server
- Login screen renders at `/`; successful login stores token and redirects to `/dashboard`
- Unauthenticated users redirected to `/` from any protected route
- Axios instance with `Authorization: Bearer <token>` header attached automatically
- Logout clears token and returns to login

**Todo List:**
1. Scaffold with `npm create vite@latest frontend -- --template react-ts` under `modern/`
2. Install dependencies: `axios`, `react-router-dom`, `tailwindcss` (and configure PostCSS)
3. Create `src/api/client.ts` — Axios instance with base URL `http://localhost:8080/api` + JWT interceptor
4. Create `AuthContext` + `useAuth` hook
5. Create `LoginPage` component: username/password form → `POST /api/auth/login` → store token → navigate
6. Add `ProtectedRoute` wrapper
7. Set up `App.tsx` routing: `/` → LoginPage, `/dashboard` → (protected) DashboardPage placeholder
8. Configure Vite proxy to backend (`/api` → `http://localhost:8080`)
9. Write Vitest component test for `LoginPage`
10. Commit + update GitHub Issues FE-1, FE-2, FE-3, FE-4

**Relevant Context:**
- Legacy [`LoginFrame`](src/main/java/com/legacybank/LoginFrame.java) — demo credential: `admin` / `admin123`
- Backend `POST /api/auth/login` returns `{ token, employeeName }`
- UI library chosen: **Tailwind CSS** (utility classes; no component library dependency)

---

### Sub-Task 7 — Frontend: Dashboard + Navigation

**Status:** `[ ] pending`

**Intent:**
Build the main Dashboard screen and navigation shell. The dashboard should be visually clean and demo-ready, showing KPI summary tiles and quick action links — the primary "wow factor" for the workshop demo.

**Expected Outcomes:**
- Persistent side navigation with links to: Dashboard, Customer Search, Deposit, Withdrawal, Transfer, Transaction History
- Dashboard displays: welcome message (employee name from JWT), KPI tiles (can be static counts or fetched from an `/api/summary` endpoint)
- Quick Actions panel with buttons navigating to key screens
- Responsive layout using Tailwind utility classes

**Todo List:**
1. Create `DashboardPage` component with Tailwind `grid` layout for KPI summary tiles
2. Add sidebar navigation and top header shell using Tailwind
3. Add Quick Actions panel with Tailwind-styled buttons or cards
4. Optionally add `GET /api/summary` backend endpoint (counts for accounts, customers) — decide in this sub-task
5. Write Vitest snapshot/render test for `DashboardPage`
6. Commit + update GitHub Issue FE-5

**Relevant Context:**
- Legacy [`MainFrame`](src/main/java/com/legacybank/MainFrame.java) — menu bar with Customers, Deposit, Withdrawal, Transfer, History entries to replicate as nav items
- Workshop outcome table: "KPI summary dashboard, Quick Actions panel, status tags, accessible navigation"

---

### Sub-Task 8 — Frontend: Customer Search + Account Detail Screens

**Status:** `[ ] pending`

**Intent:**
Build the Customer Search and Account Detail screens, replicating the information density and lookup behavior of the legacy Swing frames in a clean web UI.

**Expected Outcomes:**
- Customer Search page: input for Customer ID or Account Number; submit calls backend; renders customer info table + accounts list with status tags (ACTIVE = green, FROZEN = orange, CLOSED = red)
- Account Detail page: navigated to from search results; shows all account fields + customer contact info; buttons to Deposit / Withdraw / View History for that account
- Empty/not found state handled with inline error message

**Todo List:**
1. Create `CustomerSearchPage` component; wire to `GET /api/customers/search`
2. Add `AccountList` sub-component with Tailwind-styled table + color-coded status badges
3. Create `AccountDetailPage` component; wire to `GET /api/accounts/{accountNumber}`
4. Add navigation links from search results to Account Detail and from Account Detail to transaction screens
5. Write Vitest tests for `CustomerSearchPage` and `AccountDetailPage`
6. Commit + update GitHub Issues FE-6, FE-7

**Relevant Context:**
- Legacy [`CustomerSearchFrame`](src/main/java/com/legacybank/CustomerSearchFrame.java) — columns: Account Number, Type, Status, Balance
- Legacy [`AccountDetailsFrame`](src/main/java/com/legacybank/AccountDetailsFrame.java) — shortcuts to Deposit/Withdraw/History
- Account `STATUS` values: `ACTIVE`, `FROZEN`, `CLOSED`; `ACCOUNT_TYPE`: `SAVINGS`, `CHECKING`, `FIXED_DEPOSIT`

---

### Sub-Task 9 — Frontend: Transaction Screens

**Status:** `[ ] pending`

**Intent:**
Build the Deposit, Withdrawal, Transfer, and Transaction History screens, enforcing client-side validation rules and surfacing backend business-rule errors inline.

**Expected Outcomes:**
- Deposit screen: account number + amount form; on success shows reference number and updated balance
- Withdrawal screen: same pattern; shows insufficient-funds error inline if rejected
- Transfer screen: source account, destination account, amount; shows shared reference number on success
- Transaction History screen: account number input + optional type filter dropdown; displays results in a Tailwind-styled table, newest first
- All screens pre-populate account number if navigated from Account Detail

**Todo List:**
1. Create `DepositPage`, `WithdrawalPage`, `TransferPage` components; wire to respective backend APIs
2. Add client-side validation mirroring backend rules (positive amount, max 2 decimals, etc.)
3. Create `TransactionHistoryPage`; wire to `GET /api/transactions/{accountNumber}`; add type filter
4. Display success confirmation with reference number; display backend error messages inline
5. Write Vitest tests for each screen (happy path + error state)
6. Commit + update GitHub Issues FE-8, FE-9, FE-10, FE-11

**Relevant Context:**
- Legacy `BankUtil.parseAmount()` validation rules (positive, ≤ $10M, max 2 decimals)
- Backend error response shape: `{ error: "..." }` with `400` status
- Workshop demo checklist: deposit, withdrawal, transfer must all work end-to-end

---

### Sub-Task 10 — DevOps: Docker Compose + README + Integrity Check

**Status:** `[ ] pending`

**Intent:**
Make the modernized app easy to run locally (and optionally via Docker Compose). Write `modern/README.md` with clear instructions. Add an integrity script that confirms `legacybank.db` has not been modified.

**Expected Outcomes:**
- `modern/README.md` with: prerequisites, local run commands (`mvn spring-boot:run`, `npm run dev`), Docker Compose commands (if approved), test commands, demo credentials
- `modern/database/init.sh` creates or symlinks `legacybank.db` for the backend (if needed)
- `modern/check-integrity.sh` computes and verifies SHA-256 of `legacybank.db` and selected legacy source files
- `docker-compose.yml` (if Docker approved) under `modern/` builds and starts both services
- `mvn test` and `npm test` both pass in CI/CD-style run

**Todo List:**
1. Write `modern/README.md`
2. Write `modern/database/init.sh`
3. Write `modern/check-integrity.sh` (record baseline SHA-256 on first run; compare on subsequent runs)
4. Write `modern/docker-compose.yml` with `backend` and `frontend` services (if approved)
5. Confirm `docker compose up --build` starts the app end-to-end (if Docker approved)
6. Commit + update GitHub Issues DO-1, DO-2, DO-3

**Relevant Context:**
- Workshop Section 5 — run commands; `modern/` README takes priority over root instructions
- Guardrail: `legacybank.db` must remain at repo root; `modern/database/init.sh` should reference it without copying

---

### Sub-Task 11 — Stage 3: Validation + Pull Request

**Status:** `[ ] pending`

**Intent:**
Perform end-to-end validation of the complete modernized application, resolve any failures, then create a Pull Request from the feature branch to `main` in the user's `origin` repository.

**Expected Outcomes:**
- `mvn test` passes (126+ backend tests)
- `npm test` passes (66+ frontend tests)
- Application runs locally; all 5 demo checklist items pass:
  - [ ] Dashboard opens and looks presentable
  - [ ] Backend starts and connects to database
  - [ ] Login works with `admin` / `admin123`
  - [ ] Customer lookup, deposit, withdrawal, fund transfer work
  - [ ] Transaction history shown
- `legacybank.db` SHA-256 unchanged; `check-integrity.sh` passes
- Pull Request opened to `main` in user's repository with description of implemented features, test results, and run instructions

**Todo List:**
1. Run `mvn test` in `modern/backend/`; fix any failures
2. Run `npm test` in `modern/frontend/`; fix any failures
3. Run app locally; walk through all demo checklist items; fix any runtime issues
4. Run `modern/check-integrity.sh`; confirm legacy assets unchanged
5. Commit all fixes on the feature branch; push to `origin`
6. Update all remaining open GitHub Issues to closed/completed
7. Create Pull Request: feature branch → `main`; include summary of features, test counts, run instructions
8. Report Pull Request URL to user; **do not merge** without user approval

**Relevant Context:**
- Workshop Section 5 — demo checklist
- Workshop Section 3 — Pull Request must target user's `origin` `main` branch, not the facilitator's repo
- Workshop guardrail: check legacy files are untouched before creating PR

---

## Decision Log

> _Decisions made during Stage 1 planning will be recorded here._

| # | Question | Decision |
|---|----------|----------|
| 1 | Keep SQLite or migrate to PostgreSQL? | **Keep SQLite** — use existing `legacybank.db` as-is |
| 2 | Auth: JWT stateless or session-based? | **JWT stateless tokens** (HS256, 8h expiry) |
| 3 | UI library: Carbon Design System or Tailwind? | **Tailwind CSS** — utility-first, lighter weight |
| 4 | Include Docker Compose? | **Yes** — include in DevOps sub-task (DO-2) |
