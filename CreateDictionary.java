public class CreateDictionary {
  
  public static void main(String[] args) throws java.io.FileNotFoundException {
    if (args == null || args.length == 0) {
      System.out.println("Usage: java CreateDictionary <filename> ...");
      System.out.println("- files must be in resources/");
      System.exit(1);
    }

    java.util.HashSet<String> words = new java.util.HashSet<String>();

    for (String filename : args) {
      words.addAll(toSet(filename));
    }

    java.util.ArrayList<String> list = new java.util.ArrayList<>(words);
    java.util.Collections.sort(list);

    java.io.PrintWriter writer = new java.io.PrintWriter(
        new java.io.File("resources", "dictionary.txt"));

    for (String word : list) {
      writer.println(word);
      writer.flush();
    }

    writer.close();
    System.out.println(words.size() + " words written to dictionary.txt");
  }

  public static java.util.HashSet<String> toSet(String source) throws java.io.FileNotFoundException {
    java.util.HashSet<String> set = new java.util.HashSet<String>();

    java.util.Scanner scanner = new java.util.Scanner(
        new java.io.File("resources", source));

    while (scanner.hasNextLine()) {
      set.add(scanner.nextLine());
    }

    scanner.close();
    return set;
  }
}