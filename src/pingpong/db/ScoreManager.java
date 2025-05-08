package pingpong.db;

import java.io.*;
import java.util.ArrayList;

public class ScoreManager {
    private static final String FILE_PATH = "E:\\new pong\\scores.txt";

    public ScoreManager() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("ScoreManager: Created new file at " + FILE_PATH);
            }
        } catch (IOException e) {
            System.err.println("ScoreManager: Failed to initialize scores file: " + e.getMessage());
        }
    }

    public void saveHighScore(String username, int score) {
        try {
            // Read existing scores
            ArrayList<String> users = new ArrayList<>();
            ArrayList<Integer> scores = new ArrayList<>();
            File file = new File(FILE_PATH);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    int lineNumber = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNumber++;
                        if (line.trim().isEmpty()) {
                            System.out.println("ScoreManager: Skipping empty line at " + lineNumber);
                            continue;
                        }
                        System.out.println("ScoreManager: Read line " + lineNumber + ": " + line);
                        String[] parts = parseCSV(line);
                        if (parts.length == 2) {
                            try {
                                int parsedScore = Integer.parseInt(parts[1].trim());
                                users.add(parts[0]);
                                scores.add(parsedScore);
                                System.out.println("ScoreManager: Parsed entry: " + parts[0] + ", " + parsedScore);
                            } catch (NumberFormatException e) {
                                System.err.println("ScoreManager: Invalid score format at line " + lineNumber + ": " + parts[1]);
                            }
                        } else {
                            System.err.println("ScoreManager: Invalid CSV format at line " + lineNumber + ": " + line);
                        }
                    }
                }
            }

            // Update or add score
            int userIndex = users.indexOf(username);
            if (userIndex != -1) {
                if (score > scores.get(userIndex)) {
                    scores.set(userIndex, score);
                    System.out.println("ScoreManager: Updated score for " + username + " to " + score);
                } else {
                    System.out.println("ScoreManager: Score " + score + " not higher than existing " + scores.get(userIndex) + " for " + username);
                }
            } else {
                users.add(username);
                scores.add(score);
                System.out.println("ScoreManager: Added new score for " + username + ": " + score);
            }

            // Write back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (int i = 0; i < users.size(); i++) {
                    String entry = String.format("%s,%d\n", escapeCSV(users.get(i)), scores.get(i));
                    writer.write(entry);
                    System.out.println("ScoreManager: Wrote entry: " + entry.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("ScoreManager: Error saving high score for " + username + ": " + e.getMessage());
        }
    }

    public ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    System.out.println("ScoreManager: Skipping empty line at " + lineNumber);
                    continue;
                }
                System.out.println("ScoreManager: Read line " + lineNumber + ": " + line);
                String[] parts = parseCSV(line);
                if (parts.length == 2) {
                    users.add(parts[0]);
                    System.out.println("ScoreManager: Added user: " + parts[0]);
                } else {
                    System.err.println("ScoreManager: Invalid CSV format at line " + lineNumber + ": " + line);
                }
            }
            System.out.println("ScoreManager: Retrieved " + users.size() + " users");
        } catch (IOException e) {
            System.err.println("ScoreManager: Error reading users: " + e.getMessage());
        }
        return users;
    }

    public ArrayList<Integer> getScores() {
        ArrayList<Integer> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    System.out.println("ScoreManager: Skipping empty line at " + lineNumber);
                    continue;
                }
                System.out.println("ScoreManager: Read line " + lineNumber + ": " + line);
                String[] parts = parseCSV(line);
                if (parts.length == 2) {
                    try {
                        int score = Integer.parseInt(parts[1].trim());
                        scores.add(score);
                        System.out.println("ScoreManager: Added score: " + score);
                    } catch (NumberFormatException e) {
                        System.err.println("ScoreManager: Invalid score format at line " + lineNumber + ": " + parts[1]);
                    }
                } else {
                    System.err.println("ScoreManager: Invalid CSV format at line " + lineNumber + ": " + line);
                }
            }
            System.out.println("ScoreManager: Retrieved " + scores.size() + " scores");
        } catch (IOException e) {
            System.err.println("ScoreManager: Error reading scores: " + e.getMessage());
        }
        return scores;
    }

    public void close() {
        // No connection to close for file-based storage
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String[] parseCSV(String line) {
        if (line == null || line.trim().isEmpty()) return new String[0];
        String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                parts[i] = parts[i].substring(1, parts[i].length() - 1).replace("\"\"", "\"");
            }
        }
        return parts;
    }
}