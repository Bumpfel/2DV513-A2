package models;

public class Subreddit extends AbstractData {
  private String subreddit;

  public Subreddit() {}

  public Subreddit set(RedditData data) {
    subreddit = data.subreddit;
    id = data.subreddit_id;

    return this;
  }
}
