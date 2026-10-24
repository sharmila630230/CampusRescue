# Development Roadmap

## Phase 0 — Preserve the idea and set up GitHub
- [x] Save the requirements and design decisions in Markdown.
- [x] Create a JDK 17+ Maven project structure.
- [ ] Create/publish the live GitHub repository (account connection or user upload still required).

## Phase 1 — Console menus and input validation
- [x] Main menu with Student, Canteen Staff, and Student-Welfare Support portals.
- [x] Loop through menus and return safely to the main menu.
- [x] Validate numeric input and handle malformed input without crashing.
- [ ] Manually try every menu path in the user's IntelliJ environment.

## Phase 2 — Food catalogue and nutrition calculator
- [x] Store food name/category and seven nutrition fields per 100 g.
- [x] Calculate proportional values from grams.
- [x] Add foods and toggle availability.
- [x] Add illustrative sample foods; confirm authoritative nutrition values before any real deployment.

## Phase 3 — Meal records and nutrition summary
- [x] Record a fictional student's food, grams served, and leftovers.
- [x] Show estimated nutrients based on the recorded eaten amount.
- [x] Show a 14-day summary of recorded meals.
- [ ] Add a separately linked return-station leftover flow after the event-matching design is agreed.

## Phase 4 — Food-waste tracking
- [x] Record grams prepared, grams served, and plate leftovers by meal period.
- [x] Calculate unserved waste + returned plate leftovers.
- [x] Aggregate reports by dish and meal period.
- [ ] Add trend charts and compare waste before/after portion or recipe changes.

## Phase 5 — Simulated camera/AI detection
- [x] Simulate a predicted food and confidence value.
- [x] Offer a human correction when confidence is low or the food is unknown.
- [x] Require a unique event ID for a saved camera-linked meal and block duplicate event IDs.
- [ ] Integrate a real camera or weighing device only after accuracy, queue impact, privacy, and campus approval are reviewed.

## Phase 6 — Engagement points and student support
- [x] Add example point values and weekly totals.
- [x] Block duplicate awards for the same activity/student/week.
- [x] Display a gentle low-points reminder to the student.
- [x] Do not alert staff based on points or missing scans alone.
- [x] Show support staff only check-ins that the student explicitly agreed to share.
- [x] Mark support follow-up complete to avoid a recurring queue item.
- [ ] Confirm final points, thresholds, consent rules, and who qualifies as authorized support staff.

## Phase 7 — Cafeteria feedback and reporting
- [x] Capture ratings of taste, freshness/temperature, and portion suitability (1–5).
- [x] Prevent the same student rating the same dish more than once per day.
- [x] Show aggregate per-dish averages, low-rating counts, and reason themes.
- [x] Hide student identifiers and raw individual comments from the canteen report.
- [ ] Add trend reporting after recipe changes and review retention rules for comments.

## Phase 8 — Sick-student meal request
- [x] Choose food, quantity, hostel/block, room, preferred time, optional preference/allergy note.
- [x] Block duplicate active requests for a student.
- [x] Support Pending → Accepted → Preparing → Out for Delivery → Delivered, with Unavailable/Cancelled outcomes.
- [x] Enforce cancellation and status-transition rules.
- [ ] Confirm meal payment/entitlement, service hours, delivery responsibility, and food safety policies with the campus.

## Phase 9 — Save/load and testing
- [x] Save local demo data and reload it after restart.
- [x] Add nine dependency-free smoke-test groups; currently passing in the authoring environment.
- [ ] Add broader automated tests and run tests in IntelliJ on the user's own setup.
- [ ] Replace local Java serialization with a database after the data model is stable.

## Phase 10 — Secure deployment and expanded interface (future)
- [ ] Add real campus authentication and role-based authorization; current role selector is only a demo.
- [ ] Add consent management, restricted retention, deletion/correction, and audit logs.
- [ ] Secure/encrypt sensitive records and protect personal data in transit and at rest.
- [ ] Build a graphical/web/mobile user interface and real notifications.
- [ ] Consider optional wellness measurements/trends only with explicit informed consent, suitable safeguards, and a clear non-diagnostic purpose.
- [ ] Plan parent access for adult students only with explicit authorization and campus policy review.

**Development rule:** run the current feature, learn how the code works, test its normal and edge cases, and then extend it. Keep all real hardware and medical/wellness data out of scope until the required safeguards exist.
