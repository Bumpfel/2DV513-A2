package models;

public class Link extends AbstractData {
  private String score, subreddit_id;  

  public Link() {}

  public Link set(RedditData data) {
    id = data.link_id;
    score = data.score; // TODO osäker på om denna kan tillhöra en länk
    subreddit_id = data.subreddit_id;

    return this;
  }
}
