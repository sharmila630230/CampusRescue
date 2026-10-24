# Project Specification — Saved Discussion

## Current implementation status (2026-10-23)

The first multi-feature Java console prototype now implements the food catalogue/nutrition estimator, personal meal records and 14-day estimated summary, food-waste records/reports, simulated camera confidence/unknown-item correction and duplicate event protection, weekly points, consent-based private check-ins, aggregate cafeteria survey reports, sick-student hostel meal requests with status transitions, and local save/load. Nine dependency-free smoke-test groups pass in the authoring environment. The portal selector is only a prototype navigation aid and is not real authentication. Local serialized data is not encrypted. See `../README.md` and `CODE_GUIDE.md` for setup and source-file explanations.

This document is the central record of the ideas discussed so far. Treat proposed thresholds, point values, menu choices, and examples as adjustable design examples—not final campus policy.

## 1. Overall purpose

Build a beginner-friendly Java system for a campus cafeteria that can reduce food waste and support student nutrition awareness, student feedback, wellbeing check-ins, and meal access for students who are sick or unable to reach the canteen.

The first version is a console prototype. It will simulate data and workflows before we consider devices, an actual database, or a phone/web interface.

## 2. Food recognition and nutrition estimates

### Desired experience
- The cafeteria becomes busy when students arrive together; the solution should not create queues or require many extra scans.
- In the future, a serving-counter camera might recognize foods. Student identity could be linked through an existing campus ID/QR system only if practical and approved.
- A return-station camera might estimate leftovers.
- Use known recipes and standard serving references to estimate nutrient values.

### Accuracy limitations
- Image recognition may confuse similar dishes or fail with mixed foods, poor lighting, or partially covered plates.
- A camera alone cannot reliably measure grams served or eaten. An integrated scale, standardized portions, or a correction workflow may be needed.
- Do not claim exact consumed nutrition unless served portion and leftovers are measured/reliably linked. Show estimates and confidence/unknown-food states.
- The prototype should simulate detections and allow a user to choose/correct an unknown item before any real AI integration.

### Nutrient fields
At minimum, design `FoodItem`/`NutritionInfo` to support calories (kcal), protein, carbohydrates, fats, fibre, calcium, and iron per 100 g or per known serving. Later, confirm a consistent source and units for nutrition values.

Example calculation: if a food has 130 kcal per 100 g, a 200 g serving estimates 260 kcal. This is an example to test the formula, not a measured value for a real dish.

## 3. Food-waste tracking

- Record food item, meal/date/time period, amount prepared/served if available, and leftover/waste quantity.
- Summarize waste by dish and meal period.
- Use aggregate trends to help canteen staff improve quantities, recipes, and preparation.
- Do not use waste records to shame individual students or staff.
- Avoid presenting unreliable camera estimates as exact measurements.

## 4. Student engagement points

Points are intended to encourage optional participation—not measure a student's health, value, or academic performance.

Possible example point values to tune later:
- Optional nutrition-awareness check-in: +10.
- Nutrition/wellness learning activity: +10.
- Cafeteria feedback survey: +10.
- Voluntary wellness session: +15.
- Food-waste-awareness activity: +10.
- Example weekly engagement goal: 30 points.

Rules:
- Prevent duplicate claims for the same activity when appropriate.
- Do not award or remove points based on body weight, diagnoses, or private health outcomes.
- No penalty for a student who is busy, not participating, or has technical issues.
- Show each student their own points. Avoid public rankings and low-point leaderboards.

## 5. Low-engagement support check-in

A configurable threshold may prompt a gentle private check-in. Example discussed: fewer than 20 points in a week. The value is illustrative only and must be chosen with the campus, not treated as a health threshold.

Possible student response choices:
- Busy with classes/assignments.
- Unhappy with available food.
- Feeling tired or low on energy.
- Would like advice/support.
- Prefer not to say.

The system can offer the student the option to share their response with authorized student-welfare/hostel support staff. If they decline, keep it private except for clearly defined safety procedures. Design gentle, limited notifications and avoid repeated alerts after a staff member has already checked in.

