package com.smartcampus.rescue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** All local prototype data. A later version can replace this with a database. */
public final class CampusData implements Serializable {
    private static final long serialVersionUID = 1L;

    List<FoodItem> foods = new ArrayList<>();
    List<Student> students = new ArrayList<>();
    List<MealRecord> mealRecords = new ArrayList<>();
    List<WasteRecord> wasteRecords = new ArrayList<>();
    List<PointTransaction> pointTransactions = new ArrayList<>();
    List<StudentCheckIn> checkIns = new ArrayList<>();
    List<FoodSurvey> surveys = new ArrayList<>();
    List<SickMealRequest> mealRequests = new ArrayList<>();
    // Unique IDs already processed by the simulated camera to prevent duplicate records.
    List<String> processedDetectionIds = new ArrayList<>();

    /** Keep newly added lists usable when loading an older saved prototype file. */
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        if (foods == null) foods = new ArrayList<>();
        if (students == null) students = new ArrayList<>();
        if (mealRecords == null) mealRecords = new ArrayList<>();
        if (wasteRecords == null) wasteRecords = new ArrayList<>();
        if (pointTransactions == null) pointTransactions = new ArrayList<>();
        if (checkIns == null) checkIns = new ArrayList<>();
        if (surveys == null) surveys = new ArrayList<>();
        if (mealRequests == null) mealRequests = new ArrayList<>();
        if (processedDetectionIds == null) processedDetectionIds = new ArrayList<>();
    }
}
