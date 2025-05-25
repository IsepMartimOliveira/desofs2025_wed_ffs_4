# Test Planning

## 1. Password Encryption:

- **Password Encryption**:
    - Test that the provided password is properly encrypted.
    - Ensure plane text passwords are not stored in the database.

- **User Authentication**:
    - Test the authentication process with valid username and password.
    - Ensure that the user is authenticated successfully.

- **Invalid Credentials**:
    - Test the authentication process with invalid username and/or password.
    - Ensure that the user is not authenticated and receives appropriate error messages(generic).
- **Login Rate Limiting**:
    - Test the login endpoint with multiple failed attempts to ensure rate limiting is enforced.
    - Verify that the user account is temporarily locked after a certain number of failed attempts.

- **Password Change**:
    - Test changing the password with valid current password and new password.
    - Ensure that the password is updated successfully.
    - Verify that the user can log in with the new password.


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



## 3. User Management

- **User Creation Validation**:
    - Test user registration with valid details (username, password, email).
    - Verify that the user is created successfully and can log in.
    - Ensure that the user cannot register with an already existing username or email.

- **User Account Creation**:
    - Simulate multiple rapid registration attempts from the same location
    - Verify that the system prevents account creation to mitigate brute force attacks.
    - Ensure that the user cannot create an account with an already existing username or email.

- **Image File Upload**:

    - Test uploading a file with valid parameters (file type, size)
    - Verify file is stored correctly and associated with the user
    - Ensure file upload fails for invalid file types or sizes
    - Ensure no embemedded scripts or malicious content in uploaded files
    - Image is stored in the correct directory and accessible via the API


## 4. Device Management

- **Device Creation**:
    - Test creating a new device with valid parameters (description, macAdress, name, fileName)
    - Verify device is associated with the user and stored correctly
    - Ensure device creation fails for invalid parameters or duplicate names

- **Device Upload Image**:
   - Test uploading an image for a device with valid parameters (file type, size)
  - Verify image is stored correctly and associated with the device
  - Ensure image upload fails for invalid file types or sizes
  
## 5. Plan Management  

- **Plan Creation/Update**:
  - Ensure that only the assigned role can create/update plans, in this case the marketing director
  - Test creating a new plan with valid parameters
  - Verify if the plan was actually created~~~~
  - Ensure the plan can´t be created with invalid inputs, especially malicious ones.

- **Plan Removal**:
  - Ensure that only the assigned role can remove plans, in this case the marketing director
  - Verify if the plan was actually removed after its removal

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

## 6. Dashboard

### 6.1. Authentication and Session Management

- **Objective**: Ensure only authenticated users can access dashboard endpoints and JWT tokens are handled securely.

- **Threats Addressed**: Spoofing (STRIDE), JWT Token Theft ([ABUSE_CASE_002](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L41), [ABUSE_CASE_017](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L57)), Unauthorized Access.
- **Countermeasures Tested**: [DEFENSE_001](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L21) (JWT Security), General Requirement 1 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L54)).
- **Test Cases**:
  - **TC_DASH_AUTH_001**: Attempt to access a dashboard endpoint without a JWT token.
    - *Expected Result*: Access denied (e.g., 401 Unauthorized).
  - **TC_DASH_AUTH_002**: Attempt to access a dashboard endpoint with an invalid/expired JWT token.
    - *Expected Result*: Access denied (e.g., 401 Unauthorized or 403 Forbidden).
  - **TC_DASH_AUTH_003**: Attempt to access a dashboard endpoint with a JWT token signed with an incorrect key.
    - *Expected Result*: Access denied.
  - **TC_DASH_AUTH_004**: Verify that dashboard endpoints require HTTPS.
    - *Expected Result*: HTTP requests are rejected or redirected to HTTPS. ([DEFENSE_008](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L28), General Requirement 3 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L56))).

### 6.2. Authorization and Access Control (RBAC)

- **Objective**: Verify that only users with appropriate roles (Project Manager, Financial Director) can access dashboard functionalities.

- **Threats Addressed**: Spoofing, Elevation of Privilege ([STRIDE.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\STRIDE.md#L82)), Unauthorized Access ([ABUSE_CASE_016](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L55)), Unauthorized Access / Elevation of Privilege to Dashboard ([DREAD.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\DREAD.md#L23)).
- **Countermeasures Tested**: [DEFENSE_011](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L31) (RBAC), General Requirement 1 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L54)).
- **Test Cases**:
  - **TC_DASH_RBAC_001**: Attempt to access a dashboard endpoint with a valid JWT from a user with a "Subscriber" role.
    - *Expected Result*: Access denied (e.g., 403 Forbidden).
  - **TC_DASH_RBAC_002**: Attempt to access a dashboard endpoint with a valid JWT from an "Admin" role (or any other role not explicitly granted dashboard access).
    - *Expected Result*: Access denied (e.g., 403 Forbidden).
  - **TC_DASH_RBAC_003**: Access a dashboard endpoint with a "Project Manager" role.
    - *Expected Result*: Access granted, data returned.
  - **TC_DASH_RBAC_004**: Access a dashboard endpoint with a "Financial Director" role.
    - *Expected Result*: Access granted, data returned.

### 6.3. Input Validation and Parameter Tampering

- **Objective**: Ensure that all inputs to dashboard endpoints are validated to prevent injection, data manipulation, and DoS.

