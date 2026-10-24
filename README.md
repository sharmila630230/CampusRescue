# Smart Campus Food Rescue & Nutrition System

> A beginner-friendly Java console prototype for reducing campus food waste and supporting student nutrition awareness, food feedback, student wellbeing check-ins, and hostel meal access.

**Current status:** The first multi-feature console prototype is implemented and compiled. Nine dependency-free smoke tests pass. This is not yet a production campus system: role selection is not secure authentication, there is no real AI camera, and no SMS/email/push notifications are sent.

## Features in this version

1. **Food catalogue and nutrition estimates** — food name/category and estimated calories, protein, carbohydrates, fats, fibre, calcium, and iron per 100 g; portion estimates are scaled from those example values.
2. **Meal records and 14-day student summary** — record serving size and leftovers; estimate nutrients for the amount recorded as eaten.
3. **Food-waste tracking** — record amount prepared, served, and plate leftovers; report waste by dish and meal period.
4. **Simulated camera recognition** — simulate a food prediction, confidence score, unknown-food correction, and a unique event ID to block duplicate event records.
5. **Weekly student engagement points** — examples include learning activities, voluntary wellness sessions, waste-awareness activities, check-ins, and survey feedback. Duplicate awards for the same activity in the same week are blocked.
6. **Private student check-in** — students choose how they are feeling and can give consent to share an answer with authorized support staff. Low points alone never create a staff alert.
7. **Cafeteria food survey** — 1–5 ratings for taste, freshness/temperature, and portion suitability, plus optional reason and comment. The canteen report displays dish-level aggregates, not student IDs or individual comments.
8. **Sick-student hostel meal request** — food, quantity, hostel/block, room, preferred delivery time, optional food preference/allergy note, duplicate active-request protection, and a controlled request-status workflow.
9. **Local save/load** — the prototype saves its records to `data/campus-data.ser` so they can survive app restarts. This file is not encrypted and is excluded from Git.
10. **Input validation and smoke tests** — validation for invalid amounts, star ratings, duplicate actions, invalid request transitions, and more.

## Important safety and accuracy choices

- Sample nutrition values are **illustrative examples**, not verified nutrition data for actual recipes. Confirm portion sizes and use an authoritative recipe/nutrition source before real use.
- Camera recognition and confidence are simulated. A camera alone cannot know exact food weight or how much a student ate; reliable portion and leftover measurements are required.
- Use fictional student details only. The local serialized data file is not encrypted and should not contain real sensitive student information.
- The portal choice is only a **demo role switch**, not secure login or true role-based authentication. Do not deploy this prototype with real student data.
- Low points, missing scans, and missed surveys are not evidence of illness. They do not trigger a staff alert by themselves. A check-in appears in the support queue only if the student consents to sharing it; “Prefer not to share” remains private.
- Food quality and food waste are shown as dish-level aggregates. Do not use them to shame an individual student or staff member.
- Allergy notes must be checked against ingredients and preparation. The app never guarantees allergy safety.
- Meal delivery is not an emergency service. Urgent health needs should be directed to the campus medical service/hostel warden.
- For adult students, parent access to personal wellbeing details is not implemented and must not be enabled by default.

## Open and run it in IntelliJ IDEA

1. Download/unzip the project folder.
2. In IntelliJ IDEA, choose **File → Open**, then select the folder containing `pom.xml`.
3. Set the Project SDK to **JDK 17 or newer** and allow the project to load.
4. Open `src/main/java/com/smartcampus/rescue/App.java` and run `App.main()`.
5. Use the main menu to open the Student, Canteen Staff, or Student-Welfare Support portal. Demo student IDs are `STU001`, `STU002`, and `STU003`.
6. To run the smoke tests, open `src/test/java/com/smartcampus/rescue/PrototypeSelfTest.java` and run its `main()` method.

### Local data

The program writes `data/campus-data.ser` in the current working directory. This is local demo persistence implemented with Java serialization; it is not a production database and it is not encrypted. Keep a backup before any manual reset. `.gitignore` excludes the file so it is not uploaded to GitHub.

## Repository layout

```text
CampusRescue/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/com/smartcampus/rescue/
│   │   ├── App.java
│   │   ├── CampusData.java
│   │   ├── CampusService.java
│   │   ├── CampusStore.java
│   │   ├── FoodItem.java
│   │   ├── FoodSurvey.java
│   │   ├── MealRecord.java
│   │   ├── MealRequestStatus.java
│   │   ├── MealPeriod.java
│   │   ├── MealQuantity.java
│   │   ├── NutritionFacts.java
│   │   ├── PointActivity.java
│   │   ├── PointTransaction.java
│   │   ├── SickMealRequest.java
│   │   ├── Student.java
│   │   ├── StudentCheckIn.java
│   │   ├── StudentFeeling.java
│   │   ├── SurveyReason.java
│   │   └── WasteRecord.java
│   └── test/java/com/smartcampus/rescue/PrototypeSelfTest.java
└── docs/
    ├── PROJECT_SPEC.md
    ├── ROADMAP.md
    ├── TEST_PLAN.md
    ├── PRIVACY_AND_ACCESS.md
    ├── DECISIONS.md
    ├── SESSION_NOTES.md
    └── GITHUB_SETUP.md
```

## Smoke-test results

The dependency-free smoke test suite has passed these nine groups:

1. Nutrition scales proportionally with portion grams.
2. Invalid serving/leftover amounts are rejected.
3. Waste totals are calculated and validated.
4. Duplicate weekly points are blocked.
5. Duplicate camera event IDs are blocked.
6. Survey ratings and duplicate survey submissions are validated.
7. Student support queue respects consent and follow-up status.
8. Meal requests protect against duplicates and invalid status/cancellation actions.
9. Local records survive a save/load restart test.

## Development principles

- Keep the code understandable and add one feature at a time.
- Use comments to explain why validation and privacy rules exist.
- Test common cases, edge cases, and errors before extending the project.
- Keep the saved requirements and decisions in `docs/` up to date.
- Commit code and documentation to GitHub, but never commit local data files, personal information, passwords, tokens, or API keys.

## Planned next steps

- Review and run every menu in IntelliJ.
- Add stronger automated tests and improve any behaviours discovered during manual testing.
- Move from Java serialization to a database such as SQLite after the data model is stable.
- Add real authentication, permissions, consent revocation, retention rules, and audit logs before handling real student data.
- Design a student-facing GUI/web/mobile interface.
- Only after campus approval, assess camera/scale/campus-ID integration and real notification/delivery workflows.

The first working Java version demonstrates the project's main workflows, but real hardware, verified nutrition data, secure multi-user access, and actual notifications require separate implementation and review.
