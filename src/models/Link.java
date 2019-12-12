package models;

public class Link extends AbstractData {
  private String score, subreddit_id;  

  public String getScore() { return score; }
  public String getSubredditId() { return subreddit_id; }

  public Link() {}

  public Link(RedditData data) {
    id = truncateId(data.link_id);
    score = data.score; // TODO osäker på om denna kan tillhöra en länk
    subreddit_id = data.subreddit_id.split("_")[1];
  }
}
