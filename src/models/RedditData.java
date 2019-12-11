package models;

public class RedditData {

  public String name, id, parent_id, link_id, author, body, subreddit_id, subreddit, score, created_utc;
  // public String score_hidden, gilded, author_flair_text, author_flair_css_class, name, downs, ups, distinguished, controversiality, archived, edited, retrieved_on;

  @Override
  public String toString() {
    return "{\n" +
      "\tid:" + id + ",\n" +
      "\tparent_id:" + parent_id + ",\n" +
      "\tlink_id:" + link_id + ",\n" +
      "\tauthor:" + author + "\n" +
      "\tbody:" + body + "\n" +
      "\tsubreddit:" + subreddit + "\n" +
      "\tsubreddit_id:" + subreddit_id + "\n" +
      "\tscore:" + score + "\n" +
      "\tcreated_utc:" + created_utc + "\n" +
      "}";
  }
}