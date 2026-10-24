package com.smartcampus.rescue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Saves the console prototype to a local file so demo records survive restarts.
 * This file is NOT encrypted. Use fictional data only until secure storage exists.
 */
public final class CampusStore {
    private final Path filePath;

    public CampusStore(Path filePath) {
        this.filePath = filePath;
    }

    public CampusData load() {
        if (!Files.exists(filePath)) {
            return new CampusData();
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object value = input.readObject();
            if (value instanceof CampusData campusData) {
                return campusData;
            }
            System.err.println("Saved data file had an unexpected format. Starting with empty demo data.");
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Could not read saved demo data: " + exception.getMessage());
            // Keep the unreadable file as a backup rather than silently overwriting it.
            try {
                Path backup = filePath.resolveSibling(filePath.getFileName() + ".unreadable-backup");
                if (!Files.exists(backup)) Files.copy(filePath, backup);
                System.err.println("A backup copy was kept at: " + backup);
            } catch (IOException backupException) {
                System.err.println("Could not create a backup copy: " + backupException.getMessage());
            }
            System.err.println("The application will start with empty data; check the backup before resetting anything.");
        }
        return new CampusData();
    }

    /** Write to a temporary file first so an interrupted save is less likely to corrupt the last copy. */
    public void save(CampusData campusData) {
        Path parent = filePath.toAbsolutePath().getParent();
        Path temporary = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            if (parent != null) Files.createDirectories(parent);
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporary))) {
                output.writeObject(campusData);
            }
            try {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            System.err.println("WARNING: Could not save the latest data: " + exception.getMessage());
        }
    }
}
