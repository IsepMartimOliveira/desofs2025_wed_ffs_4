# 🛡️ STRIDE Threat Model

Stride is a threat modeling framework that helps identify and categorize potential security threats in software systems. 

#  Analysis
## User

### Login


| **Threat Category**            | **Security Property Violated** | **Description**                                                                                 |
|-------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------|
| **S - Spoofing**              | Authentication                | Attackers use stolen credentials, session tokens, or brute-force to impersonate legitimate users. |
| **T - Tampering**             | Integrity                     | Login parameters (e.g., password hash or token) are altered in transit or via client-side manipulation. |
| **R - Repudiation**           | Non-repudiation               | Users claim they never logged in, creating disputes without proper audit logging.               |
| **I - Information Disclosure**| Confidentiality               | Error messages or debug logs leak whether usernames exist or reveal internal login logic.        |
| **D - Denial of Service**     | Availability                  | Brute-force attacks or repeated login attempts lock accounts or degrade login service performance. |
| **E - Elevation of Privilege**| Authorization                 | Attacker manipulates login flow to access higher-privileged user sessions.                      |

### Account Creation
| **Threat Category**            | **Security Property Violated** | **Description**                                                                                   |
|-------------------------------|-------------------------------|---------------------------------------------------------------------------------------------------|
| **S - Spoofing**              | Authentication                | Malicious users register fake identities to impersonate others or inflate user counts.            |
| **T - Tampering**             | Integrity                     | Registration forms manipulated to inject scripts or escalate default privileges.                  |
| **R - Repudiation**           | Non-repudiation               | Users deny agreeing to terms or dispute ownership of registered accounts.                         |
| **I - Information Disclosure**| Confidentiality               | Backend responses reveal too much data (e.g., which emails are registered).                       |
| **D - Denial of Service**     | Availability                  | Bots create mass accounts, consuming storage and processing capacity.                             |
| **E - Elevation of Privilege**| Authorization                 | Attackers register accounts that bypass default role assignments through form manipulation.       |



### Upload File

| **Threat Category**            | **Security Property Violated** | **Description**                                                                                      |
|-------------------------------|-------------------------------|------------------------------------------------------------------------------------------------------|
| **S - Spoofing**              | Authentication                | Uploaded files falsely claim ownership or are submitted using stolen user sessions.                  |
| **T - Tampering**             | Integrity                     | File names, extensions, or metadata are manipulated to bypass file validation and upload malware.    |
| **R - Repudiation**           | Non-repudiation               | Users deny uploading specific files, challenging accountability without upload logs.                 |
| **I - Information Disclosure**| Confidentiality               | Uploaded files are publicly accessible or include metadata that leaks sensitive user or system info. |
| **D - Denial of Service**     | Availability                  | Oversized or malformed file uploads consume storage or crash services.                              |
| **E - Elevation of Privilege**| Authorization                 | File upload exploits lead to code execution or access to restricted server paths.                    |
