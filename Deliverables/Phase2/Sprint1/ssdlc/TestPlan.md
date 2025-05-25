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
