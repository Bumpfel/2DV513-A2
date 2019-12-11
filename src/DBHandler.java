import java.io.File;
import java.sql.*;

public class DBHandler {
  private Connection conn;

  public static void main(String[] args) {
    // new JsonReader(new File("data/big/RC_2007-10")); // 85 MB
    // mapJson(new File("data/big/RC_2011-07")); // 5.62 GB
    DBHandler db = new DBHandler();
    db.connect();
    ResultSet res = db.doExampleQuery();
    db.printResults(res);
  }


  void connect() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_company","root","root");

    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  ResultSet doExampleQuery () {
    ResultSet rs = null;
    try {
      Statement stmt = conn.createStatement();
      rs = stmt.executeQuery("SELECT * FROM employees");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  void printResults(ResultSet res) {
    try {
      while(res.next()) {
        System.out.println(res.getString("id") + " " + res.getString("firstname") + " " + res.getString("lastname"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