Low points, no scan, or a missed survey do **not** prove that a student is ill, skipping meals, or needs intervention. Offer supportive guidance, not discipline or diagnosis.

## 6. Cafeteria food-quality survey

Separate food-quality feedback from the private wellbeing check-in.

Example food choices used in the design preview:
- Rice and dal.
- Sambar rice.
- Vegetable curry.
- Chapati and curry.

Survey fields:
- Dish/meal selected.
- Taste rating: 1–5 stars.
- Freshness/temperature rating: 1–5 stars.
- Portion suitability rating: 1–5 stars.
- Optional reason for skipping/not eating: taste, quality, lack of time, not hungry, other.
- Optional comment for the food team.

Food department/canteen staff should normally see aggregated dish-level results, such as average ratings, percentage of low ratings, recurring themes, waste trends, and whether ratings changed after a recipe/portion adjustment. Avoid exposing private feelings or personal wellness records to chefs.

Repeated low ratings or consistently high waste should prompt a respectful dish/preparation review, not automatic blame or punishment.

## 7. Sick-student meal request

A student who is unwell or cannot reach the canteen should be able to request an available meal for delivery to their hostel room.

### Form fields
- Food choice: rice and dal, curd rice, idli, khichdi/soft rice meal, or other available food.
- Quantity: half serving, one serving, or two servings.
- Hostel/block.
- Room number.
- Preferred time: as soon as possible or next available meal time.
- Optional food preference/allergy note.

Students should not have to disclose a diagnosis just to request a meal.

### Workflow/statuses
`Pending → Accepted → Preparing → Out for Delivery → Delivered`

Other possible outcomes: `Unavailable`, `Cancelled`.

Expected handling:
- Canteen team reviews availability and accepts or declines.
- If declined/unavailable, show the student a clear message and alternative where possible.
- A designated authorized hostel helper/delivery person brings the food.
- Student can view the status and receive a delivery confirmation.
- Prevent duplicate active requests, and allow cancellation before preparation where practical.
- Payment/meal-plan rules are not decided yet; confirm campus requirements later.
- Allergy notes must be checked against ingredients and preparation. Never promise that a dish is medically safe without verification.
- Show hostel medical/warden/emergency contact guidance for urgent health situations; meal delivery is not an emergency service.

## 8. Roles and information access

- **Student:** own records, points, check-ins, requests, and their own order statuses.
- **Canteen/food staff:** food availability and meal requests needed to prepare/deliver; aggregated dish feedback and waste reports.
- **Authorized student-welfare/hostel support staff:** limited support alerts or student responses the student has agreed to share, with a clear campus-defined safety exception policy.
- **Faculty:** only the minimum authorized information needed for an agreed support role; no blanket access to individual health/feelings data.
- **Parents:** for adult students, no access to private wellness information by default; sharing requires the student's authorization, subject to campus policy and applicable law.
- **System administrator:** role-based maintenance access with audit logging for sensitive views.

## 9. Planned Java concepts/classes

Build these incrementally rather than creating them all at once:
- `Student`
- `FoodItem` / `NutritionInfo`
- `MealRecord`
- `LeftoverRecord` / `FoodWasteReport`
- `StudentPoints` / `WellnessActivity`
- `StudentCheckIn`
- `FoodSurvey` / `FoodQualityReport`
- `SickMealRequest`
- `AlertService` / notification component
- `AccessControl` / roles
- Later: repository/database classes.

Names may change as we learn. Start with small classes that have one clear responsibility.

## 10. Development preferences

- User is a beginner learning Java in IntelliJ IDEA.
- Explain using simple English and step-by-step examples.
- Prefer improving current code instead of replacing everything unnecessarily.
- Discuss design choices without making the project too complicated at the start.
- Test each feature, normal cases, edge cases, invalid inputs, and errors before moving on.
- User wants to make their own feature changes and ask questions while developing.
- Preserve this specification and update it when a decision changes.
