# Decision Log

Update this file when a project decision changes. Keep proposed sample values separate from confirmed campus policy.

## Discussed and accepted in principle

- [x] Start with a Java console app in IntelliJ IDEA.
- [x] Use Git/GitHub to preserve source code, requirements, test notes, and changes.
- [x] Explain in simple English, include comments, and test each stage.
- [x] Reduce friction during busy meal times; do not depend on multiple student scans per meal in a future deployment.
- [x] Food and nutrition estimates; meal records and a 14-day summary.
- [x] Food-waste tracking by dish and meal period.
- [x] Simulated camera detection with confidence, unknown-food correction, and duplicate event protection.
- [x] Weekly engagement points and optional student wellness check-in.
- [x] Low engagement may prompt a private reminder to the student, but must not diagnose or alert staff by itself.
- [x] A student can consent to share a check-in with authorized student-welfare staff; “Prefer not to share” remains private.
- [x] Separate cafeteria quality ratings from private wellness check-ins.
- [x] Show aggregated dish ratings and waste trends to food staff, not individual student wellness data.
- [x] Sick-student meal request and hostel room delivery status workflow.
- [x] Save the local prototype data across restarts and keep the data file out of Git.

## Current illustrative defaults (not final campus policy)

- Project name: **Smart Campus Food Rescue & Nutrition System**; earlier working title: **Campus Food Rescue System**.
- Java release: 17 or newer.
- Example point awards: 10 points for several voluntary activities; 15 for a voluntary wellness session.
- Example weekly engagement goal: 30 points.
- Example low-engagement reminder threshold: fewer than 20 points in a week.
- Cafeteria survey scale: 1–5 for taste, freshness/temperature, and portion suitability.
- Duplicate point claims: one award per activity/student/week.
- Duplicate dish survey: one rating per student/dish/day.
- Duplicate active sick-meal request: one per student.
- Initial nutrition facts are illustrative values only and not validated for actual recipes.
- Local persistence: Java serialization in `data/campus-data.ser` for the prototype only.

## Important implementation limits

- Main portal selection is a demo role switch, not authentication.
- Java serialized file is not encrypted and is not suitable for real sensitive student data.
- No live AI camera, scales, campus ID integration, external database, web/mobile UI, SMS/push notification, parent portal, or real food-delivery service is implemented.
- Low points do not automatically notify staff; explicit consent is required for a check-in to appear in the support portal.

## Decisions still open

- GitHub repository name and public/private visibility; a live repo has not been confirmed as created.
- Campus-approved authentication and authorized staff roles.
- Final point values, threshold, goal, consent language, and staff follow-up procedure.
- Meal entitlement/payment rules, delivery staff, service hours, and emergency contact path.
- Verified nutrition data source, recipes, and portion units.
- Camera/scale/QR integration and whether those measurements are practical without slowing queues.
- Database, graphical interface, optional wellness measurements, notification service, and retention/deletion policy.
- Parent/guardian access for adult students (must not be enabled by default).

## Version history

- Initial idea: Campus Food Rescue System.
- Expanded scope: Smart Campus Food Rescue & Nutrition System.
- Added student points, low-engagement supportive check-ins, separate cafeteria ratings, and sick-student meal requests.
- Initial multi-feature Java console prototype implemented with local persistence and nine passing smoke-test groups.
