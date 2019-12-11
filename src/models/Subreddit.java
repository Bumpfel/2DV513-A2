package models;

public class Subreddit extends Data {
  private String subreddit;

  public Subreddit() {}

  public Subreddit set(RedditData data) {
    subreddit = data.subreddit;
    id = data.subreddit_id;

    return this;
  }
}
