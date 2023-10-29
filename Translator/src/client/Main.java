package client;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome!");
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("If you want to translate a single word, please press 1.");
            System.out.println("If you want to translate all words in a file, please press 2.");
            System.out.println("If you want to quit, please press 3.");

            int answer = Integer.parseInt(scanner.nextLine());

            System.out.println("Answer: " + answer);

            if(answer == 1) {
                System.out.println("Please, enter the word you want to be translated.");
                String word = scanner.nextLine();
                System.out.println("Word: " + word);

                System.out.println("Please, enter the language in which you want the word to be translated.");
                String languageFrom = scanner.nextLine();
                System.out.println("Language from: " + languageFrom);

                System.out.println("Please, enter the language in which you want the word to be translated.");
                String languageTo = scanner.nextLine();
                System.out.println("Language to: " + languageTo);

                WordClient wordClient = new WordClient("127.0.0.1", 3000);
                wordClient.setUpConnection();

                String translatedWord = wordClient.getTranslatedWord(word, languageFrom, languageTo);
                System.out.println("Translated word: " + translatedWord);
                System.out.println("");

                wordClient.tearDownConnection();
            } else if(answer == 2) {
                System.out.println("Please, enter the path to the file with words that you want to be translated.");
                String filePath = scanner.nextLine();
                System.out.println("File path: " + filePath);

                System.out.println("Please, enter the language from which you want the words in the file to be translated.");
                String languageFrom = scanner.nextLine();
                System.out.println("Language from: " + languageFrom);

                System.out.println("Please, enter the language in which you want the words in the file to be translated.");
                String languageTo = scanner.nextLine();
                System.out.println("Language to: " + languageTo);

                FileClient fileClient = new FileClient("127.0.0.1", 3000);
                fileClient.setUpConnection();

                fileClient.getTranslatedFile(filePath, languageTo, languageFrom);
                String[] filePathParts = filePath.split("\\.");
                String newFilePath = filePathParts[0] + "_new." + filePathParts[1];
                System.out.println("Translated file can be found at " + newFilePath);
                System.out.println("");

                fileClient.tearDownConnection();
            } else if(answer == 3) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.err.println("Wrong input! Please, choose 1, 2 or 3.");
            }
        }
    }
}
