package app;

import java.io.File;

import handlers.DBHandler;
import handlers.JsonParser;
import models.Link;
import models.Subreddit;

class App {

  public static void main(String[] args) {
    // parse json data and establish a db connection
    JsonParser parser = new JsonParser(new File("data/big/RC_2007-10"), true); // 85 MB
    // JsonParser parser = new JsonParser(new File("data/big/RC_2011-07"), true); // 5.62 GB
    DBHandler db = new DBHandler();
    db.connect("reddit_test");

    db.exec("DELETE FROM subreddits");
    db.exec("DELETE FROM links");
    db.exec("DELETE FROM comments");
    
    
    // insert subreddit
    db.batchInsertion("subreddits", parser.getSubreddits(), true);
    
    // insert links
    db.batchInsertion("links", parser.getLinks(), true);

    // insert comments (batch)
    db.batchInsertion("comments", parser.getComments(), true);
  }





  void old() {
    JsonParser parser = new JsonParser(new File("data/big/RC_2007-10"), true); // 85 MB

    // insert subreddits
    int successfulInsertions = 0;
    for(Subreddit subreddit : parser.getSubreddits()) {
      // successfulInsertions += db.exec("INSERT INTO subreddits SET id = '" + subreddit.getId() + "', name = '" + subreddit.getName() + "'") ? 1 : 0;
    }
    System.out.println(successfulInsertions + " successful subreddit insertions of " + parser.getSubreddits().size() + " total");
    
    // insert links
    successfulInsertions = 0;
    for(Link link : parser.getLinks()) {
      // successfulInsertions += db.exec("INSERT INTO links SET id = '" + link.getId() + "', subreddit_id = '" + link.getSubredditId() + "', score = '" + link.getScore() + "'") ? 1 : 0;
    }

  }
}