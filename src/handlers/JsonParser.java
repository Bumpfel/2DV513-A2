package handlers;

import models.Comment;
import models.Link;
import models.RedditData;
import models.Subreddit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
  private Scanner scanner;
  private File file;

  private Set<Subreddit> allSubreddits = new HashSet<>();
  private Set<Link> allLinks = new HashSet<>();
  private Set<Comment> allComments = new HashSet<>();

  private Set<Subreddit> subreddits = new HashSet<>();
  private Set<Link> links = new HashSet<>();
  private Set<Comment> comments = new HashSet<>();

  // stats for prints
  private int batches;
  private int parsedSubreddits;
  private int parsedLinks;
  private int parsedComments;
  private int batchSize;
  private long totalTimeTaken;
  private boolean debug;
  private long bytesScanned;

  public long getBytesScanned() { return bytesScanned; }

  public Set<Subreddit> getSubreddits() { return new HashSet<>(subreddits); }
  public Set<Link> getLinks() { return new HashSet<>(links); }
  public Set<Comment> getComments() { return new HashSet<>(comments); }


  public JsonParser(File _file, int _batchSize, boolean debugPrints) {
    file = _file;
    batchSize = _batchSize;
    debug = debugPrints;
    try {
      scanner = new Scanner(file);
      scanner.useDelimiter("\n"); // set delimiter to line break
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void mapNextBatch() {
    RedditData dataPiece = null;

    subreddits.clear();
    links.clear();
    comments.clear();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    long timestamp = System.currentTimeMillis();

    // parse the lines in the file with a max batch size
    // for(int i = 0; i < batchSize && scanner.hasNext(); i++) {
    while(subreddits.size() + links.size() + comments.size() < batchSize && scanner.hasNext()) {
      try {
        String str = scanner.next();
        bytesScanned += str.length();
        dataPiece = mapper.readValue(str, RedditData.class); // parse line to RedditData object

        Subreddit subreddit = new Subreddit(dataPiece);
        Link link = new Link(dataPiece);
        Comment comment = new Comment(dataPiece);

        // create objects from redditdata and insert into sets
        if(!allSubreddits.contains(subreddit))
          subreddits.add(subreddit);
        if(!allLinks.contains(link))
          links.add(link);
        if(!allComments.contains(comment))
          comments.add(comment);

        allSubreddits.add(subreddit);
        allLinks.add(link);
        allComments.add(comment);
      } catch(IOException e) {
        System.err.println(e.getMessage());
      }
    }
    
    totalTimeTaken += System.currentTimeMillis() - timestamp;
    parsedSubreddits += subreddits.size();
    parsedLinks += links.size();
    parsedComments += comments.size();

    if(debug) {
      System.out.println("------------");
      System.out.print("Batch #" + (++batches) + ". Batch size: " + (subreddits.size() + links.size() + comments.size()));
      System.out.println(" - " + comments.size() + " comments, " + links.size() + " links, " + subreddits.size() + " subreddits");
      System.out.print("Scanned " + Math.round(bytesScanned / Math.pow(1024, 2)) + " MB / " + Math.round(file.length() / Math.pow(1024, 2)) + " MB... ");
    }

    if(!scanner.hasNext()) {
      String displayTime = ((double) totalTimeTaken / 1000) + " s";
      String fileSize = Math.round(file.length() / Math.pow(1024, 2)) + " MB";
      System.out.println();
      System.out.println("====  JsonParser  ====");
      System.out.println("data parsed from '" + file.getName() + "' (" + fileSize + ") in " + displayTime);
      System.out.println("comments: " + parsedComments + ", links: " + parsedLinks + ", subreddits: " + parsedSubreddits);
      System.out.println("======================");
      System.out.println();
    }
  }

  public boolean hasNextBatch() {
    return scanner.hasNext();
  }



  // TODO unused
  public void mapJson(File file) {
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
