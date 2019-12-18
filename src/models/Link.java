package models;

public class Link extends AbstractData {
  private String subreddit_id;

  public String getSubredditId() { return subreddit_id; }

  public Link() {}

  public String getInsertCols() {
    return "id, subreddit_id";
  }

  public String[] getInsertValues() {
    return new String[] { id, subreddit_id };
  }

  public Link(RedditData data) {
    id = truncateId(data.link_id);
    subreddit_id = data.subreddit_id.split("_")[1];
  }
}
