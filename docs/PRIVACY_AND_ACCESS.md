# Privacy, Consent, and Access Rules

This is a proposed privacy baseline, not legal advice. The campus must review the final rules before collecting real student data. The current console role switch is only a prototype and does not enforce real identity authentication. The local serialized file is not encrypted; use fictional data only.

## Minimum-access design

| Role | Appropriate access |
|---|---|
| Student | Their own meal records, points, check-ins, feedback submission, meal requests, and status. |
| Canteen/food staff | Food availability, requests needed to prepare/deliver meals, and aggregated dish quality/waste reports. |
| Authorized student-welfare/hostel staff | Only support alerts and check-in responses the student has agreed to share, subject to a clearly defined safety policy. |
| Faculty | Only the minimum information required for a specific authorized student-support role; no blanket access to individual health/feelings data. |
| Parent/guardian | For an adult student, private wellness data is not available by default; require student authorization, subject to campus rules and applicable law. |
| Administrator | Role management and technical maintenance; sensitive data access should be limited and auditable. |

## Rules

1. Explain what is collected, why it is collected, who can see it, and how long it is kept.
2. Do not require a student to disclose a diagnosis to request food or a support conversation.
3. Do not infer illness, skipped meals, poor discipline, or wellbeing status from low points or a missing scan.
4. Do not punish students based on scores, meal records, or survey feedback.
5. Keep food-quality surveys separate from personal wellness check-ins. Give food teams aggregate trends whenever possible.
6. Ask the student before sharing their feelings with support staff. In the current prototype, “Prefer not to share” stays private, and only check-ins with an explicit share choice appear in the support queue. Document carefully defined emergency/safety exceptions before implementing them.
7. Collect only the data needed. Avoid storing exact room numbers or health notes beyond the time required for delivery/support.
8. The current portal switch is not secure role-based access. Implement campus authentication, authorization, and audit logs before using a database-backed version with real student information.
9. Use fictional records during development. Never push real student details, passwords, tokens, or API keys to GitHub.
10. Keep a student-facing correction/withdrawal process and clear retention/deletion policy as part of later design.
