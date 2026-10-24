package com.smartcampus.rescue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Small dependency-free smoke test suite. Run its main() from IntelliJ or with
 * java -cp target/classes:target/test-classes com.smartcampus.rescue.PrototypeSelfTest
 */
public final class PrototypeSelfTest {
    private static int passed = 0;

    public static void main(String[] args) throws Exception {
        testNutritionScaling();
        testInvalidMealWeights();
        testWasteCalculation();
        testDuplicatePointsAreBlocked();
        testCameraDuplicateDetectionIsBlocked();
        testSurveyRatingsAndDuplicateSurvey();
        testPrivateCheckInsNeedConsent();
        testSickMealWorkflowAndDuplicateProtection();
        testLocalDataPersistence();
        System.out.println("\nALL SMOKE TESTS PASSED: " + passed);
    }

    private static CampusService freshService() {
        CampusService service = new CampusService(new CampusData(), ignored -> { });
        service.seedDemoDataWhenNeeded();
        return service;
    }

    private static void testNutritionScaling() {
        NutritionFacts per100g = new NutritionFacts(130, 4, 22, 2, 3, 25, 1.2);
        NutritionFacts for200g = per100g.forGrams(200);
        checkClose(260, for200g.getCaloriesKcal(), "200 g should be twice the 100 g calories");
        checkClose(8, for200g.getProteinGrams(), "Protein should scale with portion grams");
        pass("nutrition scales by grams");
    }

    private static void testInvalidMealWeights() {
        expectThrows(IllegalArgumentException.class,
                () -> new MealRecord("STU001", "F001", 100, 101, 1, "TEST"),
                "leftovers cannot exceed the served amount");
        expectThrows(IllegalArgumentException.class,
                () -> new MealRecord("STU001", "F001", -1, 0, 1, "TEST"),
                "negative serving weight is rejected");
        pass("invalid meal weights are rejected");
    }

    private static void testWasteCalculation() {
        WasteRecord record = new WasteRecord("F001", MealPeriod.LUNCH, 1000, 800, 100);
        checkClose(300, record.getTotalWasteGrams(), "Waste is unserved 200 g plus plate leftovers 100 g");
        expectThrows(IllegalArgumentException.class,
                () -> new WasteRecord("F001", MealPeriod.LUNCH, 100, 101, 0),
                "served weight cannot exceed prepared weight");
        pass("waste calculation and validation");
    }

    private static void testDuplicatePointsAreBlocked() {
        CampusService service = freshService();
        LocalDate today = LocalDate.now();
        check(service.awardPoints("STU001", PointActivity.LEARNING_ACTIVITY, today), "first activity should award points");
        check(!service.awardPoints("STU001", PointActivity.LEARNING_ACTIVITY, today), "duplicate weekly activity should not award points");
        check(service.getWeeklyPoints("STU001", today) == 10, "duplicate claim must not increase the total");
        pass("duplicate weekly points are blocked");
    }

    private static void testCameraDuplicateDetectionIsBlocked() {
        CampusService service = freshService();
        service.recordCameraMeal("CAM-0001", "STU001", "F001", 200, 0, 0.92);
        expectThrows(IllegalStateException.class,
                () -> service.recordCameraMeal("CAM-0001", "STU001", "F001", 200, 0, 0.92),
                "the same camera event ID must not create two meal records");
        check(service.getMealsForStudent("STU001").size() == 1, "duplicate event must not duplicate meal records");
        pass("duplicate camera events are blocked");
    }

    private static void testSurveyRatingsAndDuplicateSurvey() {
        expectThrows(IllegalArgumentException.class,
                () -> new FoodSurvey("STU001", "F001", 0, 3, 4, SurveyReason.TASTE, ""),
                "star rating must be from 1 through 5");
        CampusService service = freshService();
        service.submitSurvey("STU001", "F001", 4, 4, 3, SurveyReason.NONE, "Good overall");
        expectThrows(IllegalStateException.class,
                () -> service.submitSurvey("STU001", "F001", 5, 5, 5, SurveyReason.NONE, "second rating same day"),
                "same student cannot rate the same dish twice in one day");
        pass("survey validation and duplicate prevention");
    }

