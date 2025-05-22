    #  Phase 2 Analasys

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


# Test Planning

### 1. Password Encryption:

- **Password Encryption**:
    - Test that the provided password is properly encrypted.

- **User Authentication**:
    - Test the authentication process with valid username and password.
    - Ensure that the user is authenticated successfully.

- **Invalid Credentials**:
    - Test the authentication process with invalid username and/or password.
    - Ensure that the user is not authenticated and receives appropriate error messages.


 ### Subscription Process    

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


#### Security Tests:

- **Authorization Tests**:

  - Test that only authenticated users can create subscriptions
  - Test that Marketing Directors can perform bulk migrations
  - Test that users can only access their own subscription details
  - Verify proper JWT token validation for all subscription endpoints

 - **Parameter Validation**:

   - Test subscription creation with malicious plan names (SQL injection attempts)
   - Test payment type validation against regex pattern (annually|monthly)
   - Verify proper validation of version numbers for concurrent access control


