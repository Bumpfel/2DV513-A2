package app;

import java.io.File;

import handlers.DBHandler;
import handlers.JsonParser;
import handlers.TimeFormatter;

class App {
  private static boolean BATCH_PRINTS = true;
  private static final int BATCH_SIZE = 100000;
  private static final String db = "reddit_test";

  public static void main(String[] args) {
    // parse json data and establish a db connection
    DBHandler dbHandler = new DBHandler();
    dbHandler.connect(db);
    dbHandler.exec("DELETE FROM comments");
    dbHandler.exec("DELETE FROM links");
    dbHandler.exec("DELETE FROM subreddits");
    if(App.BATCH_PRINTS) {
      System.out.println("Database cleared");
    }

    // File file = new File("data/big/test"); // 485 MB
    // File file = new File("data/big/RC_2007-10"); // 85 MB
    File file = new File("data/big/RC_2011-07"); // 5.62 GB
    JsonParser parser = new JsonParser(file, App.BATCH_SIZE, App.BATCH_PRINTS);

    long totalTimeTaken = 0, timestamp;
    while(parser.hasNextBatch()) {
      timestamp = System.currentTimeMillis();
      parser.mapNextBatch();

      dbHandler.batchInsertion("comments", parser.getComments());

      if(App.BATCH_PRINTS)  {
        // time estimation and prints
        long timeTaken = System.currentTimeMillis() - timestamp;
        totalTimeTaken += timeTaken;
        System.out.println("inserted into db. Took " + TimeFormatter.format(timeTaken));
        long speed = parser.getBytesScanned() / totalTimeTaken; // B/ms
        long estTimeRemaining = (file.length() - parser.getBytesScanned()) / speed;
        System.out.println(" - estimated time remaining: " + TimeFormatter.format(estTimeRemaining));
      }
    }
    dbHandler.batchInsertion("subreddits", parser.getSubreddits());
    dbHandler.batchInsertion("links", parser.getLinks());

    System.out.println(parser.getParserStatus());
    System.out.println(dbHandler.getDBHandlerStatus());
    System.out.println("------------------------");
    System.out.println("Total time: " + (TimeFormatter.format(parser.getTotalTimeTaken() + dbHandler.getTotalTimeTaken())));
  }

}
