# Session Notes — Current Baseline

This file captures the decisions and current code state so a later session can continue without rethinking the project.

## User's learning preferences
- The user is a beginner learning Java in IntelliJ IDEA.
- Explain code in simple English and use comments that explain why a step exists.
- Prefer readable code and incremental improvement over unnecessary rewrites.
- Work through normal cases, edge cases, and errors; ask the user to run/check a feature before extending it.
- Preserve requirements and key decisions in this repository.

## Project purpose and features
The working project name is **Smart Campus Food Rescue & Nutrition System**; its earliest name was **Campus Food Rescue System**.

Implemented in the first console prototype:
1. Food catalogue with illustrative per-100-gram calories, protein, carbohydrates, fat, fibre, calcium, and iron.
2. Student meal records with serving/leftover weights and estimated eaten nutrients.
3. 14-day personal nutrition summary for recorded meals only.
4. Canteen food-waste records and dish/meal-period aggregate reports.
5. Simulated camera detection with confidence, unknown item/manual correction, portion warning, and unique event IDs to block duplicate camera-linked meal records.
6. Weekly optional student points with duplicate activity/week protection and a gentle low-points reminder.
7. Private student check-ins; support staff see only explicitly consented check-ins; follow-up completion removes items from the open queue.
8. Cafeteria ratings (taste, freshness/temperature, portion), optional reason/comment, and dish-level aggregate reports.
9. Sick-student meal requests with food/quantity, hostel/room, preferred time, optional preference/allergy note, duplicate-active-request protection, and status workflow.
10. Local save/load to `data/campus-data.ser`.
11. Dependency-free smoke tests.

## Essential behaviour and privacy rules
- Low points or missing scans are not proof of sickness or skipped meals and do not alert staff on their own.
- Student support responses appear to staff only when the student consents; “Prefer not to share” always remains private in this prototype.
- Canteen quality reports show dish-level aggregates and reason counts, not student IDs or individual comments.
- Adult parent access to student wellness data is not included and must require explicit authorization if later implemented.
- Allergy notes must be checked by canteen staff; the system does not guarantee a food is safe.
- Meal delivery is not an emergency service.
- Camera-only nutrition/portion estimates are uncertain. The current camera workflow is simulated, with a manually entered portion estimate.
- Food values in the initial catalogue are illustrative examples and must be replaced with verified nutrition/recipe data for real use.

## Current implementation and verification
- Maven/JDK 17 project layout is present.
- Main source files compile successfully with `javac --release 17` in the authoring environment.
- `PrototypeSelfTest` currently passes nine smoke-test groups. Run it from IntelliJ by opening `src/test/java/com/smartcampus/rescue/PrototypeSelfTest.java` and running its `main()`.
- Console exit path was manually tested.
- The data file uses Java serialization, is not encrypted, and is ignored by Git. Do not use real student data.
- The three portal options are demo navigation only, not secure login or production role-based authorization.

## Still future work
- Publish a live GitHub repository; this was not confirmed as created.
- Run all menus manually in the user's own IntelliJ setup.
- Replace local serialization with a database once the model is stable.
- Secure sign-in/roles, audit logs, data retention/deletion, consent management, secure storage, real notifications, graphical interface, actual camera/scale/campus ID, verified nutrition source, optional wellness trends, and approved meal-delivery logistics.
- Ask what the user sees when running the app and explain the next feature before adding complexity.
