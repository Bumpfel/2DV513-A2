package handlers;

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

public class JsonParser {
  public boolean debug = false;
  private Set<Comment> comments = new HashSet<>();
  private Set<Link> links = new HashSet<>();
  private Set<Subreddit> subreddits = new HashSet<>();

  public Set<Comment> getComments() {
    return new HashSet<>(comments);
  }
  public Set<Link> getLinks() {
    return new HashSet<>(links);
  }
  public Set<Subreddit> getSubreddits() {
    return new HashSet<>(subreddits);
  }

  public JsonParser(File file, boolean debugPrints) {
    debug = debugPrints;
    mapJson(file);
  }

  void mapJson(File file) {
    RedditData dataPiece;

    try(Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("\n"); // set delimiter to line break

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      long timestamp = System.currentTimeMillis();

      // loop through each line
      while(scanner.hasNext() && timestamp + 5000 > System.currentTimeMillis()) {
        String str = scanner.next();
        dataPiece = mapper.readValue(str, RedditData.class); // parse line to RedditData object
        
        // create objects from redditdata and insert into sets
        comments.add(new Comment(dataPiece));
        links.add(new Link(dataPiece));
        subreddits.add(new Subreddit(dataPiece));
      }
      double timeTaken = (double) (System.currentTimeMillis() - timestamp) / 1000;
      
      if(debug) {
        String fileSize = Math.round(file.length() / Math.pow(1024, 2)) + " MB";
        System.out.println("====  JsonParser  ====");
        System.out.println("data parsed from '" + file.getName() + "' (" + fileSize + ") in " + timeTaken + " s");
        System.out.println("comments: " + comments.size() + ", links: " + links.size() + ", subreddits: " + subreddits.size());
        System.out.println("======================");
      }

    } catch(IOException e) {
      System.err.println(e.getMessage());
    }
  }

}