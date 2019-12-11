package models;

public abstract class AbstractData {

  protected String id;

  public String getId() { return id; }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    try {
      AbstractData other = (AbstractData) o;
      return id.equals(other.id);
    } catch (ClassCastException e) {
      return false;
    }
  }
}