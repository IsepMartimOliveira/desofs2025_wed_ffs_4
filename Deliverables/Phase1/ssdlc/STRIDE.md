# 🛡️ STRIDE Threat Model

Stride is a threat modeling framework that helps identify and categorize potential security threats in software systems.

# Analysis

## User

### Login

| **Threat Category** | **Security Property Violated** | **Description**                                                                                 |
|--------------|-------------------------------|-------------------------------------------------------------------------------------------------|
| **Spoofing** | Authentication                | Attackers use stolen credentials, session tokens, or brute-force to impersonate legitimate users. |
| **Tampering** | Integrity                     | Login parameters (e.g., password hash or token) are altered in transit or via client-side manipulation. |
| **Repudiation** | Non-repudiation               | Users claim they never logged in, creating disputes without proper audit logging.               |
| **Information Disclosure**| Confidentiality               | Error messages or debug logs leak whether usernames exist or reveal internal login logic.        |
| **Denial of Service** | Availability                  | Brute-force attacks or repeated login attempts lock accounts or degrade login service performance. |
| **Elevation of Privilege**| Authorization                 | Attacker manipulates login flow to access higher-privileged user sessions.                      |

---

### Account Creation

| **Threat Category** | **Security Property Violated** | **Description**                                                                                   |
|---------------|-------------------------------|---------------------------------------------------------------------------------------------------|
| **Spoofing**  | Authentication                | Malicious users register fake identities to impersonate others or inflate user counts.            |
| **Tampering** | Integrity                     | Registration forms manipulated to inject scripts or escalate default privileges.                  |
| **Repudiation** | Non-repudiation               | Users deny agreeing to terms or dispute ownership of registered accounts.                         |
| **Information Disclosure**| Confidentiality               | Backend responses reveal too much data (e.g., which emails are registered).                       |
| **Denial of Service** | Availability                  | Bots create mass accounts, consuming storage and processing capacity.                             |
| **Elevation of Privilege**| Authorization                 | Attackers register accounts that bypass default role assignments through form manipulation.       |

---

### Upload File

| **Threat Category** | **Security Property Violated** | **Description**                                                                                      |
|-------------------|-------------------------------|------------------------------------------------------------------------------------------------------|
| **Spoofing**      | Authentication                | Uploaded files falsely claim ownership or are submitted using stolen user sessions.                  |
| **Tampering**     | Integrity                     | File names, extensions, or metadata are manipulated to bypass file validation and upload malware.    |
| **Repudiation**   | Non-repudiation               | Users deny uploading specific files, challenging accountability without upload logs.                 |
| **Information Disclosure**| Confidentiality               | Uploaded files are publicly accessible or include metadata that leaks sensitive user or system info. |
| **Denial of Service** | Availability                  | Oversized or malformed file uploads consume storage or crash services.                              |
| **Elevation of Privilege**| Authorization                 | File upload exploits lead to code execution or access to restricted server paths.                    |

---

### Subscriptions

| **Threat Category**     | **Security Property Violated** | **Description**  |
|-------------------------|-------------------------------|------------------|
| **Spoofing**            | Authentication                | An attacker impersonates a legitimate user due to weak authentication mechanisms, gaining access to subscription details or modifying plans. |
| **Tampering**           | Integrity                     | Unauthorized manipulation of subscription data in transit or at rest, such as altering plan details, payment methods, or subscription status, due to insufficient data validation or protection. |
| **Repudiation**         | Non-repudiation               | Users deny actions like canceling subscriptions or upgrading plans, exploiting the absence of proper audit logs and traceability. |
| **Information Disclosure** | Confidentiality            | Exposure of sensitive data (e.g., subscription details, payment info) to unauthorized users due to improper access control or insecure data handling. |
| **Denial of Service**   | Availability                  | Attackers flood the API with excessive requests, overloading endpoints and preventing legitimate users or admins from accessing subscription services. |
| **Elevation of Privilege** | Authorization              | A regular user exploits system flaws to gain administrative or marketing director privileges, allowing unauthorized modification of plans, pricing, or user migrations. |

---

### Plans

| **Threat Category**       | **Security Property Violated** | **Description**                                                                                                                                                     |
|---------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Spoofing**              | Authentication                | Unauthorized users impersonate Marketing Director, which allows them to create, edit, or remove plans.                                                              |
| **Tampering**             | Integrity                     | Attackers manipulate critical data in the database or in the requests, to keep access to plans or reduce a plan cost, which is possible due to the lack of security |
| **Repudiation**           | Non-repudiation               | Users deny being able to access or view plans knowing there are no logs available.                                                                                  |
| **Information Disclosure**| Confidentiality               | Sensitive data may be disclosured, due to improper access control, insecure data handling or response interception.                                                 |
| **Denial of Service**     | Availability                  | Excessive amount of requests to view plans, impacts the system availability, either by reducing response time or shutting it down completely.                       |
| **Elevation of Privilege**| Authorization                 | Unauthorized users are able to access to Marketing Director functionalities due to faulted role validation.                                                         |

---

### Dashboard

| **Threat Category**       | **Security Property Violated** | **Description**                                                                                   |
|---------------------------|-------------------------------|---------------------------------------------------------------------------------------------------|
| **Spoofing**              | Authentication                | Unauthorized users impersonate Product Manager, Financial Director, or Project Manager using stolen JWT tokens. |
| **Tampering**             | Integrity                     | Malicious users manipulate input parameters (e.g., `plan`, `numberMonth`, `startDate`, `endDate`) to cause unexpected behavior and manipulate plans data. |
| **Repudiation**           | Non-repudiation               | Users deny accessing revenue or subscription data due to lack of proper audit logs.               |
| **Information Disclosure**| Confidentiality               | Unauthorized access to sensitive data (e.g., revenue, subscription details) due to improper access control, insecure error handling or response interception due to the lack of encryption. |
| **Denial of Service**     | Availability                  | Excessive requests or malformed inputs cause resource exhaustion, impacting service availability. |
| **Elevation of Privilege**| Authorization                 | Unauthorized users access higher-privileged (Product Manager or Financial Director) functionalities due to insufficient role validation.  |

**Justification for a Single STRIDE Analysis Table:**

A single STRIDE analysis table was included because the threats identified across the functionalities of the Dashboard Management component share similar patterns. These functionalities (e.g., processing revenue, querying subscriptions) involve common actors, data flows, and security concerns. By consolidating the analysis, we can efficiently address overlapping threats and apply consistent mitigation strategies.

---

### Devices

| **Threat Category**       | **Security Property Violated** | **Description**                                                                                                                                 |
|---------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| **Spoofing**              | Authentication                | Unauthorized users impersonate a Subscriber, which allows them to create, edit, or remove devices.                                              |
| **Tampering**             | Integrity                     | Attackers manipulate critical data in the database or in the requests                                                                           |
| **Repudiation**           | Non-repudiation               | Users deny being able to access or view devices knowing there are no logs available.                                                            |
| **Information Disclosure**| Confidentiality               | Sensitive data may be disclosured, due to improper access control, insecure data handling or response interception.                             |
| **Denial of Service**     | Availability                  | Excessive amount of requests to view devices, impacts the system availability, either by reducing response time or shutting it down completely. |
| **Elevation of Privilege**| Authorization                 | Unauthorized users are able to access to a Subscriber functionalities due to faulted role validation.                                           |

---