- **Threats Addressed**: Tampering ([STRIDE.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\STRIDE.md#L77)), Parameter Tampering / Injection on Dashboard ([DREAD.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\DREAD.md#L21)), ([ABUSE_CASE_018](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L58), [ABUSE_CASE_019](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L59), [ABUSE_CASE_020](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L60)).
- **Countermeasures Tested**: [DEFENSE_005](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L25) (Input Validation), [DEFENSE_013](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L33) (Parameter Validation), General Requirement 2 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L55)).
- **Test Cases** (assuming parameters like `startDate`, `endDate`, `plan`, `numberMonth`):
  - **TC_DASH_INPUT_001**: Send requests with missing required parameters (e.g., `startDate` for a time-bound report).
    - *Expected Result*: Request rejected with a clear error message (e.g., 400 Bad Request).
  - **TC_DASH_INPUT_002**: Send requests with parameters of invalid data types (e.g., "abc" for a date, "text" for `numberMonth`).
    - *Expected Result*: Request rejected.
  - **TC_DASH_INPUT_003**: Send requests with out-of-range or logically invalid values (e.g., invalid date formats, negative `numberMonth`, `endDate` before `startDate`, extremely large date ranges).
    - *Expected Result*: Request rejected or handled gracefully.
  - **TC_DASH_INPUT_004**: Send requests with malicious payloads in parameters (e.g., SQLi fragments like `' OR '1'='1`, XSS payloads like `<script>alert(1)</script>`).
    - *Expected Result*: Input is sanitized; no injection occurs; request is rejected or processed safely.
  - **TC_DASH_INPUT_005**: Send requests with unexpected or additional parameters not defined in the API specification.
    - *Expected Result*: Unexpected parameters are ignored, or the request is rejected.

### 6.4. Information Disclosure

- **Objective**: Prevent leakage of sensitive system information or excessive data through error messages or responses.

- **Threats Addressed**: Information Disclosure ([STRIDE.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\STRIDE.md#L79)), Information Disclosure from Dashboard ([DREAD.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\DREAD.md#L19)), ([ABUSE_CASE_021](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L61)).
- **Countermeasures Tested**: [DEFENSE_012](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L32) (Secure Error Handling), Generic Error Responses ([Phase2Analysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\Phase2Analysis.md#L96)).
- **Test Cases**:
  - **TC_DASH_INFO_001**: Trigger various errors (e.g., invalid input, authorization failure) and inspect the HTTP status codes and response bodies.
    - *Expected Result*: Error messages are generic (e.g., "An error occurred," "Invalid input") and do not reveal stack traces, SQL queries, internal paths, or other sensitive system details.
  - **TC_DASH_INFO_002**: Verify that successful responses only contain data relevant to the query and authorized for the user's role, not excessive or unrelated data.
    - *Expected Result*: Data is appropriately scoped and minimized.

### 6.5. Denial of Service (DoS)

- **Objective**: Ensure dashboard endpoints are resilient to common DoS attack vectors.

- **Threats Addressed**: Denial of Service ([STRIDE.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\STRIDE.md#L80)), Denial of Service (DoS) against Dashboard ([DREAD.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\DREAD.md#L20)), ([ABUSE_CASE_023](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L63), [ABUSE_CASE_024](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L64)).
- **Countermeasures Tested**: [DEFENSE_002](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L22) (Rate Limiting), [DEFENSE_015](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L35) (Resource Usage Limits), General Requirement 5 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L58)).
- **Test Cases**:
  - **TC_DASH_DOS_001**: Send a high volume of legitimate requests to a dashboard endpoint in a short period.
    - *Expected Result*: Rate limiting is triggered (e.g., HTTP 429 Too Many Requests), and the service remains stable.
  - **TC_DASH_DOS_002**: Send requests designed to consume excessive resources (e.g., queries with extremely large date ranges for reports, if not fully mitigated by input validation).
    - *Expected Result*: The system handles the request gracefully (e.g., timeout, specific error message for overly complex query) without impacting overall service availability.

### 6.6. Audit Logging and Repudiation

- **Objective**: Ensure that access to dashboard functionalities is logged to support auditability and address repudiation threats.

- **Threats Addressed**: Repudiation ([STRIDE.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\STRIDE.md#L78)), Repudiation of Dashboard Actions/Access ([DREAD.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\DREAD.md#L25)), ([ABUSE_CASE_025](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L65)).
- **Countermeasures Tested**: [DEFENSE_014](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\ssdlc\Abuse_Case.md#L34) (Audit Logging), User Requirement 9 ([ArchithectureAnalysis.md](c:\Users\rafae\OneDrive\Documentos\Mestrado\2Semestre\DESOFS\desofs2025_wed_ffs_4\Deliverables\Phase2\Sprint1\docs\ArchithectureAnalysis.md#L74)).
- **Test Cases**:
  - **TC_DASH_LOG_001**: Successfully access each dashboard endpoint/functionality with an authorized user (e.g., Project Manager).
    - *Expected Result*: Audit logs record the access, including user identification, timestamp, endpoint accessed, and key parameters used.
  - **TC_DASH_LOG_002**: Attempt to access dashboard endpoints with an unauthorized user (e.g., Subscriber) or trigger an error (e.g., invalid input).
    - *Expected Result*: Failed access attempts and significant errors related to dashboard access are logged for security monitoring.
