#  Phase 1 Analasys

# Introduction


Considering the growing complexity of attacks and the frequent disclosure of sensitive data, security is becoming a crucial necessity in software engineering application development. Following  the Secure Software Development Life Cycle (SSDLC) paradigm, the project created as part of the Master of Computer Engineering program's Secure Software Development (DESOFS) course aims to methodically implement security techniques at every level of the development life cycle.

The goal of this project is to provide a digital platform for music subscription plans that will benefit system administrators as well as end users. With the help of a relational database and a REST API, the program provides features including subscription management, usage analytics, and device administration.

The project's first phase focusses on the analysis and secure solution design. During this phase, functional and non-functional requirements, specific security requirements, and potential abuses that could compromise the system's integrity or confidentiality were identified.


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
