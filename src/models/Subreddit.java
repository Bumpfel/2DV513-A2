package models;

public class Subreddit extends AbstractData {
  private String subreddit;

  public String getName() { return subreddit; }

  public Subreddit() {}

  public Subreddit(RedditData data) {
    subreddit = data.subreddit;
    id = data.subreddit_id.split("_")[1];
  }
}
