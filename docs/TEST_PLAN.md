# Test Plan and Edge Cases

This file distinguishes tests already executed from future/manual tests. Smoke tests are implemented in `src/test/java/com/smartcampus/rescue/PrototypeSelfTest.java` and run as that class's `main()` method.

## Automated smoke tests — PASS
- [x] Nutrition scales correctly: 130 kcal per 100 g × 200 g = 260 kcal.
- [x] Negative serving weight is rejected.
- [x] Leftovers larger than the served amount are rejected.
- [x] Waste total = prepared minus served + plate leftovers; served cannot exceed prepared.
- [x] Duplicate points claim for the same activity/student/week is blocked.
- [x] Duplicate camera event ID does not create a second personal meal record.
- [x] Ratings outside 1–5 are rejected.
- [x] Same student cannot rate the same dish more than once in one day.
- [x] Check-ins without consent and “Prefer not to share” are hidden from support staff.
- [x] Consented support check-in appears in the open queue and leaves after follow-up is marked complete.
- [x] Duplicate active sick-meal request is blocked.
- [x] Invalid cancellation after preparation starts is blocked.
- [x] Completed meal request cannot move backwards to an earlier status.
- [x] Local data survives save/load.

## Manual console tests still recommended

### Console menu
- [ ] Each valid option opens the expected section.
- [ ] Text where a number is expected does not crash the program.
- [ ] Out-of-range menu number produces a helpful message.
- [ ] Empty food catalogue is handled.
- [ ] Exit ends cleanly.

### Food and nutrition
- [ ] A valid food is listed and its availability can be changed.
- [ ] A new food accepts all nutrition fields.
- [ ] Unknown food/student IDs show a helpful error.
- [ ] Zero consumed grams produce zero nutrient estimates; negative grams are rejected.
- [ ] Values are clearly labelled as estimates and use consistent units.

### Camera simulation
- [ ] High confidence continues with the predicted item.
- [ ] Low confidence offers manual correction.
- [ ] Unknown/mixed dish can be corrected or ignored.
- [ ] Reusing a camera event ID is rejected.
- [ ] Portion input is not claimed to have been measured by the camera.
- [ ] Unlinked detection does not create a personal nutrition record.

### Waste reports
- [ ] No waste records show a clear empty message.
- [ ] Waste is grouped by dish and meal period.
- [ ] Invalid prepared/served/leftover quantities are rejected.
- [ ] Reports do not blame individual students or staff.

### Points and check-ins
- [ ] Below-threshold student sees a private, gentle reminder.
- [ ] Reminder does not send staff notification by itself.
- [ ] Student can choose each feeling, including “Prefer not to share.”
- [ ] Student can decline to share; support queue does not display that answer.
- [ ] Repeated check-in in the same week is blocked.
- [ ] Staff can mark a consented follow-up complete.
- [ ] There is no public low-score leaderboard or punishment.

### Cafeteria survey
- [ ] Ratings 1–5 are accepted; values outside the range are rejected.
- [ ] Comments are optional and length-limited.
- [ ] Empty report displays a clear no-data message.
- [ ] Aggregate averages and reason themes display correctly.
- [ ] Student IDs and raw individual comments do not appear in the canteen report.

### Sick-student meal requests
- [ ] Blank hostel, room, food, and required fields are rejected.
- [ ] Unavailable food cannot be ordered from the current catalogue.
- [ ] Duplicate active request is rejected.
- [ ] Student sees only their own requests from the student portal.
- [ ] Student can cancel while pending/accepted, but not after preparation begins.
- [ ] Status order is enforced; unavailable/cancelled/delivered requests close.
- [ ] Allergy/preference notes are shown only in the canteen preparation workflow and are not a guarantee of safety.
- [ ] Urgent medical need is not handled as a meal order.

### Privacy and reliability — future deployment requirements
- [ ] Replace demo portal switching with real authentication and authorization.
- [ ] Prevent students from opening another student's private records.
- [ ] Audit sensitive staff access in the database-backed version.
- [ ] Parent access for an adult student stays off unless explicitly authorized.
- [ ] Test data recovery/backup and corrupted-file handling.
- [ ] Test multiple concurrent users, database/network failures, and duplicate notification retries.
- [ ] Never commit local data, real student details, passwords, tokens, or API keys.
