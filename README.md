# Bright Training: Avoiding Mocks and Improving HTTP Request Handling

This repository contains training materials for addressing two common issues in Spring Boot applications:

1. **Abuse of mocks in tests** - Overuse of mocks can lead to brittle tests that are tightly coupled to implementation
   details.
2. **Reliance on HttpServletRequest and HttpServletResponse in service layers** - Direct use of servlet API in service
   layers makes code harder to test and less reusable.

## Training Plan

This repository is organized into branches that demonstrate the progression from problematic code to improved
implementations:

### Branch 1: `aw-sign-up-initial-state`

- Examples of problematic code with excessive mocking
- Examples of service layer code tightly coupled to servlet API
- Tests that are brittle and hard to maintain

### Branch 2: `refactored-unit-integration`

- Refactored code uses unit test for pure logic
- Refactored code to rely heavily on integration tests with as little mocks as possible

### Branch 3: `refactored-avoid-mocks`

- Refactored code using "Testing Without Mocks" patterns
- Improved HTTP request handling with proper abstractions
- Tests that are more robust and less coupled to implementation details

## Key Concepts

### Testing Without Mocks

Based on James
Shore's ["Testing Without Mocks: A Pattern Language"](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks),
this approach focuses on:

- Using real objects instead of mocks when possible
- Creating "Nullables" or "Fakes" for infrastructure dependencies
- Using "Output Trackers" to verify behavior without mocks
- Structuring code to be more testable without mocks

### Improved HTTP Request Handling

- Abstracting HTTP request/response details behind interfaces
- Using DTOs to pass data between layers
- Keeping servlet API dependencies at the controller level only

## Getting Started

This project uses:

- Java 21
- Spring Boot 3.2.0
- Gradle

To run the application:

```
./gradlew bootRun
```

To run the tests:

```
./gradlew test
```

## Training Sessions

### Session 1: Understanding the Problems

- Identify issues with current approach
- Demonstrate how current tests break during refactoring
- Show how servlet API dependencies make testing difficult

### Session 2: Implementing Solutions

- Introduce "Testing Without Mocks" patterns
- Refactor code to remove servlet API dependencies from service layer
- Create more robust tests using the new patterns

### Session 3: Applying to Real Projects

- Strategies for incrementally improving existing codebases
- Identifying good candidates for refactoring
- Measuring success: fewer test failures during refactoring