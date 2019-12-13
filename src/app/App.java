package app;

import java.io.File;

import handlers.DBHandler;
import handlers.JsonParser;

class App {

  public static void main(String[] args) {
    // parse json data and establish a db connection
    DBHandler db = new DBHandler(false);
    db.connect("reddit_test");
    db.exec("TRUNCATE reddit_test.subreddits");
    db.exec("TRUNCATE reddit_test.links");
    db.exec("TRUNCATE reddit_test.comments");
    // db.exec("DELETE FROM subreddits");
    // db.exec("DELETE FROM links");
    // db.exec("DELETE FROM comments");
    System.out.println("Database cleared");
    
    // File file = new File("data/big/RC_2007-10"); // 85 MB
    File file = new File("data/big/RC_2011-07"); // 5.62 GB
    JsonParser parser = new JsonParser(file, 100000, true);

    long totalTimeTaken = 0, timestamp;
    while(parser.hasNextBatch()) {
      timestamp = System.currentTimeMillis();
      parser.mapNextBatch();
      db.batchInsertion("subreddits", parser.getSubreddits());
      db.batchInsertion("links", parser.getLinks());
      db.batchInsertion("comments", parser.getComments());

      // time estimation and prints
      long timeTaken = System.currentTimeMillis() - timestamp;
      totalTimeTaken += timeTaken;
      System.out.println("inserted into db. Took " + ((double) timeTaken / 1000) + " s");
      long speed = parser.getBytesScanned() / totalTimeTaken; // B/ms
      long estTimeRemaining = (file.length() - parser.getBytesScanned()) / speed / 1000;
      String displayTime = (estTimeRemaining > 60 ? estTimeRemaining / 60 + ":" + estTimeRemaining % 60 : estTimeRemaining + " s");
      System.out.println("est. time remaining: " + displayTime);
    }
    
    String displayTime = ((double) db.getTotalTimeTaken() / 1000) + " s";
    System.out.println(db.getSuccessfulInsertions() + " successful insertions " + " done in " + displayTime);
      System.out.println("============================");
      
  }

}
