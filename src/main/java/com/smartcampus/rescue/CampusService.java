package com.smartcampus.rescue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Business rules for the project. Console menus call this class rather than
 * directly editing data, which makes validation and tests easier.
 */
public final class CampusService {
    public static final int LOW_POINTS_THRESHOLD = 20; // Example threshold, not a health threshold.
    public static final int WEEKLY_POINTS_GOAL = 30;   // Example goal; campus policy may change it.

    private final CampusData data;
    private final Consumer<CampusData> saveAction;

    public CampusService(CampusData data, Consumer<CampusData> saveAction) {
        this.data = data;
        this.saveAction = saveAction == null ? ignored -> { } : saveAction;
    }

    public void seedDemoDataWhenNeeded() {
        boolean changed = false;
        if (data.foods.isEmpty()) {
            // Illustrative numbers only. Verify recipes/portion sizes before real nutrition use.
            data.foods.add(new FoodItem("F001", "Rice and dal", "Meal",
                    new NutritionFacts(130, 4.5, 24, 2.0, 2.5, 25, 1.2), true));
            data.foods.add(new FoodItem("F002", "Sambar rice", "Meal",
                    new NutritionFacts(145, 4.0, 27, 2.5, 3.0, 30, 1.1), true));
            data.foods.add(new FoodItem("F003", "Vegetable curry", "Side dish",
                    new NutritionFacts(95, 2.5, 12, 4.0, 3.5, 45, 1.4), true));
            data.foods.add(new FoodItem("F004", "Chapati", "Main dish",
                    new NutritionFacts(280, 8.0, 45, 7.0, 6.0, 25, 2.2), true));
            data.foods.add(new FoodItem("F005", "Curd rice", "Meal",
                    new NutritionFacts(125, 3.5, 20, 3.0, 0.5, 85, 0.4), true));
            data.foods.add(new FoodItem("F006", "Idli", "Breakfast",
                    new NutritionFacts(150, 4.0, 30, 0.8, 2.0, 15, 1.0), true));
            data.foods.add(new FoodItem("F007", "Khichdi / soft rice meal", "Meal",
                    new NutritionFacts(120, 4.0, 21, 2.5, 3.0, 22, 1.3), true));
            changed = true;
        }
        if (data.students.isEmpty()) {
            // Fictional profiles so the demo can be tried without entering real details.
            data.students.add(new Student("STU001", "Demo Student A"));
            data.students.add(new Student("STU002", "Demo Student B"));
            data.students.add(new Student("STU003", "Demo Student C"));
            changed = true;
        }
        if (changed) save();
    }

    private void save() { saveAction.accept(data); }

    public List<FoodItem> getFoods() {
        return data.foods.stream().sorted(Comparator.comparing(FoodItem::getId)).toList();
    }

    public List<FoodItem> getAvailableFoods() {
        return data.foods.stream().filter(FoodItem::isAvailable)
                .sorted(Comparator.comparing(FoodItem::getId)).toList();
    }

