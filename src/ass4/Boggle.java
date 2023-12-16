package ass4;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class Boggle {
    public String[][] board;
    public int[][] marked;
    public int N; 

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final double[] FREQUENCIES = {
        0.08167, 0.01492, 0.02782, 0.04253, 0.12703, 0.02228,
        0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025,
        0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
        0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
        0.01974, 0.00074
    };
    public Boggle(String[][] board) {
        N = board.length;
        this.board = new String[N][N];
        this.marked = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, N);
        }
    }
    public Boggle(int N) {
        this.N = N;
        this.board = new String[N][N];
        this.marked = new int[N][N];
        randomBoard();
    }
    //Generating for a randomboard
    private void randomBoard() {
        Random rand = new Random();
        double[] cumulativeFrequencies = new double[ALPHABET.length()];
        cumulativeFrequencies[0] = FREQUENCIES[0];
        for (int i = 1; i < ALPHABET.length(); i++) {
            cumulativeFrequencies[i] = cumulativeFrequencies[i - 1] + FREQUENCIES[i];
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double r = rand.nextDouble();
                int k = 0;
                while (r > cumulativeFrequencies[k]) {
                    k++;
                }
                board[i][j] = String.valueOf(ALPHABET.charAt(k)).toLowerCase();
            }
        }
    }
    public Boggle (String [] dice, long seed) { 
        double d = Math.sqrt(dice.length);
        N = (int) d;
        board = new String [N][N];
        marked = new int [N][N];
        Random rand = new Random(seed);
        
        //shake the dice
        for (int k=0; k<dice.length-1; k++) {
         int r = rand.nextInt(k, dice.length);
         //swap dice[k] and dice [r]
         String temp = dice[k];
         dice[k] = dice[r];
         dice[r] = temp;
        }
        int k = 0;
        for (int i=0; i<N; i++) {
         for (int j=0; j<N; j++) {
          String die = dice[k++];
          int r = rand.nextInt(die.length());
          board[i][j] = String.valueOf(die.charAt(r)).toLowerCase();
         }
        }
       } 
    // Boggle provided by Barsky (with updated testcases)
    

    public boolean matchWord(String word) {
        reset();
        if (word == null || word.length() < 3) {
            return false;
        }
        word = word.replace("qu", "q").toLowerCase(); // Adjusting for Qu case
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (search(i, j, word, 0)) {
                    marked(word); // Marking the found word on the board
                    return true;
                }
            }
        }
        return false;
    }

    private void marked(String word) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (marked[i][j] == 1) {
                    board[i][j] = board[i][j].toUpperCase();
                }
            }
        }
    }
    
    private void reset() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                marked[i][j] = 0;
                board[i][j] = board[i][j].toLowerCase();
                // This is done so that the testmark can be satisfied
            }
        }
    }
    
    private boolean search(int i, int j, String word, int index) {
        if (index == word.length()) {
            return true; // word has been found
        }
        if (i < 0 || i >= N || j < 0 || j >= N || marked[i][j] == 1 || !board[i][j].equals(String.valueOf(word.charAt(index)).toLowerCase())) {
            return false; // Out of bounds, already visited, or character does not match
        }
        marked[i][j] = 1;
        // adjacent cells
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if ((row != 0 || col != 0) && search(i + row, j + col, word, index + 1)) {
                    return true; // Found word
                }
            }
        }
        marked[i][j] = 0; // Backtrack
        return false; // Word not found 
    }



public static List<String> getAllValidWords(String dictionaryName, String boardName) {
    try {
        Set<String> dictionary = readDictionary(dictionaryName);
        Boggle boggleBoard = new Boggle(boardName);
        Set<String> foundWords = new HashSet<>();

    for (int i = 0; i < boggleBoard.N; i++) {
        for (int j = 0; j < boggleBoard.N; j++) {
            inBoard(boggleBoard, dictionary, foundWords, "", i, j, new boolean[boggleBoard.N][boggleBoard.N]);
        }
    }

    List<String> sortedWords = new ArrayList<>(foundWords);
    Collections.sort(sortedWords); // Sort the words alphabetically
    return sortedWords;
} catch (Exception e) {
    System.err.println("Error occurred in getAllValidWords: " + e.getMessage());
    return new ArrayList<>(); // Return an empty list in case of error
}
}

