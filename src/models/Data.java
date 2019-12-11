package models;

public abstract class Data {

  protected String id;

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    try {
      Data other = (Data) o;
      return id.equals(other.id);
    } catch (ClassCastException e) {
      return false;
    }
  }
}