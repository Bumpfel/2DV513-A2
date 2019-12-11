import java.io.File;
import java.sql.*;
import java.util.Set;

import models.Comment;
import models.Link;
import models.Subreddit;

public class DBHandler {
  private Connection conn;

  public static void main(String[] args) {
    JsonReader reader = new JsonReader(new File("data/big/RC_2007-10")); // 85 MB
    // JsonReader reader = new JsonReader(new File("data/big/RC_2011-07")); // 5.62 GB
    DBHandler db = new DBHandler();
    db.connect("reddit_test");
    
    int successfulInsertions = 0;

    for(Subreddit subreddit : reader.getSubreddits()) {
      successfulInsertions += db.exec("INSERT INTO subreddits SET id = '" + subreddit.getId() + "', name = '" + subreddit.getName() + "'") ? 1 : 0;
    }
    System.out.println(successfulInsertions + " successful subreddit insertions of " + reader.getSubreddits().size() + " total");
    
    successfulInsertions = 0;
    for(Link link : reader.getLinks()) {
      successfulInsertions += db.exec("INSERT INTO links SET id = '" + link.getId() + "', subreddit_id = '" + link.getSubredditId() + "', score = '" + link.getScore() + "'") ? 1 : 0;
    }
    System.out.println(successfulInsertions + " successful link insertions of " + reader.getLinks().size() + " total");

    // TODO comments

  }

  void runShowcaseCode() {
    DBHandler db = new DBHandler();
    db.connect("my_company");
    ResultSet res = db.execQuery("SELECT * FROM employees");
    db.printResults(res, "id", "firstname", "lastname");
  }


  void connect(String database) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, "root", "root");

    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  ResultSet execQuery(String query) {
    ResultSet rs = null;
    try {
      Statement stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  boolean exec(String query) {
    try {
      Statement stmt = conn.createStatement();
      stmt.execute(query);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }


  void printResults(ResultSet res, String...fields) {
    try {
      StringBuilder str = new StringBuilder();
      while(res.next()) {
        for(String field : fields) {
          str.append(res.getString(field) + " ");
        }
        System.out.println(str);
        str.delete(0, str.length());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
