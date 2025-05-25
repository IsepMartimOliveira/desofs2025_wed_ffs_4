# Threat Hierarchy Analysis

DREAD is a quantitative risk assessment framework that evaluates threats using five criteria (Damage, Reproducibility, Exploitability, Affected users, Discoverability), each scored 1-10. The methodology allows security teams to objectively compare different threats and prioritize remediation efforts based on calculated risk scores. The total score is calculated as the mean of the five criteria.
The key concepts used in creating the table include systematic scoring of each threat across all five DREAD dimensions, risk level classification based on total scores, and a structured approach to analyzing attack vectors, impacts, and likelihood of exploitation.

## Risk Level Glossary (Based on Mean Score)

* **CRITICAL (8.0 - 10.0):** Highest risk. Immediate attention required. Threat could cause severe damage, is easily reproducible/exploitable, affects many users, and is highly discoverable.
* **HIGH (6.0 - 7.9):** Significant risk. Priority attention. Threat could cause considerable damage, is moderately reproducible/exploitable, affects a notable number of users, and is reasonably discoverable.
* **MEDIUM (4.0 - 5.9):** Moderate risk. Attention required as resources permit. Threat could cause noticeable damage, has average reproducibility/exploitability, affects some users, and has moderate discoverability.
* **LOW (2.0 - 3.9):** Low risk. Monitor. Threat causes minor damage, is difficult to reproduce/exploit, affects few users, and is hard to discover.
* **VERY LOW (0 - 1.9):** Minimal risk. Acceptable risk or address if trivial. Threat causes negligible damage, is very difficult to reproduce/exploit, affects very few users, and is very hard to discover.

| Rank | Threat                                    | Damage | Reproducibility | Exploitability | Affected Users | Discoverability | **Mean Score** | **Risk Level** |
|------|-------------------------------------------|--------|-----------------|----------------|----------------|-----------------|----------------|----------------|
| 1    | Malicious File Upload (Extended)          | 10     | 8               | 8              | 9              | 7               | **8.4**        | **CRITICAL**   |
| 2    | User Account Creation Process Abuse       | 8      | 9               | 7              | 9              | 8               | **8.2**        | **CRITICAL**   |
| 3    | Unauthorized Access to Subscriber Account | 9      | 7               | 8              | 7              | 9               | **8.0**        | **CRITICAL**   |
| 4    | User Account Login Attacks                | 8      | 8               | 7              | 8              | 9               | **8.0**        | **CRITICAL**   |
| 5    | Information Disclosure from Dashboard     | 7      | 8               | 6              | 6              | 7               | **6.8**        | **HIGH**       |
| 6    | Denial of Service (DoS) against Dashboard | 6      | 8               | 7              | 5              | 8               | **6.8**        | **HIGH**       |
| 7    | Parameter Tampering / Injection on Dashboard| 7      | 8               | 6              | 6              | 6               | **6.6**        | **HIGH**       |
| 8    | Plan Creation/Update/Removal Exploitation | 7      | 8               | 6              | 6              | 5               | **6.4**        | **HIGH**       |
| 9    | Device Creation/Update Abuse              | 6      | 8               | 6              | 5              | 6               | **6.2**        | **HIGH**       |
| 10   | Unauthorized Access / Elevation of Privilege to Dashboard | 8      | 6               | 6              | 6              | 5               | **6.2**        | **HIGH**       |
| 11   | Repudiation of Dashboard Actions/Access   | 3      | 9               | 8              | 2              | 4               | **5.2**        | **MEDIUM**     |
