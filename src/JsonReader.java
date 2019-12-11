import models.Comment;
import models.Link;
import models.RedditData;
import models.Subreddit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

  public static void main(String[] args) throws Exception {  
    List<RedditData> rawData= mapJson(new File("data/big/RC_2007-10")); // 85 MB
    // List<RedditData> rawData= mapJson(new File("data/big/RC_2011-07")); // 5.62 GB
    // List<RedditData> rawData= mapJson(new File("data/testdata.json"));
    separateStuff(rawData);
  }

  static List<RedditData> mapJson(File file) throws Exception {
    ArrayList<RedditData> redditData = new ArrayList<>();
    try(Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("\n");

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      long timestamp = System.currentTimeMillis();
      while(scanner.hasNext()) {
        redditData.add(mapper.readValue(scanner.next(), RedditData.class));
      }
      double timeTaken = (double) (System.currentTimeMillis() - timestamp);
      System.out.println("mapped in " + timeTaken + " ms");

    } catch(Exception e) {
      System.err.println(e.getMessage());
    }

    return redditData;    
  }

  static void separateStuff(List<RedditData> rawData) {
    Set<Comment> comments = new HashSet<>();
    Set<Link> links = new HashSet<>();
    Set<Subreddit> subreddits = new HashSet<>();

    // Making three types of objects and modifying them instead of creating new objects all the time (for performance reasons)
    Comment comment = new Comment();
    Link link = new Link();
    Subreddit subreddit = new Subreddit();

    long timestamp = System.currentTimeMillis();
    for(RedditData dataPiece : rawData) {
      comments.add(comment.set(dataPiece));
      links.add(link.set(dataPiece));
      subreddits.add(subreddit.set(dataPiece));
    }
    double timeTaken = (double) (System.currentTimeMillis() - timestamp) / 1000;
    System.out.println("done in " + timeTaken + " s");
    System.out.println("comments: " + comments.size() + ", links: " + links.size() + ", subreddits: " + subreddits.size());
  }

}