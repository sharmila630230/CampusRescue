# Code Guide — Read This Before Modifying the Project

This file explains the responsibilities of the Java classes in simple terms.

## Application and storage

- `App.java` — The text menus. It asks for input, displays information, and calls the service. Try to keep business rules out of the menu where practical.
- `CampusService.java` — The main business rules: validates records, blocks duplicate points and camera event IDs, checks consent before support reporting, and controls meal request transitions.
- `CampusData.java` — The lists of records used by the prototype. It also handles older saved prototype files that may not have newer lists yet.
- `CampusStore.java` — Loads and saves `CampusData` to `data/campus-data.ser`. This is a simple local prototype store, not encrypted or suitable for real sensitive data.

## Food and nutrition

- `FoodItem.java` — One dish in the menu and its nutrition values per 100 grams.
- `NutritionFacts.java` — Holds the seven nutrition values and calculates a portion estimate. Example: 130 kcal per 100 g × 200 g = 260 kcal.
- `MealRecord.java` — One student's food record, the amount served, the entered leftovers, and the estimated amount eaten.
- `WasteRecord.java` — Canteen-level prepared/served/leftover amounts for one dish and one meal period.
- `MealPeriod.java` — Breakfast, lunch, evening snack, or dinner.

## Points and student support

- `PointActivity.java` — Activity labels and example point values.
- `PointTransaction.java` — One points award for one student, activity, and week.
- `StudentFeeling.java` — Options on the private check-in form.
- `StudentCheckIn.java` — A student's check-in, optional note, sharing consent, and follow-up state.

## Cafeteria feedback

- `FoodSurvey.java` — One dish survey with three ratings, an optional reason, and comment.
- `SurveyReason.java` — Structured reason choices that can be summarized without showing individual comments.

## Sick-student meal requests

- `SickMealRequest.java` — Stores food, quantity, hostel/room, preferred time, optional food note, and current status. It validates which status transitions are allowed.
- `MealQuantity.java` — Half, one, or two servings.
- `MealRequestStatus.java` — Pending, Accepted, Preparing, Out for Delivery, Delivered, Unavailable, and Cancelled.

## Tests

- `src/test/java/com/smartcampus/rescue/PrototypeSelfTest.java` — A small dependency-free test runner. Open it in IntelliJ and run its `main()` method. It prints PASS for each test group and stops if a test fails.

## Suggested safe editing order

1. Run `App.main()` and explore the menus using fictional demo profiles.
2. Run `PrototypeSelfTest.main()` and confirm each test passes.
3. Choose one small behaviour, change it, and run the tests again.
4. Add a test for a new rule before adding multiple new features.
5. Do not commit `data/campus-data.ser` or any real personal/student information.

## What this code does not yet do

There is no live camera, image-recognition model, scale, campus ID/QR connection, database, encrypted storage, actual SMS/push notification, GUI, parent portal, or real delivery tracking integration. The current console flow simulates these decisions so they can be understood and tested before a real integration is designed.
