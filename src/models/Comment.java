package models;

public class Comment extends AbstractData {
  
  private String parent_id, link_id, body, score, created_utc;
  public String getParentId() { return parent_id; }
  public String getLinkId() { return link_id; }
  public String getBody() { return body; }
  public String getScore() { return score; }
  public String getCreatedUTC() { return created_utc; }

  public String getInsertCols() {
    return "id, parent_id, link_id, body, score, created_utc"; // TODO could be static. could place in abstract class
  }

  public String[] getInsertValues() {
    return new String[] { id, parent_id, link_id, body, score, created_utc };
  }

  public Comment() {}

  public Comment(RedditData data) {
    id = data.id;
    parent_id = truncateId(data.parent_id);
    link_id = truncateId(data.link_id);
    body = data.body;
    score = data.score;
    created_utc = data.created_utc;
  }

}
