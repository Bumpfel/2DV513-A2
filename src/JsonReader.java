import models.Comment;
import models.Link;
import models.RedditData;
import models.Subreddit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {
  private Set<Comment> comments = new HashSet<>();
  private Set<Link> links = new HashSet<>();
  private Set<Subreddit> subreddits = new HashSet<>();

  public static void main(String[] args) {
    new JsonReader(new File("data/big/RC_2007-10")); // 85 MB
    // mapJson(new File("data/big/RC_2011-07")); // 5.62 GB
  }

  public JsonReader(File file) {
    mapJson(file);
  }

  void mapJson(File file) {
    // Making three types of objects and modifying them instead of creating new objects all the time (for performance reasons)
    Comment comment = new Comment();
    Link link = new Link();
    Subreddit subreddit = new Subreddit();
    RedditData dataPiece;

    try(Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("\n"); // set delimiter to line

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      long timestamp = System.currentTimeMillis();

      // loop through each line
      while(scanner.hasNext()) {
        String str = scanner.next();
        dataPiece = mapper.readValue(str, RedditData.class); // parse line to RedditData object
        
        comments.add(comment.set(dataPiece));
        links.add(link.set(dataPiece));
        subreddits.add(subreddit.set(dataPiece));
      }
      double timeTaken = (double) (System.currentTimeMillis() - timestamp) / 1000;
      System.out.println("done in " + timeTaken + " s");
      System.out.println("comments: " + comments.size() + ", links: " + links.size() + ", subreddits: " + subreddits.size());

    } catch(IOException e) {
      System.err.println(e.getMessage());
    }
  }

}