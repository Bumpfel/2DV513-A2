package app;

import java.io.File;

import handlers.DBHandler;
import handlers.JsonReader;
import models.Comment;
import models.Link;
import models.Subreddit;

class App {

  public static void main(String[] args) {
    // parse json data and establish a db connection
    JsonReader reader = new JsonReader(new File("data/big/RC_2007-10")); // 85 MB
    // JsonReader reader = new JsonReader(new File("data/big/RC_2011-07")); // 5.62 GB
    DBHandler db = new DBHandler();
    db.connect("reddit_test");
    
    
    // insert subreddits
    int successfulInsertions = 0;
    for(Subreddit subreddit : reader.getSubreddits()) {
      // successfulInsertions += db.exec("INSERT INTO subreddits SET id = '" + subreddit.getId() + "', name = '" + subreddit.getName() + "'") ? 1 : 0;
    }
    System.out.println(successfulInsertions + " successful subreddit insertions of " + reader.getSubreddits().size() + " total");
    

    // insert links
    successfulInsertions = 0;
    for(Link link : reader.getLinks()) {
      // successfulInsertions += db.exec("INSERT INTO links SET id = '" + link.getId() + "', subreddit_id = '" + link.getSubredditId() + "', score = '" + link.getScore() + "'") ? 1 : 0;
    }
    System.out.println(successfulInsertions + " successful link insertions of " + reader.getLinks().size() + " total");


    // insert comments
    successfulInsertions = 0;
    for(Comment comment : reader.getComments()) {
      String[] cols = { "id", "parent_id", "link_id", "body", "score", "created_utc" };
      String[] values = { comment.getId(), comment.getParentId(), comment.getLinkId(), comment.getBody(), comment.getScore(), comment.getCreatedUTC() };
      successfulInsertions += db.insert("comments", cols, values) ? 1 : 0;
      
      if(successfulInsertions >= 100) { // TODO temp limit
        break;
      }
    }
    System.out.println(successfulInsertions + " successful comment insertions of " + reader.getComments().size() + " total");
  }
}