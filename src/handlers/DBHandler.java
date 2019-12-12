package handlers;

import java.sql.*;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DBHandler {
  private Connection conn;

  public void connect(String database) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, "root", "root"); // running on localhost only, so root user is fine
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public ResultSet execQuery(String query) {
    ResultSet rs = null;
    try {
      Statement stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  public boolean exec(String query) { // TODO old. remove?
    try {
      Statement stmt = conn.createStatement();
      stmt.execute(query);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * Inserts data using prepared statement to escape special characters and protect against accidental or intended sql injections
   * @param table Which table to insert data to
   * @param cols which columns to set, separated by comma e.g.: "id, parent_id, name"
   * @param values values of the columns. Important! must be ordered in the same wy as the cols
   * @return
   */
  public boolean insert(String table, String[] cols, String[] values) { // TODO reaaallly slow. try suggestion batch inserts
    try {
      // error checking
      if(cols.length == 0) {
        throw new IllegalArgumentException("cols is empty");
      } else if(values == null) {
        throw new IllegalArgumentException("values is null");
      } else if(cols.length != values.length) {
        throw new IllegalArgumentException("# of cols and values must be equal");
      }

      // build a comma-separated string of the column
      // build a comma-separated string "?, ?, ?, ..." with a question mark for each column
      // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
      StringBuilder colsBuilder = new StringBuilder();
      StringBuilder parameterBuilder = new StringBuilder();
      for(int i = 0; i < cols.length; i++) {
        colsBuilder.append(cols[i] + ", ");
        parameterBuilder.append("?, ");
      }
      String colsString = colsBuilder.substring(0, colsBuilder.length() - 2);
      String parameterString = parameterBuilder.substring(0, parameterBuilder.length() - 2);

      // prepare statement
      PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + "(" + colsString + ") VALUES (" + parameterString + ")");
      for(int i = 1; i <= values.length; i++) {
        stmt.setString(i, values[i - 1]);
      }
      // System.out.println(stmt.toString());

      // execute statement
      stmt.executeUpdate();
      return true;
    } catch(MySQLIntegrityConstraintViolationException e) {
      // duplicate entry
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }


  // TODO unused. initial testing

  void runShowcaseCode() {
    DBHandler db = new DBHandler();
    db.connect("my_company");
    ResultSet res = db.execQuery("SELECT * FROM employees");
    db.printResults(res, "id", "firstname", "lastname");
  }

  void printResults(ResultSet res, String...fields) { 
    try {
      StringBuilder str = new StringBuilder();
      while(res.next()) {
        for(String field : fields) {
          str.append(res.getString(field) + " ");
        }
        System.out.println(str);
        str.delete(0, str.length());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
