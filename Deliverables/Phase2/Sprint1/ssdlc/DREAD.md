# Threat Hierarchy Analysis

DREAD is a quantitative risk assessment framework that evaluates threats using five criteria (Damage, Reproducibility, Exploitability, Affected users, Discoverability), each scored 1-10. The methodology allows security teams to objectively compare different threats and prioritize remediation efforts based on calculated risk scores.
The key concepts used in creating the table include systematic scoring of each threat across all five DREAD dimensions, risk level classification based on total scores, and a structured approach to analyzing attack vectors, impacts, and likelihood of exploitation.


| Rank | Threat                                    | Damage | Reproducibility | Exploitability | Affected Users | Discoverability | **Total Score** | **Risk Level** |
|------|-------------------------------------------|--------|-----------------|----------------|----------------|-----------------|-----------------|----------------|
| 1    | Malicious File Upload (Extended)          | 10     | 8               | 8              | 9              | 7               | **42**          | **CRITICAL**   |
| 2    | User Account Creation Process Abuse       | 8      | 9               | 7              | 9              | 8               | **41**          | **CRITICAL**   |
| 3    | Unauthorized Access to Subscriber Account | 9      | 7               | 8              | 7              | 9               | **40**          | **CRITICAL**   |
| 4    | User Account Login Attacks                | 8      | 8               | 7              | 8              | 9               | **40**          | **CRITICAL**   |
| 5    | Plan Creation/Update/Removal Exploitation | 7      | 8               | 6              | 6              | 5               | **32**          | **HIGH**       |
| 6    | Device Creation/Update Abuse              | 6      | 8               | 6              | 5              | 6               | **31**          | **HIGH**       |