private static void inBoard(Boggle boggleBoard, Set<String> dictionary, Set<String> foundWords, 
    String currentWord, int i, int j, boolean[][] visited) {
        if (i < 0 || i >= boggleBoard.N || j < 0 || j >= boggleBoard.N || visited[i][j]) 
        {
            return;
        }

        currentWord += boggleBoard.board[i][j];
        visited[i][j] = true;

        // Checking if the current word is in the dictionary 
        if (currentWord.length() >= 3 && dictionary.contains(currentWord)) 
        {
            foundWords.add(currentWord);
        }

        // Checking through adjacent cells
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) 
            {
                if (row != 0 || col != 0) 
                {
                    inBoard(boggleBoard, dictionary, foundWords, currentWord, i + row, j + col, visited);
                }
            }
        }

    visited[i][j] = false; // Backtrack
}




public Boggle(String filename) {
    try (Scanner scanner = new Scanner(new File(filename))) {
        N = scanner.nextInt(); // first line has size of board
        scanner.nextLine(); 
        board = new String[N][N];
        marked = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (scanner.hasNext()) {
                    board[i][j] = scanner.next().toLowerCase();
                }
            }
        }
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filename);
    }
}
   
private static Set<String> readDictionary(String dictionaryName) {
    Set<String> dictionary = new HashSet<>();
    try (Scanner scanner = new Scanner(new File(dictionaryName))) {
        while (scanner.hasNextLine()) 
        {
            String word = scanner.nextLine().trim().toLowerCase();
                dictionary.add(word);
        }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Dictionary file not found: " + dictionaryName);
        }

        return dictionary;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Boggle game;
    
        if (args.length == 0) {
            System.out.println("No arguments provided. Exiting...");
            scanner.close();
            return;
        }
    
        try {
            if (isNumeric(args[0])) {
                // If the first argument is numeric, generate a random board of that size
                int size = Integer.parseInt(args[0]);
                game = new Boggle(size);
            } else {
                // If the first argument is a filename, load the board from the file
                game = new Boggle(args[0]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid argument. Expected a filename or a board size.");
            scanner.close();
            return;
        }
    
        // Initialize the game with a board file and dictionary file
        Set<String> dictionary = readDictionary(args[1]); // Load the dictionary

        int score = 0;
        boolean keepPlaying = true;
        String input;

        while (keepPlaying) {
            System.out.println(game.toString()); // Print the current state of the board

            System.out.println("Enter a word (or 'exit' to quit): ");
            input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                keepPlaying = false;
            } else if (input.length() < 3) {
                System.out.println("Word must be at least 3 letters long.");
            } else if (dictionary.contains(input) && game.matchWord(input)) {
                // Word is valid and found on the board
                score += calculateScore(input); // Update the score
                System.out.println("Word accepted! Current score: " + score);
            } else {
                System.out.println("Invalid word or not found on the board.");
            }

            game.reset(); // Reset the board for the next round
        }

        System.out.println("Final score: " + score);
        System.out.println("Thank you for playing Boggle!");
        scanner.close();
    }
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // Method to calculate score based on word length
    private static int calculateScore(String word) {
        int length = word.length();
        if (length == 3 || length == 4) {
            return 1;
        } else {
            return length - 2;
        }
    }
    
@Override
    public String toString() {
           StringBuilder sb = new StringBuilder();
           for (int i = 0; i < N; i++) {
               for (int j = 0; j < N; j++) {
                   sb.append(board[i][j]);
                   // Add a space after each character
                   if (j < N) {
                       sb.append(" ");
                   }
               }
               // Add a newline character after each row
               if (i < N) {
                   sb.append(System.lineSeparator());
               }
           }
           return sb.toString();
       }
       
}