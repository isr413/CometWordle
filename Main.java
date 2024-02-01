import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

  private static final int R_WORD = 0;
  private static final int GUESS = 1;
  private static final int ALPHABET_SIZE = 26;
  private static final int MAX_GUESSES = 5;

  private static HashSet<String> WORDS;
  private static String random_word = null;
  private static int number_guesses = 0;

  public static void main(String[] args) throws FileNotFoundException, IOException {
    WORDS = CreateDictionary.toSet("dictionary.txt");
    listen("localhost", 80);
  }

  public static void assignRandomWord() throws IOException {
    ArrayList<String> list = new ArrayList<>(WORDS);

    int randomIndex = new Random().nextInt(WORDS.size());
    random_word = list.get(randomIndex).toUpperCase();
  }

  public static String checkGuess(String guess) {
    if (guess.length() != 5 || !WORDS.contains(guess))
      return "";
    guess = guess.toUpperCase();

    // first row is for the random_word; second is for the guess
    int[][] counts = new int[2][ALPHABET_SIZE];
    for (int i = 0; i < random_word.length(); i++) {
      counts[R_WORD][random_word.charAt(i) - 'A']++;
      counts[GUESS][guess.charAt(i) - 'A']++;
    }

    StringBuilder output = new StringBuilder();
    for (int i = 0; i < random_word.length(); i++) {
      output.append(guess.charAt(i));
      if (guess.charAt(i) == random_word.charAt(i)) {
        output.append("#");
        counts[R_WORD][guess.charAt(i) - 'A']--;
      } else if (counts[R_WORD][guess.charAt(i) - 'A'] > 0
          && counts[GUESS][guess.charAt(i) - 'A'] <= counts[R_WORD][guess.charAt(i) - 'A']) {
        output.append("?");
      } else {
        output.append("!");
      }
      counts[GUESS][guess.charAt(i) - 'A']--;
    }

    return output.toString();
  }

  public static void listen(String hostname, int port) throws IOException {
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
    server.createContext("/", new MyHttpHandler());
    server.setExecutor(threadPoolExecutor);
    server.start();
    System.out.println(">>> Server started on port " + port);
    if (random_word == null) {
      assignRandomWord();
    }
  }

  public static class MyHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      String requestParamValue = null;
      System.out.println("received request");
      handleResponse(httpExchange);
      // if("GET".equals(httpExchange.getRequestMethod())) {
      // handleGetRequest(httpExchange);
      // }
    }

    public static void handleResponse(HttpExchange httpExchange) throws IOException {
      OutputStream output = httpExchange.getResponseBody();
      String requestURI = httpExchange.getRequestURI().toString();
      String[] params = requestURI.split("\\?")[1].split("=");

      String guess = "";
      for (int i = 0; i < params.length; i++) {
        if (params[i].equals("guess")) {
          guess = params[i + 1];
          break;
        }
      }

      String guessOut = checkGuess(guess);

      int matchedChars = 0;
      for (int i = 0; i < guessOut.length(); i++) {
        if (guessOut.charAt(i) == '#') {
          matchedChars++;
        }
      }
      if (matchedChars == random_word.length()) {
        assignRandomWord();
        number_guesses = 0;
      } else {
        number_guesses++;
      }

      StringBuilder str = new StringBuilder();
      if (guessOut.equals("")) {
        str.append("<p>")
            .append("Invalid guess")
            .append("</p>");
      } else {
        str.append("<guess>")
            .append(guessOut)
            .append("</guess>");
      }

      if (number_guesses >= MAX_GUESSES) {
        str.append("<p>")
           .append("Too many guesses")
           .append("</p>")
           .append("<word>")
           .append(random_word)
           .append("</word>");
        assignRandomWord();
        number_guesses = 0;
      }

      String response = str.toString();
      httpExchange.sendResponseHeaders(200, response.length());
      output.write(response.getBytes());
      output.flush();
      output.close();
      System.out.println("sent response");
    }
  }
}