package models;

import handlers.TimeFormatter;

public class Comment extends AbstractData {
  
  private TimeFormatter timeFormatter = new TimeFormatter();

  private String parent_id, link_id, subreddit_id, author, body, score, created_utc;

  public String getInsertCols() {
    return "id, parent_id, link_id, subreddit_id, author, body, score, created_utc";
  }

  public String[] getInsertValues() {
    return new String[] { id, parent_id, link_id, subreddit_id, author, body, score, created_utc };
  }

  public Comment() {}

  public Comment(RedditData data) {
    id = data.id;
    parent_id = truncateId(data.parent_id);
    link_id = truncateId(data.link_id);
    subreddit_id = truncateId(data.subreddit_id);
    author = data.author;
    body = data.body;
    score = data.score;
    created_utc = timeFormatter.epochToReadable(Long.parseLong(data.created_utc));
  }

}
