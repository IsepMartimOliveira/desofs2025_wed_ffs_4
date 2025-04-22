# Phase 1 Analysis


### Introduction


Considering the growing complexity of attacks and the frequent disclosure of sensitive data, security is becoming a crucial necessity in software engineering application development. Following  the Secure Software Development Life Cycle (SSDLC) paradigm, the project created as part of the Master of Computer Engineering program's Secure Software Development (DESOFS) course aims to methodically implement security techniques at every level of the development life cycle.

The goal of this project is to provide a digital platform for music subscription plans that will benefit system administrators as well as end users. With the help of a relational database and a REST API, the program provides features including subscription management, usage analytics, and device administration.

The project's first phase focusses on the analysis and secure solution design. During this phase, functional and non-functional requirements, specific security requirements, and potential abuses that could compromise the system's integrity or confidentiality were identified.


### Project Overview

The selected project consists of the development of a **backend system for managing music subscription plans**. This platform is designed to provide essential services for handling user subscriptions, device associations, and usage metrics, all through a secure and well-structured **RESTful API**.

The system allows users to subscribe to different music plans, manage their active subscriptions, and link multiple devices to their accounts. Additionally, it offers administrative functionalities, enabling system administrators to manage users, monitor subscription data, and access relevant business metrics.

#### Key Features
- **User Management**: Handling user registration, authentication, and role-based access control, supporting multiple roles such as standard users, premium users, and administrators.
- **Subscription Management**: Enabling users to subscribe, modify, or cancel music plans, with full tracking of subscription status and history.
- **Device Association**: Allowing users to register and manage devices authorized to access their music subscription.
- **Usage Metrics**: Collecting and providing access to system usage statistics to support administrative decision-making.
- **Backend Operations**: Performing specific server-side tasks such as file handling and directory management when necessary.

The system architecture is based on a **REST API** connected to a **relational database**, ensuring data persistence and scalability. Designed without a frontend, the platform is intended to be integrated with external clients or user interfaces, acting purely as a service provider.



## Domain Model

![Domain Model](./Phase1/img/DM.png)



## Use Case Diagram
![Use Case Diagram](./Phase1/img/UCD.png)

## Functional Requirements

1. Admin can bootstrap user credential data.
2. Admin can bootstrap subscription plan data.
3. Marketing director can define a new subscription plan, including monthly/annual cost, device limit, and other features.
4. Marketing director can deactivate a subscription plan.
5. Marketing director can change plan details (excluding pricing).
6. Customer can view all available subscription plans.
7. Customer can subscribe to a plan.
8. Subscriber can cancel their subscription.
9. Subscriber can view the details of their subscription plan.
10. Product Manager can view the number of new subscriptions and cancellations for a specific month.
11. Subscriber can add a new device to their account.
12. Subscriber can remove a device from their account.
13. Subscriber can update a device’s details (name and description).
14. Subscriber can list all registered devices.
15. Subscriber can upload a profile image.
16. Subscriber can upload an image when adding or editing a device.
17. Marketing director can promote a subscription plan.
18. Marketing director can cease a subscription plan (only if it has no active subscribers).
19. Subscriber can switch their plan (upgrade or downgrade).
20. Subscriber can renew an annual subscription.
21. Marketing director can migrate all subscribers from one plan to another.
22. Product Manager or Financial Director can view future cash flows for the upcoming months, filtered by plan.
23. Product Manager or Financial Director can view year-to-date revenue, filtered by plan.
24. Marketing director can change the pricing of a plan.
25. Marketing director can view the price change history of a plan.
26. System can augment a subscriber profile with a weather forecast based on their location.
27. System can augment a subscriber profile with a random/funny quote of the day.

## Non-Functional Requirements
1. The system must provide an OpenAPI specification.
2. All authenticated API requests must use JWT (JSON Web Tokens).
3. Long result lists must support pagination.
4. Concurrent access must be handled properly.




## Security Requirements

### General Requirements

1. All endpoints must enforce authentication and validate roles via RBAC.
2. All endpoints must validate input data.
3. All endpoints must use HTTPS.
4. All endpoints must log access attempts.
5. All endpoints must implement rate limiting.
6. Store passwords using strong hashing algorithms.
7. Implement JWT with appropriate expiration times.
8. Include proper logging of authentication attempts and errors.

### User Requirements

1. As  an anonymous user, I can view the available subscription plans.
2. As an authenticated user, I can view my subscription plan details, and should not be able to view other users' subscription details.
3. As an authenticated user, I can view my devices and should not be able to view other users' devices.
4. As an authenticated user, I want my uploaded images to be stored securely and not accessible to others unless intended.
5. As an authenticated user, I want to ensure that the information I submit is stored and displayed accurately.
6. As an authenticated user, I want to be notified if any of my critical data is changed (e.g., email, subscription plan)
7. As an authenticated user, I want a log of my important actions (e.g., plan changes, cancellations) to verify what I’ve done.
8. As an authenticated user, I want to be able to recover my account if I forget my password.



