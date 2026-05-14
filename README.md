# Loan Origination System

A concurrent loan processing system built using Java, Spring Boot and PostgreSQL.

The system supports:
- Loan application submission
- Automatic loan approval/rejection
- Manual underwriting flow
- Concurrent async loan processing
- Agent assignment and review
- Loan monitoring APIs
- Idempotent loan creation
- Pagination and aggregation APIs

---

# Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Database | PostgreSQL |
| Build Tool | Maven |
| ORM | Spring Data JPA / Hibernate |

---

# System Workflow

```text
Client submits loan
        ↓
Loan stored as APPLIED
        ↓
Scheduler polls APPLIED loans
        ↓
Loans atomically moved to PROCESSING
        ↓
Async worker processes loan
        ↓
Decision Engine decides:
    - APPROVED_BY_SYSTEM
    - REJECTED_BY_SYSTEM
    - UNDER_REVIEW
        ↓
If UNDER_REVIEW:
    assign available agent
        ↓
notify agent + manager
        ↓
agent approves/rejects
        ↓
customer notification sent
```

---

# Architecture Decisions

## Concurrent Loan Processing

The system uses PostgreSQL row-level locking with:

```sql
FOR UPDATE SKIP LOCKED
```

This prevents:
- Duplicate loan processing
- Multiple workers processing the same loan
- Agent over-assignment

---

## Atomic Loan Claiming

Loans are:
1. Fetched with row locking
2. Immediately transitioned to `PROCESSING`
3. Saved within the same transaction

This guarantees single worker ownership per loan.

---

## Async Processing

Loan processing runs asynchronously using:

```text
ThreadPoolTaskExecutor
```

This enables:
- Parallel loan processing
- Controlled thread management
- Scalable execution flow

---

## Idempotency Handling

Loan creation uses:

```text
Idempotency-Key
```

along with a database unique constraint to prevent duplicate loan creation during retries.

---

## Manual Underwriting Flow

Large or suspicious loans move to:

```text
UNDER_REVIEW
```

and are assigned to an available agent.

Each agent:
- belongs to a manager
- has limited review capacity
- can review only assigned loans

---

# Loan Decision Rules

| Condition | Result |
|---|---|
| Too many recent applications | UNDER_REVIEW |
| Too many previous rejections | REJECTED_BY_SYSTEM |
| Large BUSINESS loan | UNDER_REVIEW |
| Small loan amount | APPROVED_BY_SYSTEM |

---

# API Endpoints

## 1. Create Loan

### Endpoint

```http
POST /api/v1/loans
```

### Headers

```http
Idempotency-Key: loan-101
```

### Request Body

```json
{
  "customerName": "Sonal Rao",
  "customerPhone": "9999999999",
  "loanAmount": 50000,
  "loanType": "PERSONAL"
}
```

---

## 2. Agent Review Decision

### Endpoint

```http
PUT /api/v1/agents/{agentId}/loans/{loanId}/decision
```

### Request Body

```json
{
  "decision": "APPROVE"
}
```

OR

```json
{
  "decision": "REJECT"
}
```

---

## 3. Fetch Loans By Status

### Endpoint

```http
GET /api/v1/loans?status={status}&page={n}&size={m}
```

### Example

```http
GET /api/v1/loans?status=UNDER_REVIEW&page=0&size=10
```

---

## 4. Loan Status Count

### Endpoint

```http
GET /api/v1/loans/status-count
```

---

## 5. Top Customers API

### Endpoint

```http
GET /api/v1/customers/top
```

Returns the top 3 customers with the highest approved loan count.

---

## 6. Fetch Assigned Loans

### Endpoint

```http
GET /api/v1/agents/{agentId}/loans?page=0&size=10
```

---

# Loan Status Flow

```text
APPLIED
    ↓
PROCESSING
    ↓
APPROVED_BY_SYSTEM
OR
REJECTED_BY_SYSTEM
OR
UNDER_REVIEW
        ↓
APPROVED_BY_AGENT
OR
REJECTED_BY_AGENT
```

---

# Database Design

## Tables

- loan_applications
- agents

---

## Important Database Features

### Indexes

Indexes are added on:
- application_status
- assigned_agent_id
- loan_id
- idempotency_key

These indexes optimize:
- scheduler polling
- loan filtering
- pagination queries
- assignment lookups
- idempotency validation

---

### Agent Hierarchy

Agents use a self-referencing relationship:

```text
Agent → Manager(Agent)
```

This supports manager notification during manual review assignment.

---

# Error Handling

Global exception handling is implemented using:

```text
@RestControllerAdvice
```

This provides:
- Consistent API error responses
- Validation handling
- Workflow exception handling

---

# Scalability Considerations

The system supports scalable concurrent processing through:

- Async worker pool
- Batched scheduler polling
- Row-level pessimistic locking
- Pagination support
- Indexed query paths

---

# Running Locally

## Prerequisites

- Java 21
- Maven
- PostgreSQL

---

# PostgreSQL Setup

Create database:

```sql
CREATE DATABASE loan_origination_system;
```

---

# Configure application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loan_origination_system
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

# Run Application

```bash
mvn spring-boot:run
```

Application runs on:

```text
http://localhost:8080
```

---

# Manual Seed Data

Agents and sample loan records are currently seeded manually using PostgreSQL queries.

---

# Tested Scenarios

## Successful Workflow Scenarios

The following workflows were tested successfully:

- Loan creation and async processing
- Automatic approval flow for smaller loans
- Manual underwriting flow for larger loans
- Agent assignment and review flow
- Loan status aggregation APIs
- Pagination and loan filtering APIs
- Top customers aggregation API
- Idempotent loan creation handling

---

## Failure and Validation Scenarios

The following validation and error scenarios were tested to ensure workflow safety and data consistency:

- Duplicate loan creation attempts using the same Idempotency-Key
- Agent attempting to review a loan not assigned to them
- Review attempts on loans that are already finalized
- Duplicate approval/rejection requests on the same loan
- Loan assignment failure when no agents are available
- Invalid workflow transitions between loan states

These checks ensure the system prevents inconsistent state transitions and invalid review actions.

---

# Postman Collection

The repository includes a Postman collection containing:
- All APIs
- Success flow test cases
- Failure and validation test cases

---

# Future Improvements

- Retry mechanisms for failed processing
- Distributed queue/event-driven architecture
- Docker support
- Authentication & authorization
- Metrics and observability
- Distributed scheduling
- Redis caching
- Kafka-based workflow orchestration
- Adding @Version to implement Optimistic locking