    private static void testPrivateCheckInsNeedConsent() {
        StudentCheckIn privateEntry = new StudentCheckIn("STU001", StudentFeeling.WANT_SUPPORT,
                "I need guidance", false);
        check(!privateEntry.isVisibleToSupportStaff(), "no-consent check-in must stay private");
        StudentCheckIn preferNot = new StudentCheckIn("STU002", StudentFeeling.PREFER_NOT_TO_SAY,
                "", true);
        check(!preferNot.isVisibleToSupportStaff(), "prefer-not-to-say must stay private");

        CampusService service = freshService();
        service.createCheckIn("STU001", StudentFeeling.WANT_SUPPORT, "Please talk to me", true);
        check(service.getOpenSupportCheckIns().size() == 1, "consented check-in should appear in support queue");
        String checkInId = service.getOpenSupportCheckIns().get(0).getId();
        service.markSupportFollowUpDone(checkInId);
        check(service.getOpenSupportCheckIns().isEmpty(), "completed follow-up should leave open queue");
        pass("support queue respects consent and follow-up status");
    }

    private static void testSickMealWorkflowAndDuplicateProtection() {
        CampusService service = freshService();
        SickMealRequest request = service.createMealRequest("STU002", "F001", "",
                MealQuantity.ONE, "Demo Hostel", "101", "As soon as possible", "Less spicy");
        expectThrows(IllegalStateException.class,
                () -> service.createMealRequest("STU002", "F002", "", MealQuantity.HALF,
                        "Demo Hostel", "101", "Next available meal time", ""),
                "only one active request per student");
        service.updateMealRequestStatus(request.getRequestId(), MealRequestStatus.ACCEPTED, "Accepted");
        service.updateMealRequestStatus(request.getRequestId(), MealRequestStatus.PREPARING, "Preparing");
        expectThrows(IllegalStateException.class,
                () -> service.cancelOwnRequest("STU002", request.getRequestId()),
                "student cannot cancel after food preparation starts");
        service.updateMealRequestStatus(request.getRequestId(), MealRequestStatus.OUT_FOR_DELIVERY, "On the way");
        service.updateMealRequestStatus(request.getRequestId(), MealRequestStatus.DELIVERED, "Delivered");
        check(request.getStatus() == MealRequestStatus.DELIVERED, "request should finish in delivered status");
        expectThrows(IllegalStateException.class,
                () -> service.updateMealRequestStatus(request.getRequestId(), MealRequestStatus.ACCEPTED, "Invalid rewind"),
                "closed request cannot move back to accepted");
        pass("meal request duplicate checks, status workflow, and cancellation rules");
    }

    private static void testLocalDataPersistence() throws Exception {
        Path directory = Files.createTempDirectory("smart-campus-test-");
        Path file = directory.resolve("campus-data.ser");
        CampusStore store = new CampusStore(file);
        CampusData data = new CampusData();
        CampusService service = new CampusService(data, store::save);
        service.seedDemoDataWhenNeeded();
        service.recordMeal("STU001", "F001", 200, 25, 1, "TEST");
        CampusData loaded = store.load();
        check(loaded.foods.size() == 7, "food catalogue should survive save/load");
        check(loaded.mealRecords.size() == 1, "meal record should survive save/load");
        Files.deleteIfExists(file);
        Files.deleteIfExists(directory);
        pass("local demo data survives an application restart");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void checkClose(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void expectThrows(Class<? extends Throwable> expected,
                                     Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) return;
            throw new AssertionError(message + ": wrong exception " + actual, actual);
        }
        throw new AssertionError(message + ": expected " + expected.getSimpleName());
    }

    private static void pass(String name) {
        passed++;
        System.out.println("PASS " + passed + ": " + name);
    }
}
