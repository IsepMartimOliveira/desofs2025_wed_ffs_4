

# Introduction


Considering the growing complexity of attacks and the frequent disclosure of sensitive data, security is becoming a crucial necessity in software engineering application development. Following  the Secure Software Development Life Cycle (SSDLC) paradigm, the project created as part of the Master of Computer Engineering program's Secure Software Development (DESOFS) course aims to methodically implement security techniques at every level of the development life cycle.

The goal of this project is to provide a digital platform for music subscription plans that will benefit system administrators as well as end users. With the help of a relational database and a REST API, the program provides features including subscription management, usage analytics, and device administration.

The project's first phase focuses on the analysis and secure solution design. During this phase, functional and non-functional requirements, specific security requirements, and potential abuses that could compromise the system's integrity or confidentiality were identified.


# Project Overview

The selected project consists of the development of a **backend system for managing music subscription plans**. This platform is designed to provide essential services for handling user subscriptions, device associations, and usage metrics, all through a secure and well-structured **RESTful API**.

The system allows users to subscribe to different music plans, manage their active subscriptions, and link multiple devices to their accounts. Additionally, it offers administrative functionalities, enabling system administrators to manage users, monitor subscription data, and access relevant business metrics.

#### Key Features
- **User Management**: Handling user registration, authentication, and role-based access control, supporting multiple roles such as standard users, premium users, and administrators.
- **Subscription Management**: Enabling users to subscribe, modify, or cancel music plans, with full tracking of subscription status and history.
- **Device Association**: Allowing users to register and manage devices authorized to access their music subscription.
- **Usage Metrics**: Collecting and providing access to system usage statistics to support administrative decision-making.
- **Backend Operations**: Performing specific server-side tasks such as file handling and directory management when necessary.

The system architecture is based on a **REST API** connected to a **relational database**, ensuring data persistence and scalability. Designed without a frontend, the platform is intended to be integrated with external clients or user interfaces, acting purely as a service provider.




[Archithecture Analasys](./docs/ArchithectureAnalysis.md)

[Threat Modeling](./ssdlc/ThreatModeling.md)

[Stride](./ssdlc/STRIDE.md)

[Abuse Cases](./ssdlc/Abuse_Case.md)

[ASVS](./asvs/asvs.md)

[Workflow](./ssdlc/pipeline/Workflow.md)


# Requirements Implemented

## Architecture,Design and Threat 

## Authentication


The system now takes a more flexible, user-friendly approach to passwords: instead of enforcing complex regex patterns, you only need to pick a password between 12 and 24 characters. That way, you still get a strong, secure password without wrestling with rules that are hard to remember.

 Whenever you want to change your password, just call a dedicated endpoint, enter your current password to verify it’s you, and choose a new one. The entire process uses the same BCrypt encryption standards, so no one can swap out your password without your permission—keeping your account safe and sound.

## Error Handling and Logging 

The system implements a **security-first error handling strategy** that prioritizes information disclosure prevention while maintaining reliability, featuring generic error messages to external clients that prevent sensitive data leakage, detailed internal logging for security monitoring, and consistent error response formats across all API endpoints. 

The error handling covers authentication failures with uniform responses to prevent username enumeration, business logic errors that mask internal system details, database errors with SQL injection protection through parameterized queries, and concurrent access errors with optimistic locking and version control, ensuring that all error responses maintain security while providing appropriate feedback for legitimate users and comprehensive audit trails for security analysis.

### Security Implementation Features

#### Information Security Protection
- **Generic Error Responses**: All client-facing errors use standardized messages that prevent system architecture disclosure and sensitive information leakage
- **Detailed Internal Logging**: Comprehensive security event logging for monitoring, incident response, and audit trail maintenance
- **Database Schema Protection**: SQL exceptions are masked and converted to generic responses, preventing database structure disclosure



# Communication

The system implements comprehensive security measures to ensure secure communication and data protection across all API endpoints. **HTTPS enforcement** is mandatory for all data transmission, preventing man-in-the-middle attacks and ensuring encrypted communication channels. 

The platform utilizes **JWT-based authentication** with secure token management, implementing proper signing algorithms and appropriate expiration times for stateless authentication. 

**Input validation** is rigorously applied to all user inputs including payment type validation against regex patterns (annually|monthly), parameter sanitization to prevent injection attacks, and version number validation for concurrent access control. 

The error handling strategy prioritizes **information disclosure prevention** with generic error messages that prevent system architecture exposure, database schema protection through masked SQL exceptions, and consistent error response formats across all endpoints, ensuring secure communication while maintaining system reliability and user experience.

# Test Planning

## 1. Password Encryption:

- **Password Encryption**:
    - Test that the provided password is properly encrypted.

- **User Authentication**:
    - Test the authentication process with valid username and password.
    - Ensure that the user is authenticated successfully.

- **Invalid Credentials**:
    - Test the authentication process with invalid username and/or password.
    - Ensure that the user is not authenticated and receives appropriate error messages.


 ## 2. Subscription Process    

  #### Functional Tests 


- **Subscription Creation**:

  - Test creating a new subscription with valid plan name and payment type (monthly/annually)
   - Verify subscription is created with correct start date, end date, and active status
   - Ensure user can only have one active subscription at a time


- **Invalid Subscription Creation**:

  - Test creating subscription with non-existent plan name
  - Test creating subscription when user already has an active subscription
  - Test creating subscription with invalid payment type
  - Verify appropriate error messages are returned



- **Subscription Cancellation**:


  - Test canceling an active subscription with proper version control (If-Match header)
  - Verify subscription status changes to inactive
  - Ensure only the subscription owner can cancel their subscription
  


- **Plan Change/Migration**:


  - Test changing subscription plan with valid plan name and version
  - Verify device limit compatibility when changing plans
  - Test preventing change to same plan
  - Ensure proper version control during plan changes



- **Subscription Renewal**:

  - Test renewing annual subscription with proper version control
  - Verify end date is extended by one year
  - Test preventing renewal of monthly subscriptions
  - Test renewal authorization (only subscription owner)


### Security Tests:

- **Authorization Tests**:

  - Test that only authenticated users can create subscriptions
  - Test that users can only access their own subscription details
  - Verify proper JWT token validation for all subscription endpoints

 - **Parameter Validation**:

 
   - Test payment type validation against regex pattern (annually|monthly)
   - Verify proper validation of version numbers for concurrent access control


### Error Handling Tests:

- **Secure Error Messages**:
  - Verify error responses don't leak sensitive system information
  - Test that database errors are properly masked
  - Ensure consistent error message format across endpoints
