import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PoliticalQuizApp {
    private static final Map<String, Map<String, String>> partyAnswers = new HashMap<>();
    private static final Map<String, Map<String, Double>> answerWeights = new HashMap<>();
    private static final Map<String, Integer> userAnswers = new HashMap<>();

    private static void initializePartyAnswers() {
        // Initialize party answers
        Map<String, String> democraticAnswers = new HashMap<>();
        democraticAnswers.put("A", "Democratic answer for option A.");
        democraticAnswers.put("B", "Democratic answer for option B.");
        democraticAnswers.put("C", "Democratic answer for option C.");
        democraticAnswers.put("D", "Democratic answer for option D.");
        partyAnswers.put("Democratic", democraticAnswers);

        // Add answers for other parties
        Map<String, String> republicanAnswers = new HashMap<>();
        republicanAnswers.put("A", "Republican answer for option A.");
        republicanAnswers.put("B", "Republican answer for option B.");
        republicanAnswers.put("C", "Republican answer for option C.");
        republicanAnswers.put("D", "Republican answer for option D.");
        partyAnswers.put("Republican", republicanAnswers);

        Map<String, String> independentAnswers = new HashMap<>();
        independentAnswers.put("A", "Independent answer for option A.");
        independentAnswers.put("B", "Independent answer for option B.");
        independentAnswers.put("C", "Independent answer for option C.");
        independentAnswers.put("D", "Independent answer for option D.");
        partyAnswers.put("Independent", independentAnswers);

        Map<String, String> progressiveAnswers = new HashMap<>();
        progressiveAnswers.put("A", "Progressive answer for option A.");
        progressiveAnswers.put("B", "Progressive answer for option B.");
        progressiveAnswers.put("C", "Progressive answer for option C.");
        progressiveAnswers.put("D", "Progressive answer for option D.");
        partyAnswers.put("Progressive", progressiveAnswers);
    }

    private static void initializeAnswerWeights() {
        // Load answer weights from data files
        loadAnswerWeightsFromFile("democratic_weights.txt", "Democratic");
        loadAnswerWeightsFromFile("republican_weights.txt", "Republican");
        loadAnswerWeightsFromFile("independent_weights.txt", "Independent");
        loadAnswerWeightsFromFile("progressive_weights.txt", "Progressive");
    }

    private static void loadAnswerWeightsFromFile(String filename, String party) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String option = parts[0].trim();
                    double weight = Double.parseDouble(parts[1].trim());
                    answerWeights.putIfAbsent(party, new HashMap<>());
                    answerWeights.get(party).put(option, weight);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading answer weights: " + e.getMessage());
        }
    }

    private static double getAnswerWeight(String party, String option) {
        Map<String, Double> partyWeights = answerWeights.get(party);
        if (partyWeights != null) {
            return partyWeights.getOrDefault(option, 0.0);
        } else {
            return 0.0;
        }
    }

    private static String getAnswer(String party, String option) {
        Map<String, String> partyOptions = partyAnswers.get(party);
        if (partyOptions != null) {
            return partyOptions.getOrDefault(option, "Invalid option.");
        } else {
            return "Invalid party.";
        }
    }

    private static void storeUserAnswer(String option, int score) {
        userAnswers.put(option, score);
    }

    private static void displayUserResults() {
        // Analyze user answers and determine their political affiliation
        double democraticScore = 0.0;
        double republicanScore = 0.0;
        double independentScore = 0.0;
        double progressiveScore = 0.0;

        for (Map.Entry<String, Integer> entry : userAnswers.entrySet()) {
            String option = entry.getKey();
            int score = entry.getValue();

            democraticScore += score * getAnswerWeight("Democratic", option);
            republicanScore += score * getAnswerWeight("Republican", option);
            independentScore += score * getAnswerWeight("Independent", option);
            progressiveScore += score * getAnswerWeight("Progressive", option);
        }

        double maxScore = Math.max(Math.max(democraticScore, republicanScore), Math.max(independentScore, progressiveScore));
        if (democraticScore == maxScore) {
            System.out.println("You are most likely affiliated with the Democratic party.");
        } else if (republicanScore == maxScore) {
            System.out.println("You are most likely affiliated with the Republican party.");
        } else if (independentScore == maxScore) {
            System.out.println("You are most likely an Independent.");
        } else if (progressiveScore == maxScore) {
            System.out.println("You are most likely affiliated with the Progressive party.");
        } else {
            System.out.println("Unable to determine your political affiliation.");
        }
    }

    private static void saveUserData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("user_data.dat"))) {
            out.writeObject(userAnswers);
            System.out.println("User data saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private static void loadUserData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("user_data.dat"))) {
            userAnswers.clear();
            userAnswers.putAll((Map<String, Integer>) in.readObject());
            System.out.println("User data loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializePartyAnswers();
        initializeAnswerWeights();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Political Quiz App!");
        System.out.println("Please answer the following 20 questions:");

        for (int i = 1; i <= 20; i++) {
            System.out.print("Question " + i + " (A-D): ");
            String option = scanner.nextLine().toUpperCase();
            int score = 21 - i;
            storeUserAnswer(option, score);
            System.out.println("You selected option " + option + ". " + getAnswer("Democratic", option));
        }

        displayUserResults();

        saveUserData();
        loadUserData();
    }
}