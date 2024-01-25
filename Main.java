public class Main {

  private static final int R_WORD = 0;
  private static final int GUESS = 1;
  private static final int ALPHABET_SIZE = 26;
  private static final int MAX_GUESSES = 5;
  
  public static void main(String[] args) {
    try {
      java.util.HashSet<String> words = CreateDictionary.toSet("dictionary.txt");
      java.util.ArrayList<String> list = new java.util.ArrayList<>(words);

      int randomIndex = new java.util.Random().nextInt(words.size());
      String randomWord = list.get(randomIndex).toUpperCase();

      int numGuesses = 0, matchedChars = 0;
      java.util.Scanner scanner = new java.util.Scanner(System.in);

      while (matchedChars < randomWord.length() && numGuesses < MAX_GUESSES) {
        matchedChars = 0;
        
        System.out.println("Enter your guess: ");
        String guess = scanner.nextLine();
  
        while (guess.length() != randomWord.length() || !words.contains(guess.toLowerCase())) {
          System.out.println("Guess must be " + randomWord.length() + " letters long and in the dictionary!");
          System.out.println("Enter your guess: ");
          guess = scanner.nextLine();
        }
  
        guess = guess.toUpperCase();
        numGuesses++;
  
        // first row is for the randomWord; second is for the guess
        int[][] counts = new int[2][ALPHABET_SIZE]; 
        for (int i = 0; i < randomWord.length(); i++) {
          counts[R_WORD][randomWord.charAt(i) - 'A']++;
          counts[GUESS][guess.charAt(i) - 'A']++;
        }
  
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < randomWord.length(); i++) {
          output.append(guess.charAt(i));
          if (guess.charAt(i) == randomWord.charAt(i)) {
            output.append("#");
            counts[R_WORD][guess.charAt(i) - 'A']--;
            matchedChars++;
          } else if (counts[R_WORD][guess.charAt(i) - 'A'] > 0
                && counts[GUESS][guess.charAt(i) - 'A'] <= counts[R_WORD][guess.charAt(i) - 'A']) {
            output.append("?");
          } else {
            output.append("!");
          }
          counts[GUESS][guess.charAt(i) - 'A']--;
        }
  
        System.out.println(output.toString());
      }

      System.out.println("Word was: " + randomWord);

      scanner.close();
    } catch (java.io.FileNotFoundException e) {
      System.out.println(e);
    }
  }
}