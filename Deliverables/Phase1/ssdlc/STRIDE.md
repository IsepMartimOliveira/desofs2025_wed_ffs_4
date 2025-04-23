# 🛡️ STRIDE Threat Model

Stride is a threat modeling framework that helps identify and categorize potential security threats in software systems. 

#  Analysis
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

### Account Creation
| **Threat Category** | **Security Property Violated** | **Description**                                                                                   |
|---------------|-------------------------------|---------------------------------------------------------------------------------------------------|
| **Spoofing**  | Authentication                | Malicious users register fake identities to impersonate others or inflate user counts.            |
| **Tampering** | Integrity                     | Registration forms manipulated to inject scripts or escalate default privileges.                  |
| **Repudiation** | Non-repudiation               | Users deny agreeing to terms or dispute ownership of registered accounts.                         |
| **Information Disclosure**| Confidentiality               | Backend responses reveal too much data (e.g., which emails are registered).                       |
| **Denial of Service** | Availability                  | Bots create mass accounts, consuming storage and processing capacity.                             |
| **Elevation of Privilege**| Authorization                 | Attackers register accounts that bypass default role assignments through form manipulation.       |



### Upload File

| **Threat Category** | **Security Property Violated** | **Description**                                                                                      |
|-------------------|-------------------------------|------------------------------------------------------------------------------------------------------|
| **Spoofing**      | Authentication                | Uploaded files falsely claim ownership or are submitted using stolen user sessions.                  |
| **Tampering**     | Integrity                     | File names, extensions, or metadata are manipulated to bypass file validation and upload malware.    |
| **Repudiation**   | Non-repudiation               | Users deny uploading specific files, challenging accountability without upload logs.                 |
| **Information Disclosure**| Confidentiality               | Uploaded files are publicly accessible or include metadata that leaks sensitive user or system info. |
| **Denial of Service** | Availability                  | Oversized or malformed file uploads consume storage or crash services.                              |
| **Elevation of Privilege**| Authorization                 | File upload exploits lead to code execution or access to restricted server paths.                    |

### Subscriptions

| **Threat Category**     | **Security Property Violated** | **Description**  |
|-------------------------|-------------------------------|------------------|
| **Spoofing**            | Authentication                | An attacker impersonates a legitimate user due to weak authentication mechanisms, gaining access to subscription details or modifying plans. |
| **Tampering**           | Integrity                     | Unauthorized manipulation of subscription data in transit or at rest, such as altering plan details, payment methods, or subscription status, due to insufficient data validation or protection. |
| **Repudiation**         | Non-repudiation               | Users deny actions like canceling subscriptions or upgrading plans, exploiting the absence of proper audit logs and traceability. |
| **Information Disclosure** | Confidentiality            | Exposure of sensitive data (e.g., subscription details, payment info) to unauthorized users due to improper access control or insecure data handling. |
| **Denial of Service**   | Availability                  | Attackers flood the API with excessive requests, overloading endpoints and preventing legitimate users or admins from accessing subscription services. |
| **Elevation of Privilege** | Authorization              | A regular user exploits system flaws to gain administrative or marketing director privileges, allowing unauthorized modification of plans, pricing, or user migrations. |