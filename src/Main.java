import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // Inputting the file
        File file = new File(args[0]);
        StringBuilder s = new StringBuilder();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                s.append(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + file.getName());
        }

        // Splitting the passage into sentences
        String[] sentences = s.toString().split("[.!?]+\\s*");
        double lengthOfSentences = sentences.length;

        // Splitting the sentences into words and directly taking the count
        String[] words = s.toString().split("\\s");
        double lengthOfWords = words.length;


        // Finding the length of Characters
        double lengthOfCharacter = s.toString().replaceAll("[\\n\\t\\s]*", "").length();

        // Finding the number of Syllables
        int[] syllables = new int[words.length];
        for (int i = 0; i < syllables.length; i++) {
            syllables[i] = countSyllables(words[i]);
        }
        int numberOfSyllables = 0;
        for (int n : syllables) {
            numberOfSyllables += n;
        }

        // Finding the number of polysyllables
        int numberOfPolysyllables = 0;
        for (int syllable : syllables) {
            if (syllable > 2) {
                numberOfPolysyllables++;
            }
        }


        // Readability Score
        double ariScore = calculateARIScore(lengthOfCharacter, lengthOfWords, lengthOfSentences);

        // Flesch–Kincaid Score
        double fkrtScore = calculateFKRTScore(lengthOfWords, lengthOfSentences, numberOfSyllables);

        // SMOG Score
        double smogScore = calculateSMOGScore(numberOfPolysyllables, lengthOfSentences);

        // Coleman-Liau Score
        double coliScore = calculateColiScore(lengthOfSentences, lengthOfWords, lengthOfCharacter);

        // Readability age
        int ariAge = calculateFromIndexScore(ariScore);

        // Flesch-Kincaid age
        int fkrtAge = calculateFromIndexScore(fkrtScore);

        // SMOG age
        int smogAge = calculateFromIndexScore(smogScore);

        // Coleman-Liau age
        int coliAge = calculateFromIndexScore(coliScore);

        // average age
        double avgAge = (double)(ariAge + fkrtAge + smogAge + coliAge) / 4;

        // Output
        System.out.println("Words: " + (int)lengthOfWords);
        System.out.println("Sentences: " + (int)lengthOfSentences);
        System.out.println("Characters: " + (int)lengthOfCharacter);
        System.out.println("Syllables: " + numberOfSyllables);
        System.out.println("Polysyllables: " + numberOfPolysyllables);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        System.out.println();
        char ch = 8211;
        switch (str) {
            case "ARI":
                System.out.printf("Automated Readability Index: %.2f (about %d year olds).%n",ariScore,ariAge);
                break;
            case "FK":
                System.out.printf("Flesch%cKincaid readability tests: %.2f (about %d year olds).%n",ch,fkrtScore,fkrtAge);
                break;
            case "SMOG":
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).%n",smogScore,smogAge);
                break;
            case "CL":
                System.out.printf("Coleman%cLiau index: %.2f (about %d year olds).%n",ch, coliScore, coliAge);
                break;
            case "all":
                System.out.printf("Automated Readability Index: %.2f (about %d year olds).%n",ariScore,ariAge);
                System.out.printf("Flesch%cKincaid readability tests: %.2f (about %d year olds).%n",ch,fkrtScore,fkrtAge);
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).%n",smogScore,smogAge);
                System.out.printf("Coleman%cLiau index: %.2f (about %d year olds).%n",ch, coliScore, coliAge);
                System.out.printf("This text should be understood in average by %.2f year olds.%n", avgAge);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + str);

        }


    }

    public static double calculateARIScore(double lengthOfCharacter, double lengthOfWords, double lengthOfSentences) {
        return ((4.71 * (lengthOfCharacter / lengthOfWords)) + (0.5 * (lengthOfWords / lengthOfSentences)) - 21.43);
    }

    public static double calculateFKRTScore(double lengthOfWords, double lengthOfSentences, double numberOfSyllables) {
        return 0.39 * (lengthOfWords/lengthOfSentences) + 11.8 * (numberOfSyllables/lengthOfWords) - 15.59;
    }

    public static double calculateSMOGScore(double polySyllables, double lengthOfSentences) {
        return 1.043 * Math.sqrt(polySyllables * (30/lengthOfSentences)) + 3.1291;
    }

    public static double calculateColiScore(double lengthOfSentences, double lengthOfWords, double lengthOfCharacters) {
        double L = lengthOfCharacters / lengthOfWords * 100.0;
        double S = 100 / (lengthOfWords / lengthOfSentences);

        return 0.0588 * L - 0.296 * S - 15.8;
    }

    // Method to find the number of syllables
    public static int countSyllables(String word) {
        String s = "(?i)[bcdfghjklmnpqrstvwxz]*[aeiouy]+[bcdfghjklmnpqrstvwxz]*";
        Matcher m = Pattern.compile(s).matcher(word);
        int count = 0;
        while (m.find()) {
            count++;
        }
        if(count > 1 && word.endsWith("e")) {
            count -= 1;
        }
        return Math.max(count,1);
    }

    public static int calculateFromIndexScore(double score) {
        int scoreOfCeil = (int) Math.ceil(score);
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(1,6);
        hashMap.put(2,7);
        hashMap.put(3,9);
        hashMap.put(4,10);
        hashMap.put(5,11);
        hashMap.put(6,12);
        hashMap.put(7,13);
        hashMap.put(8,14);
        hashMap.put(9,15);
        hashMap.put(10,16);
        hashMap.put(11,17);
        hashMap.put(12,18);
        hashMap.put(13,24);
        hashMap.put(14,25);

        return hashMap.getOrDefault(scoreOfCeil, 25);
    }
}

