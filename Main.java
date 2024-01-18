public class Main {
  public static void main(String[] args) {
    try {
      java.util.HashSet<String> words = CreateDictionary.toSet("dictionary.txt");
      java.util.ArrayList<String> list = new java.util.ArrayList<>(words);

      int randomWord = new java.util.Random().nextInt(words.size());
      System.out.println(list.get(randomWord));
    } catch (java.io.FileNotFoundException e) {
      System.out.println(e);
    }
  }
}