    public FoodItem getFood(String id) {
        if (id == null) return null;
        return data.foods.stream().filter(food -> food.getId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    public FoodItem addFood(String name, String category, NutritionFacts nutritionPer100g) {
        String id = String.format(Locale.ROOT, "F%03d", data.foods.size() + 1);
        while (getFood(id) != null) {
            id = String.format(Locale.ROOT, "F%03d", Integer.parseInt(id.substring(1)) + 1);
        }
        FoodItem food = new FoodItem(id, name, category, nutritionPer100g, true);
        data.foods.add(food);
        save();
        return food;
    }

    public void setFoodAvailable(String foodId, boolean available) {
        FoodItem food = requireFood(foodId);
        food.setAvailable(available);
        save();
    }

    public List<Student> getStudents() {
        return data.students.stream().sorted(Comparator.comparing(Student::getId)).toList();
    }

    public Student getStudent(String id) {
        if (id == null) return null;
        return data.students.stream().filter(student -> student.getId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    public Student registerStudent(String id, String displayName) {
        String normalizedId = id == null ? "" : id.trim().toUpperCase();
        if (normalizedId.isEmpty()) throw new IllegalArgumentException("Student ID cannot be empty.");
        if (getStudent(normalizedId) != null) throw new IllegalArgumentException("That student ID already exists.");
        Student student = new Student(normalizedId, displayName);
        data.students.add(student);
        save();
        return student;
    }

    public MealRecord recordMeal(String studentId, String foodId, double gramsServed,
                                 double gramsLeftover, double confidence, String source) {
        requireStudent(studentId);
        FoodItem food = requireFood(foodId);
        if (!food.isAvailable()) throw new IllegalArgumentException("That food is currently marked unavailable.");
        MealRecord record = new MealRecord(studentId, foodId, gramsServed, gramsLeftover, confidence, source);
        data.mealRecords.add(record);
        save();
        return record;
    }

    public List<MealRecord> getMealsForStudent(String studentId) {
        String normalized = requireStudent(studentId).getId();
        return data.mealRecords.stream().filter(record -> record.getStudentId().equals(normalized))
                .sorted(Comparator.comparing(MealRecord::getRecordedAt).reversed()).toList();
    }

    public List<MealRecord> getMealsForStudentSince(String studentId, LocalDateTime earliest) {
        String normalized = requireStudent(studentId).getId();
        return data.mealRecords.stream().filter(record -> record.getStudentId().equals(normalized)
                && !record.getRecordedAt().isBefore(earliest)).toList();
    }

    public List<MealRecord> getAllMealRecords() { return List.copyOf(data.mealRecords); }

    /**
     * Saves a camera-linked meal only once for each unique camera event ID.
     * Real camera software should create this ID automatically; it is typed manually in the demo.
     */
    public MealRecord recordCameraMeal(String detectionId, String studentId, String foodId,
                                       double gramsServed, double gramsLeftover, double confidence) {
        if (detectionId == null || detectionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Camera detection event ID is required.");
        }
        String normalizedId = detectionId.trim().toUpperCase();
        if (data.processedDetectionIds.contains(normalizedId)) {
            throw new IllegalStateException("This camera detection ID was already processed. Duplicate record blocked.");
        }
        MealRecord record = recordMeal(studentId, foodId, gramsServed, gramsLeftover,
                confidence, "SIMULATED CAMERA");
        data.processedDetectionIds.add(normalizedId);
        save();
        return record;
    }

    public WasteRecord recordWaste(String foodId, MealPeriod period, double prepared,
                                   double served, double plateLeftover) {
        requireFood(foodId);
        WasteRecord record = new WasteRecord(foodId, period, prepared, served, plateLeftover);
        data.wasteRecords.add(record);
        save();
        return record;
    }

    public List<WasteRecord> getWasteRecords() {
        return data.wasteRecords.stream().sorted(Comparator.comparing(WasteRecord::getRecordedAt).reversed()).toList();
    }

    public boolean awardPoints(String studentId, PointActivity activity, LocalDate date) {
        Student student = requireStudent(studentId);
        if (activity == null) throw new IllegalArgumentException("Choose an activity.");
        LocalDate weekStart = weekStart(date == null ? LocalDate.now() : date);
        boolean alreadyClaimed = data.pointTransactions.stream().anyMatch(transaction ->
                transaction.getStudentId().equals(student.getId())
                        && transaction.getActivity() == activity
                        && transaction.getWeekStart().equals(weekStart));
        if (alreadyClaimed) return false;
        data.pointTransactions.add(new PointTransaction(student.getId(), activity, weekStart));
        save();
        return true;
    }

    public int getWeeklyPoints(String studentId, LocalDate date) {
        Student student = requireStudent(studentId);
        LocalDate weekStart = weekStart(date == null ? LocalDate.now() : date);
        return data.pointTransactions.stream().filter(transaction ->
                transaction.getStudentId().equals(student.getId())
                        && transaction.getWeekStart().equals(weekStart))
                .mapToInt(PointTransaction::getPoints).sum();
    }

    public List<PointTransaction> getPointsForStudent(String studentId) {
        String normalized = requireStudent(studentId).getId();
        return data.pointTransactions.stream().filter(transaction -> transaction.getStudentId().equals(normalized))
                .sorted(Comparator.comparing(PointTransaction::getAwardedAt).reversed()).toList();
    }

    public boolean hasCheckInThisWeek(String studentId, LocalDate date) {
        String normalized = requireStudent(studentId).getId();
        LocalDate weekStart = weekStart(date == null ? LocalDate.now() : date);
        return data.checkIns.stream().anyMatch(checkIn -> checkIn.getStudentId().equals(normalized)
                && weekStart(checkIn.getCreatedAt().toLocalDate()).equals(weekStart));
    }

    public StudentCheckIn createCheckIn(String studentId, StudentFeeling feeling,
                                        String comment, boolean consentToShare) {
        Student student = requireStudent(studentId);
        if (hasCheckInThisWeek(student.getId(), LocalDate.now())) {
            throw new IllegalStateException("You have already completed this week's check-in. You can try again next week.");
        }
        StudentCheckIn checkIn = new StudentCheckIn(student.getId(), feeling, comment, consentToShare);
        data.checkIns.add(checkIn);
        // Completing the check-in earns points once per week; it never alerts staff by itself.
        awardPoints(student.getId(), PointActivity.NUTRITION_CHECK_IN, LocalDate.now());
        save();
        return checkIn;
    }

    public List<StudentCheckIn> getMyCheckIns(String studentId) {
        String normalized = requireStudent(studentId).getId();
        return data.checkIns.stream().filter(checkIn -> checkIn.getStudentId().equals(normalized))
                .sorted(Comparator.comparing(StudentCheckIn::getCreatedAt).reversed()).toList();
    }

    /** Only consented check-ins that still need a follow-up are returned to support staff. */
    public List<StudentCheckIn> getOpenSupportCheckIns() {
        return data.checkIns.stream().filter(StudentCheckIn::isVisibleToSupportStaff)
                .filter(checkIn -> !checkIn.isFollowUpDone())
                .sorted(Comparator.comparing(StudentCheckIn::getCreatedAt)).toList();
    }

    public void markSupportFollowUpDone(String checkInId) {
        StudentCheckIn checkIn = data.checkIns.stream()
                .filter(item -> item.getId().equalsIgnoreCase(checkInId == null ? "" : checkInId.trim()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Check-in was not found."));
        if (!checkIn.isVisibleToSupportStaff()) {
            throw new IllegalArgumentException("This check-in was not shared with support staff.");
        }
        checkIn.markFollowUpDone();
        save();
    }

    public FoodSurvey submitSurvey(String studentId, String foodId, int taste, int freshness,
                                   int portion, SurveyReason reason, String comment) {
        Student student = requireStudent(studentId);
        requireFood(foodId);
        LocalDate today = LocalDate.now();
        boolean duplicate = data.surveys.stream().anyMatch(survey ->
                survey.getStudentId().equals(student.getId())
                        && survey.getFoodId().equalsIgnoreCase(foodId)
                        && survey.getSubmittedAt().toLocalDate().equals(today));
        if (duplicate) throw new IllegalStateException("You have already rated this dish today.");
        FoodSurvey survey = new FoodSurvey(student.getId(), foodId, taste, freshness, portion, reason, comment);
        data.surveys.add(survey);
        // The weekly points guard prevents earning feedback points multiple times in one week.
        awardPoints(student.getId(), PointActivity.FOOD_FEEDBACK, LocalDate.now());
        save();
        return survey;
    }

    public List<FoodSurvey> getSurveys() { return List.copyOf(data.surveys); }

    public SickMealRequest createMealRequest(String studentId, String foodId, String otherFoodName,
                                             MealQuantity quantity, String hostelBlock,
                                             String room, String preferredTime, String foodNote) {
        Student student = requireStudent(studentId);
        boolean other = foodId == null || foodId.isBlank() || foodId.equalsIgnoreCase("OTHER");
        FoodItem food = null;
        if (!other) {
            food = requireFood(foodId);
            if (!food.isAvailable()) throw new IllegalArgumentException("That food is currently unavailable.");
        }
        boolean alreadyActive = data.mealRequests.stream().anyMatch(request ->
                request.getStudentId().equals(student.getId()) && request.isActive());
        if (alreadyActive) {
            throw new IllegalStateException("You already have an active meal request. Check its status before making another.");
        }
        String foodName = other ? requireText("Other food name", otherFoodName) : food.getName();
        String normalizedFoodId = other ? "OTHER" : food.getId();
        String requestId = String.format(Locale.ROOT, "REQ-%04d", data.mealRequests.size() + 1);
        SickMealRequest request = new SickMealRequest(requestId, student.getId(), normalizedFoodId,
                foodName, quantity, hostelBlock, room, preferredTime, foodNote);
        data.mealRequests.add(request);
        save();
        return request;
    }

    public List<SickMealRequest> getRequestsForStudent(String studentId) {
        String normalized = requireStudent(studentId).getId();
        return data.mealRequests.stream().filter(request -> request.getStudentId().equals(normalized))
                .sorted(Comparator.comparing(SickMealRequest::getRequestedAt).reversed()).toList();
    }

    public List<SickMealRequest> getAllMealRequests() {
        return data.mealRequests.stream().sorted(Comparator.comparing(SickMealRequest::getRequestedAt)).toList();
    }

    public void cancelOwnRequest(String studentId, String requestId) {
        String normalized = requireStudent(studentId).getId();
        SickMealRequest request = findRequest(requestId);
        if (!request.getStudentId().equals(normalized)) throw new IllegalArgumentException("You can cancel only your own request.");
        if (!request.canCancel()) throw new IllegalStateException("This request cannot be cancelled after preparation has begun.");
        request.updateStatus(MealRequestStatus.CANCELLED, "Cancelled by student.");
        save();
    }

    public void updateMealRequestStatus(String requestId, MealRequestStatus status, String note) {
        SickMealRequest request = findRequest(requestId);
        request.updateStatus(status, note);
        save();
    }

    private SickMealRequest findRequest(String requestId) {
        return data.mealRequests.stream().filter(request -> request.getRequestId().equalsIgnoreCase(
                        requestId == null ? "" : requestId.trim()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Meal request was not found."));
    }

    public List<SickMealRequest> getActiveMealRequests() {
        return data.mealRequests.stream().filter(SickMealRequest::isActive)
                .sorted(Comparator.comparing(SickMealRequest::getRequestedAt)).toList();
    }

    public boolean shouldShowLowPointsReminder(String studentId, LocalDate date) {
        return getWeeklyPoints(studentId, date) < LOW_POINTS_THRESHOLD;
    }

    public static LocalDate weekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private Student requireStudent(String id) {
        Student student = getStudent(id);
        if (student == null) throw new IllegalArgumentException("Student profile not found. Select or register a demo profile first.");
        return student;
    }

    private FoodItem requireFood(String id) {
        FoodItem food = getFood(id);
        if (food == null) throw new IllegalArgumentException("Food item was not found.");
        return food;
    }

    private static String requireText(String field, String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " cannot be empty.");
        return value.trim();
    }
}
