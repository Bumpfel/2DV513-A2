package models;

public class Subreddit extends AbstractData {
  private String subreddit;

  public String getName() { return subreddit; }

  public Subreddit() {}

  public String getInsertCols() {
    return "id, name";
  }

  public String[] getInsertValues() {
    return new String[] { id, subreddit };
  }

  public Subreddit(RedditData data) {
    subreddit = data.subreddit;
    id = truncateId(data.subreddit_id);
  }
}
