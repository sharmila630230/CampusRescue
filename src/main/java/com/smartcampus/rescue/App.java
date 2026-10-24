package com.smartcampus.rescue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Main menu and console screens for the Smart Campus Food Rescue project. */
public final class App {
    private final Scanner scanner = new Scanner(System.in);
    private final CampusService service;

    private App(CampusService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        // Data is saved locally in the project folder. This demo file is not encrypted.
        CampusStore store = new CampusStore(Path.of("data", "campus-data.ser"));
        CampusService service = new CampusService(store.load(), store::save);
        service.seedDemoDataWhenNeeded();
        new App(service).run();
    }

    private void run() {
        System.out.println("=======================================================");
        System.out.println(" SMART CAMPUS FOOD RESCUE & NUTRITION SYSTEM");
        System.out.println("=======================================================");
        System.out.println("Console prototype only: food values and detection are examples.");
        System.out.println("Use fictional student details. This menu is NOT secure login.");

        boolean running = true;
        while (running) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Student portal");
            System.out.println("2. Canteen staff portal");
            System.out.println("3. Student-welfare support portal");
            System.out.println("4. Exit");
            int choice = readInt("Choose an option: ", 1, 4);
            switch (choice) {
                case 1 -> studentPortal();
                case 2 -> canteenPortal();
                case 3 -> supportPortal();
                case 4 -> running = false;
                default -> System.out.println("Please select a valid option.");
            }
        }
        System.out.println("\nThank you. Your local demo records have been saved when changes were made.");
        System.out.println("Remember: data/campus-data.ser is local demo data. Do not commit it to GitHub.");
    }

    // ---------------------------------------------------------------------
    // Student portal
    // ---------------------------------------------------------------------

    private void studentPortal() {
        Student student = chooseOrRegisterStudent();
        if (student == null) return;
        showWeeklyPointsAndGentleReminder(student);

        boolean inside = true;
        while (inside) {
            System.out.println("\nSTUDENT PORTAL - " + student.getDisplayName() + " (" + student.getId() + ")");
            System.out.println("1. View food menu and nutrition estimates");
            System.out.println("2. Record a meal / view estimated nutrients");
            System.out.println("3. View my 14-day nutrition summary");
            System.out.println("4. View my engagement points");
            System.out.println("5. Claim an optional completed activity");
            System.out.println("6. Complete my private weekly check-in");
            System.out.println("7. Rate a cafeteria dish");
            System.out.println("8. Request food delivered to my hostel room");
            System.out.println("9. View or cancel my meal requests");
            System.out.println("10. Return to main menu");
            int choice = readInt("Choose an option: ", 1, 10);
            try {
                switch (choice) {
                    case 1 -> showFoodMenu(false);
                    case 2 -> recordStudentMeal(student);
                    case 3 -> showNutritionSummary(student);
                    case 4 -> showPoints(student);
                    case 5 -> claimOptionalActivity(student);
                    case 6 -> completeCheckIn(student);
                    case 7 -> submitFoodSurvey(student);
                    case 8 -> requestSickMeal(student);
                    case 9 -> viewOrCancelRequests(student);
                    case 10 -> inside = false;
                    default -> System.out.println("Please choose a valid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Could not complete that action: " + exception.getMessage());
            }
        }
    }

    private Student chooseOrRegisterStudent() {
        List<Student> students = service.getStudents();
        System.out.println("\nDEMO STUDENT PROFILES");
        for (Student student : students) System.out.println("  " + student);
        System.out.println("You may enter one of these IDs or register a fictional demo profile.");
        String id = readLine("Student ID (or NEW to register): ").trim();
        if (id.equalsIgnoreCase("NEW")) {
            String newId = readLine("Create a demo student ID (example STU004): ").trim();
            String name = readLine("Display name (use a fictional name): ").trim();
            try {
                Student student = service.registerStudent(newId, name);
                System.out.println("Demo profile created: " + student);
                return student;
            } catch (IllegalArgumentException exception) {
                System.out.println("Could not create profile: " + exception.getMessage());
                return null;
            }
        }
        Student student = service.getStudent(id);
        if (student == null) {
            System.out.println("That demo profile was not found. Choose NEW to register one.");
            return null;
        }
        return student;
    }

    private void showWeeklyPointsAndGentleReminder(Student student) {
        int points = service.getWeeklyPoints(student.getId(), LocalDate.now());
        System.out.println("\nThis week's engagement points: " + points + " / example goal " + CampusService.WEEKLY_POINTS_GOAL);
        if (points < CampusService.LOW_POINTS_THRESHOLD) {
            System.out.println("Gentle reminder: your points are below the example range this week.");
            System.out.println("Points do not measure your health. No staff alert is sent from points alone.");
            if (!service.hasCheckInThisWeek(student.getId(), LocalDate.now())) {
                System.out.println("You can choose the private weekly check-in if you would like support.");
            }
        }
    }

    private void showFoodMenu(boolean includeUnavailable) {
        List<FoodItem> foods = includeUnavailable ? service.getFoods() : service.getAvailableFoods();
        if (foods.isEmpty()) {
            System.out.println("No food items are currently available.");
            return;
        }
        System.out.println("\nFOOD CATALOGUE (nutrition values are illustrative per 100 g)");
        System.out.printf("%-5s %-27s %-12s %-10s %-10s %-10s %-10s%n",
                "ID", "Food", "Category", "Kcal", "Protein", "Carbs", "Status");
        for (FoodItem food : foods) {
            NutritionFacts facts = food.getNutritionPer100g();
            System.out.printf("%-5s %-27s %-12s %-10.1f %-10.1f %-10.1f %-10s%n",
                    food.getId(), shorten(food.getName(), 26), shorten(food.getCategory(), 11),
                    facts.getCaloriesKcal(), facts.getProteinGrams(), facts.getCarbohydratesGrams(),
                    food.isAvailable() ? "Available" : "Unavailable");
        }
        System.out.println("Nutrients per 100 g: calories, protein, carbohydrates, fats, fibre, calcium, and iron.");
        System.out.println("These sample values are not verified recipe data or medical advice.");
        for (FoodItem food : foods) {
            NutritionFacts n = food.getNutritionPer100g();
            System.out.printf("  %s - Fat %.1fg | Fibre %.1fg | Calcium %.1fmg | Iron %.2fmg%n",
                    food.getName(), n.getFatGrams(), n.getFibreGrams(), n.getCalciumMg(), n.getIronMg());
        }
    }

    private void recordStudentMeal(Student student) {
        FoodItem food = chooseFood(true, false);
        if (food == null) return;
        double served = readDouble("Portion served in grams (example 200): ", 1, 2000);
        double leftover = readDouble("Leftover grams (0 if none; must not exceed served amount): ", 0, served);
        MealRecord record = service.recordMeal(student.getId(), food.getId(), served, leftover, 1.0, "MANUAL STUDENT ENTRY");
        NutritionFacts estimate = record.estimateConsumedNutrition(food);
        System.out.println("Meal record saved. Estimated amount eaten: " + record.getConsumedGramsEstimate() + " g");
        System.out.println("Estimated nutrients: " + estimate);
        System.out.println("This estimate depends on correct food identification and portion/leftover weights.");
    }

    private void showNutritionSummary(Student student) {
        LocalDateTime from = LocalDateTime.now().minusDays(14);
        List<MealRecord> meals = service.getMealsForStudentSince(student.getId(), from);
        System.out.println("\nYOUR LAST 14 DAYS - ESTIMATED NUTRITION");
        if (meals.isEmpty()) {
            System.out.println("No meal records yet. Add a meal record to start your summary.");
            return;
        }
        NutritionFacts total = NutritionFacts.zero();
        double grams = 0;
        for (MealRecord meal : meals) {
            FoodItem food = service.getFood(meal.getFoodId());
            if (food != null) {
                total = total.plus(meal.estimateConsumedNutrition(food));
                grams += meal.getConsumedGramsEstimate();
            }
        }
        System.out.println("Meal records: " + meals.size());
        System.out.printf("Estimated food eaten: %.1f g%n", grams);
        System.out.println("Estimated total nutrients across recorded meals: " + total);
        System.out.println("This is only the total for recorded meals, not your full daily intake or a medical assessment.");
    }

    private void showPoints(Student student) {
        int total = service.getWeeklyPoints(student.getId(), LocalDate.now());
        System.out.println("\nYOUR POINTS THIS WEEK: " + total + " (example goal: " + CampusService.WEEKLY_POINTS_GOAL + ")");
        System.out.println("Example activities and values:");
        for (PointActivity activity : PointActivity.values()) {
            System.out.println("  " + activity.getLabel() + " = " + activity.getPoints() + " points");
        }
        System.out.println("Your activity history:");
        List<PointTransaction> history = service.getPointsForStudent(student.getId());
        if (history.isEmpty()) System.out.println("  No points recorded yet.");
        for (PointTransaction transaction : history) {
            System.out.printf("  %s | +%d | week starting %s%n", transaction.getActivity().getLabel(),
                    transaction.getPoints(), transaction.getWeekStart());
        }
        System.out.println("Points encourage optional engagement; they do not measure health, grades, or personal value.");
    }

    private void claimOptionalActivity(Student student) {
        System.out.println("\nOPTIONAL ACTIVITIES (claim only after completing them)");
        System.out.println("1. Nutrition/wellness learning activity (+10)");
        System.out.println("2. Voluntary wellness session (+15)");
        System.out.println("3. Food-waste awareness activity (+10)");
        System.out.println("4. Back");
        int choice = readInt("Choose completed activity: ", 1, 4);
        if (choice == 4) return;
        PointActivity activity = switch (choice) {
            case 1 -> PointActivity.LEARNING_ACTIVITY;
            case 2 -> PointActivity.WELLNESS_SESSION;
            case 3 -> PointActivity.WASTE_AWARENESS;
            default -> throw new IllegalStateException("Unexpected menu option.");
        };
        boolean awarded = service.awardPoints(student.getId(), activity, LocalDate.now());
        System.out.println(awarded
                ? "Points added: +" + activity.getPoints()
                : "This activity already earned points for you this week. No duplicate points were added.");
    }

    private void completeCheckIn(Student student) {
        if (service.hasCheckInThisWeek(student.getId(), LocalDate.now())) {
            System.out.println("You have already completed this week's check-in.");
            printMyCheckIns(student);
            return;
        }
        System.out.println("\nPRIVATE WEEKLY CHECK-IN");
        System.out.println("This is optional. You do not need to disclose a diagnosis.");
        StudentFeeling[] feelings = StudentFeeling.values();
        for (int i = 0; i < feelings.length; i++) System.out.println((i + 1) + ". " + feelings[i].getLabel());
        StudentFeeling feeling = feelings[readInt("How have things been? ", 1, feelings.length) - 1];
        String comment = readLine("Optional note (press Enter to skip, 300 characters max): ");
        boolean consent = false;
        if (feeling != StudentFeeling.PREFER_NOT_TO_SAY) {
            consent = readYesNo("Share this answer with the authorized student-welfare team for guidance? (y/n): ");
        }
        StudentCheckIn checkIn = service.createCheckIn(student.getId(), feeling, comment, consent);
        System.out.println("Your check-in was saved privately. You earned +10 points.");
        if (checkIn.isVisibleToSupportStaff()) {
            System.out.println("A support item is now available to the authorized student-welfare team in this prototype.");
        } else {
            System.out.println("Your answer will not appear in the support team's queue.");
        }
        System.out.println("This prototype displays notices in the console; it does not send real texts or push notifications.");
    }

    private void printMyCheckIns(Student student) {
        List<StudentCheckIn> checkIns = service.getMyCheckIns(student.getId());
        if (checkIns.isEmpty()) return;
        System.out.println("Your private check-in history:");
        for (StudentCheckIn checkIn : checkIns) {
            System.out.printf("  %s | %s | shared with support: %s%n", checkIn.getCreatedAt(),
                    checkIn.getFeeling().getLabel(), checkIn.isVisibleToSupportStaff() ? "yes" : "no");
        }
    }

    private void submitFoodSurvey(Student student) {
        FoodItem food = chooseFood(false, false);
        if (food == null) return;
        System.out.println("Rate each item from 1 (poor) to 5 (excellent).");
        int taste = readInt("Taste (1-5): ", 1, 5);
        int freshness = readInt("Freshness/temperature (1-5): ", 1, 5);
        int portion = readInt("Portion suitability (1-5): ", 1, 5);
        SurveyReason[] reasons = SurveyReason.values();
        for (int i = 0; i < reasons.length; i++) System.out.println((i + 1) + ". " + reasons[i].getLabel());
        SurveyReason reason = reasons[readInt("Optional reason: ", 1, reasons.length) - 1];
        String comment = readLine("Optional comment for improvement (300 characters max): ");
        FoodSurvey survey = service.submitSurvey(student.getId(), food.getId(), taste, freshness, portion, reason, comment);
        System.out.println("Thank you. Feedback saved; it contributes to aggregate dish-level reporting.");
        System.out.println("Your survey record ID: " + survey.getId());
        System.out.println("You earn feedback points at most once per week, even if you rate more than one dish.");
    }

    private void requestSickMeal(Student student) {
        System.out.println("\nSICK-STUDENT MEAL REQUEST");
        System.out.println("You do not need to enter a diagnosis. Food delivery is not an emergency service.");
        FoodItem food = chooseFood(true, true);
        if (food == null && lastFoodChoiceCancelled) return;
        String foodId = food == null ? "OTHER" : food.getId();
        String otherName = food == null ? readNonBlank("Name of other available food: ") : "";
        MealQuantity[] quantities = MealQuantity.values();
        for (int i = 0; i < quantities.length; i++) System.out.println((i + 1) + ". " + quantities[i].getLabel());
        MealQuantity quantity = quantities[readInt("Choose quantity: ", 1, quantities.length) - 1];
        String hostel = readNonBlank("Hostel name/block: ");
        String room = readNonBlank("Room number: ");
        System.out.println("1. As soon as possible");
        System.out.println("2. Next available meal time");
        String preferred = readInt("Preferred time: ", 1, 2) == 1
                ? "As soon as possible" : "Next available meal time";
        String note = readLine("Optional food preference/allergy note (300 characters max): ");
        SickMealRequest request = service.createMealRequest(student.getId(), foodId, otherName,
                quantity, hostel, room, preferred, note);
        System.out.println("Request submitted: " + request.getRequestId() + " | Status: Pending");
        if (!note.isBlank()) {
            System.out.println("Important: canteen staff must verify ingredients and preparation. This form cannot guarantee a food is allergy-safe.");
        }
        System.out.println("The canteen must accept the request before preparation. Check the status in the student portal.");
        System.out.println("For urgent medical help, contact the hostel warden/campus medical service directly.");
    }

    // This flag allows the user to cancel the food picker instead of treating it as "Other" food.
    private boolean lastFoodChoiceCancelled;

    /** Choose an item. If otherOption is true, the last choice means Other food. */
    private FoodItem chooseFood(boolean availableOnly, boolean otherOption) {
        lastFoodChoiceCancelled = false;
        List<FoodItem> foods = availableOnly ? service.getAvailableFoods() : service.getFoods();
        if (foods.isEmpty()) {
            System.out.println("No food items are in the catalogue.");
            lastFoodChoiceCancelled = true;
            return null;
        }
        System.out.println("\nChoose food:");
        for (int i = 0; i < foods.size(); i++) {
            FoodItem food = foods.get(i);
            System.out.println((i + 1) + ". " + food.getName() + " (" + food.getId() + ")");
        }
        int otherChoice = foods.size() + 1;
        if (otherOption) System.out.println(otherChoice + ". Other available food (enter name)");
        System.out.println((otherOption ? otherChoice + 1 : foods.size() + 1) + ". Cancel");
        int cancelChoice = otherOption ? otherChoice + 1 : foods.size() + 1;
        int choice = readInt("Choice: ", 1, cancelChoice);
        if (choice == cancelChoice) {
            lastFoodChoiceCancelled = true;
            return null;
        }
        if (otherOption && choice == otherChoice) return null;
        return foods.get(choice - 1);
    }

    private void viewOrCancelRequests(Student student) {
        List<SickMealRequest> requests = service.getRequestsForStudent(student.getId());
        if (requests.isEmpty()) {
            System.out.println("You do not have any meal requests yet.");
            return;
        }
        printStudentRequests(requests);
        System.out.println("Enter a request number from the list to cancel it, or 0 to return.");
        int choice = readInt("Choice: ", 0, requests.size());
        if (choice == 0) return;
        SickMealRequest request = requests.get(choice - 1);
        try {
            service.cancelOwnRequest(student.getId(), request.getRequestId());
            System.out.println("Request " + request.getRequestId() + " cancelled.");
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void printStudentRequests(List<SickMealRequest> requests) {
        System.out.println("\nYOUR MEAL REQUESTS");
        for (int i = 0; i < requests.size(); i++) {
            SickMealRequest request = requests.get(i);
            System.out.printf("%d. %s | %s | %s | Hostel %s, Room %s | %s%n",
                    i + 1, request.getRequestId(), request.getFoodName(), request.getQuantity().getLabel(),
                    request.getHostelBlock(), request.getRoomNumber(), request.getStatus().getLabel());
            if (!request.getStatusNote().isBlank()) System.out.println("   Update: " + request.getStatusNote());
        }
    }

    // ---------------------------------------------------------------------
    // Canteen portal
    // ---------------------------------------------------------------------

    private void canteenPortal() {
        boolean inside = true;
        while (inside) {
            System.out.println("\nCANTEEN STAFF PORTAL");
            System.out.println("1. View food catalogue");
            System.out.println("2. Add food and nutrition values");
            System.out.println("3. Mark a food available/unavailable");
            System.out.println("4. Record prepared food and leftovers");
            System.out.println("5. View aggregate food-waste report");
            System.out.println("6. View aggregate cafeteria survey report");
            System.out.println("7. View active hostel meal requests");
            System.out.println("8. Update a meal request status");
            System.out.println("9. Simulate serving-counter camera detection");
            System.out.println("10. Return to main menu");
            int choice = readInt("Choose an option: ", 1, 10);
            try {
                switch (choice) {
                    case 1 -> showFoodMenu(true);
                    case 2 -> addFoodItem();
                    case 3 -> toggleFoodAvailability();
                    case 4 -> recordKitchenWaste();
                    case 5 -> showWasteReport();
                    case 6 -> showFoodQualityReport();
                    case 7 -> showActiveMealRequests();
                    case 8 -> updateMealRequest();
                    case 9 -> simulateCameraDetection();
                    case 10 -> inside = false;
                    default -> System.out.println("Please select a valid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Could not complete that action: " + exception.getMessage());
            }
        }
    }

    private void addFoodItem() {
        System.out.println("Enter nutrition values per 100 g. Use verified recipe data in a real deployment.");
        String name = readNonBlank("Food name: ");
        String category = readNonBlank("Category (breakfast/meal/side dish/etc.): ");
        NutritionFacts nutrition = new NutritionFacts(
                readDouble("Calories (kcal): ", 0, 2000),
                readDouble("Protein (g): ", 0, 1000),
                readDouble("Carbohydrates (g): ", 0, 1000),
                readDouble("Fat (g): ", 0, 1000),
                readDouble("Fibre (g): ", 0, 1000),
                readDouble("Calcium (mg): ", 0, 10000),
                readDouble("Iron (mg): ", 0, 1000));
        FoodItem food = service.addFood(name, category, nutrition);
        System.out.println("Food added: " + food.getId() + " - " + food.getName());
    }

    private void toggleFoodAvailability() {
        FoodItem food = chooseFood(false, false);
        if (food == null) return;
        boolean available = readYesNo("Should " + food.getName() + " be available? (y/n): ");
        service.setFoodAvailable(food.getId(), available);
        System.out.println("Availability updated.");
    }

    private void recordKitchenWaste() {
        FoodItem food = chooseFood(false, false);
        if (food == null) return;
        MealPeriod[] periods = MealPeriod.values();
        for (int i = 0; i < periods.length; i++) System.out.println((i + 1) + ". " + periods[i].getLabel());
        MealPeriod period = periods[readInt("Meal period: ", 1, periods.length) - 1];
        double prepared = readDouble("Amount prepared in grams: ", 1, 1_000_000);
        double served = readDouble("Amount served in grams (not above prepared): ", 0, prepared);
        double plateLeftovers = readDouble("Returned plate leftovers in grams (not above served): ", 0, served);
        WasteRecord record = service.recordWaste(food.getId(), period, prepared, served, plateLeftovers);
        System.out.printf("Waste record saved. Estimated total waste: %.1f g (unserved + plate leftovers).%n",
                record.getTotalWasteGrams());
    }

    private void showWasteReport() {
        List<WasteRecord> records = service.getWasteRecords();
        if (records.isEmpty()) {
            System.out.println("No kitchen waste records yet.");
            return;
        }
        // Key is dish plus meal period; values: prepared grams, served grams, wasted grams, number of records.
        Map<String, double[]> totals = new LinkedHashMap<>();
        for (WasteRecord record : records) {
            FoodItem food = service.getFood(record.getFoodId());
            String key = (food == null ? record.getFoodId() : food.getName()) + " | " + record.getMealPeriod().getLabel();
            double[] values = totals.computeIfAbsent(key, ignored -> new double[4]);
            values[0] += record.getGramsPrepared();
            values[1] += record.getGramsServed();
            values[2] += record.getTotalWasteGrams();
            values[3] += 1;
        }
        System.out.println("\nAGGREGATE FOOD-WASTE REPORT");
        System.out.printf("%-42s %12s %12s %12s %10s%n", "Dish / meal", "Prepared g", "Served g", "Waste g", "Waste %");
        for (Map.Entry<String, double[]> entry : totals.entrySet()) {
            double[] v = entry.getValue();
            double rate = v[0] == 0 ? 0 : v[2] / v[0] * 100.0;
            System.out.printf("%-42s %12.1f %12.1f %12.1f %9.1f%%%n",
                    shorten(entry.getKey(), 41), v[0], v[1], v[2], rate);
        }
        System.out.println("Use these trends for respectful planning, not blame. Validate kitchen measurements before acting on them.");
    }

    private void showFoodQualityReport() {
        List<FoodSurvey> surveys = service.getSurveys();
        if (surveys.isEmpty()) {
            System.out.println("No cafeteria surveys have been submitted yet.");
            return;
        }
        Map<String, List<FoodSurvey>> byFood = new LinkedHashMap<>();
        for (FoodSurvey survey : surveys) byFood.computeIfAbsent(survey.getFoodId(), ignored -> new ArrayList<>()).add(survey);
        System.out.println("\nAGGREGATE FOOD-QUALITY REPORT");
        System.out.printf("%-27s %7s %10s %12s %12s %11s%n", "Dish", "Surveys", "Taste avg", "Freshness", "Portion", "Low ratings");
        for (Map.Entry<String, List<FoodSurvey>> entry : byFood.entrySet()) {
            List<FoodSurvey> group = entry.getValue();
            FoodItem food = service.getFood(entry.getKey());
            double taste = group.stream().mapToInt(FoodSurvey::getTasteRating).average().orElse(0);
            double fresh = group.stream().mapToInt(FoodSurvey::getFreshnessRating).average().orElse(0);
            double portion = group.stream().mapToInt(FoodSurvey::getPortionRating).average().orElse(0);
            long low = group.stream().filter(FoodSurvey::isLowRating).count();
            System.out.printf("%-27s %7d %10.2f %12.2f %12.2f %11d%n",
                    shorten(food == null ? entry.getKey() : food.getName(), 26), group.size(), taste, fresh, portion, low);
            Map<SurveyReason, Long> reasons = new EnumMap<>(SurveyReason.class);
            for (FoodSurvey survey : group) reasons.merge(survey.getReason(), 1L, Long::sum);
            System.out.print("   Feedback themes: ");
            if (reasons.isEmpty()) System.out.println("none");
            else System.out.println(reasons.entrySet().stream()
                    .map(reason -> reason.getKey().getLabel() + "=" + reason.getValue())
                    .reduce((a, b) -> a + ", " + b).orElse("none"));
        }
        System.out.println("Only aggregated ratings and reason counts are shown here; student IDs and individual comments are hidden.");
        System.out.println("Low ratings are a prompt to review the dish, recipe, temperature, and portion—not to blame staff.");
    }

    private void showActiveMealRequests() {
        List<SickMealRequest> requests = service.getActiveMealRequests();
        if (requests.isEmpty()) {
            System.out.println("There are no active hostel meal requests.");
            return;
        }
        System.out.println("\nACTIVE HOSTEL MEAL REQUESTS");
        for (SickMealRequest request : requests) {
            System.out.printf("%s | Student %s | %s (%s) | %s | Hostel %s, Room %s | preferred: %s%n",
                    request.getRequestId(), request.getStudentId(), request.getFoodName(),
                    request.getQuantity().getLabel(), request.getStatus().getLabel(),
                    request.getHostelBlock(), request.getRoomNumber(), request.getPreferredTime());
            if (!request.getPrivateFoodNote().isBlank()) {
                System.out.println("   PRIVATE PREPARATION NOTE: " + request.getPrivateFoodNote());
                System.out.println("   Verify ingredients and preparation; do not assume allergy safety from this note alone.");
            }
        }
        System.out.println("Room details and food notes should only be used by staff involved in preparing/delivering the order.");
    }

    private void updateMealRequest() {
        List<SickMealRequest> active = service.getActiveMealRequests();
        if (active.isEmpty()) {
            System.out.println("There are no active requests to update.");
            return;
        }
        for (int i = 0; i < active.size(); i++) {
            SickMealRequest r = active.get(i);
            System.out.printf("%d. %s | %s | %s%n", i + 1, r.getRequestId(), r.getFoodName(), r.getStatus().getLabel());
        }
        SickMealRequest request = active.get(readInt("Choose request: ", 1, active.size()) - 1);
        System.out.println("Current status: " + request.getStatus().getLabel());
        List<MealRequestStatus> nextStates = switch (request.getStatus()) {
            case PENDING -> List.of(MealRequestStatus.ACCEPTED, MealRequestStatus.UNAVAILABLE);
            case ACCEPTED -> List.of(MealRequestStatus.PREPARING, MealRequestStatus.UNAVAILABLE);
            case PREPARING -> List.of(MealRequestStatus.OUT_FOR_DELIVERY);
            case OUT_FOR_DELIVERY -> List.of(MealRequestStatus.DELIVERED);
            case DELIVERED, CANCELLED, UNAVAILABLE -> List.of();
        };
        if (nextStates.isEmpty()) {
            System.out.println("This request is already closed.");
            return;
        }
        for (int i = 0; i < nextStates.size(); i++) System.out.println((i + 1) + ". " + nextStates.get(i).getLabel());
        MealRequestStatus status = nextStates.get(readInt("Choose next status: ", 1, nextStates.size()) - 1);
        String note = readLine("Optional status note for the student: ");
        service.updateMealRequestStatus(request.getRequestId(), status, note);
        System.out.println("Request status updated to " + status.getLabel() + ".");
    }

    private void simulateCameraDetection() {
        System.out.println("\nSIMULATED CAMERA DETECTION - no real camera or AI is connected");
        List<FoodItem> foods = service.getAvailableFoods();
        if (foods.isEmpty()) {
            System.out.println("No food catalogue items are available to detect.");
            return;
        }
        for (int i = 0; i < foods.size(); i++) {
            System.out.println((i + 1) + ". " + foods.get(i).getName());
        }
        int unknownChoice = foods.size() + 1;
        System.out.println(unknownChoice + ". Unknown / mixed dish / not recognized");
        System.out.println((unknownChoice + 1) + ". Cancel");
        int detectionChoice = readInt("What did the simulated camera detect? ", 1, unknownChoice + 1);
        if (detectionChoice == unknownChoice + 1) return;
        FoodItem detected;
        if (detectionChoice == unknownChoice) {
            System.out.println("Unknown food cannot be assigned reliable nutrition values automatically.");
            if (!readYesNo("Can a person correct it to a known catalogue item? (y/n): ")) {
                System.out.println("Detection was not recorded; no nutrition/meal record was created.");
                return;
            }
            detected = chooseFood(true, false);
            if (detected == null) return;
        } else {
            detected = foods.get(detectionChoice - 1);
        }
        double confidence = readDouble("Simulated detection confidence (0.00-1.00): ", 0, 1);
        if (confidence < 0.70) {
            System.out.println("Low confidence: the system should ask for correction rather than trust the detection.");
            if (!readYesNo("Manually correct the detected food? (y/n): ")) {
                System.out.println("Detection ignored. No meal record saved.");
                return;
            }
            FoodItem corrected = chooseFood(true, false);
            if (corrected == null) return;
            detected = corrected;
            System.out.println("Food corrected to: " + detected.getName());
        }
        System.out.println("A camera alone cannot reliably measure grams. Enter a measured or standard portion estimate.");
        double grams = readDouble("Estimated portion served (g): ", 1, 2000);
        double leftover = readDouble("Known leftover grams (0 if none; must not exceed portion): ", 0, grams);
        NutritionFacts estimate = detected.estimateForGrams(grams - leftover);
        System.out.println("Estimated eaten portion: " + (grams - leftover) + " g; estimated nutrients: " + estimate);
        if (readYesNo("Link this simulated record to a demo student? (y/n): ")) {
            String id = readNonBlank("Student ID: ");
            String detectionId = readNonBlank("Unique camera event ID (example CAM-0001; reusing it is blocked): ");
            MealRecord record = service.recordCameraMeal(detectionId, id, detected.getId(), grams, leftover, confidence);
            System.out.println("Linked record saved: " + record.getId());
        } else {
            System.out.println("Estimate displayed only; no personal meal record was saved.");
        }
        System.out.println("Real deployment needs measured portions, reliable meal matching, and consent/privacy review.");
    }

    // ---------------------------------------------------------------------
    // Student-welfare portal
    // ---------------------------------------------------------------------

    private void supportPortal() {
        System.out.println("\nSTUDENT-WELFARE SUPPORT PORTAL");
        System.out.println("Only student check-ins explicitly shared with support staff appear here.");
        System.out.println("Low points or missing scans alone do not generate an alert.");
        boolean inside = true;
        while (inside) {
            System.out.println("1. View open consented check-ins");
            System.out.println("2. Mark a follow-up complete");
            System.out.println("3. Return to main menu");
            int choice = readInt("Choose an option: ", 1, 3);
            try {
                switch (choice) {
                    case 1 -> showOpenSupportCheckIns();
                    case 2 -> completeSupportFollowUp();
                    case 3 -> inside = false;
                    default -> System.out.println("Please choose a valid option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Could not complete that action: " + exception.getMessage());
            }
        }
    }

    private void showOpenSupportCheckIns() {
        List<StudentCheckIn> requests = service.getOpenSupportCheckIns();
        if (requests.isEmpty()) {
            System.out.println("No open consented check-ins. The system does not treat low points alone as a welfare alert.");
            return;
        }
        System.out.println("\nOPEN SUPPORT CHECK-INS");
        for (StudentCheckIn checkIn : requests) {
            System.out.printf("ID %s | Student %s | %s | %s%n", checkIn.getId(), checkIn.getStudentId(),
                    checkIn.getFeeling().getLabel(), checkIn.getCreatedAt());
            if (!checkIn.getOptionalComment().isBlank()) System.out.println("   Student note: " + checkIn.getOptionalComment());
            System.out.println("   Please check in respectfully; this is not a diagnosis.");
        }
    }

    private void completeSupportFollowUp() {
        List<StudentCheckIn> requests = service.getOpenSupportCheckIns();
        if (requests.isEmpty()) {
            System.out.println("There are no open consented check-ins.");
            return;
        }
        for (StudentCheckIn checkIn : requests) {
            System.out.printf("%s | Student %s | %s%n", checkIn.getId(), checkIn.getStudentId(), checkIn.getFeeling().getLabel());
        }
        String id = readNonBlank("Enter check-in ID after making contact: ");
        service.markSupportFollowUpDone(id);
        System.out.println("Follow-up marked complete. The system will not keep showing it in the open queue.");
    }

    // ---------------------------------------------------------------------
    // Input helpers: keep malformed input from crashing the program.
    // ---------------------------------------------------------------------

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Show a helpful prompt below rather than ending the program.
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (Double.isFinite(value) && value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Show a helpful prompt below rather than ending the program.
            }
            System.out.println("Enter a valid number from " + min + " to " + max + ".");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isBlank()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String answer = readLine(prompt).toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) return true;
            if (answer.equals("n") || answer.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }

    private String shorten(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) return value;
        return value.substring(0, Math.max(0, maxLength - 1)) + "…";
    }
}
