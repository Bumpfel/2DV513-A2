package models;

public class Comment extends Data {

  private String parent_id, link_id, body, score, created_utc;
  
  public Comment() {}

  public Comment set(RedditData data) {
    id = data.id;
    parent_id = data.parent_id;
    link_id = data.link_id;
    body = data.body;
    score = data.score;
    created_utc = data.created_utc;

    return this;
  }

}
