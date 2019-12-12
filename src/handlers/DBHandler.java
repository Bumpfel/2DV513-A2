package handlers;

import java.sql.*;
import java.util.Collection;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import models.AbstractData;

public class DBHandler {
  private Connection conn;

  /**
   * Using rewriteBatchedStatements=true for performance
   * @param database
   */
  public void connect(String database) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?rewriteBatchedStatements=true", "root", "root"); // running on localhost only, so root user is fine
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
  public boolean insert(String table, String[] cols, String[] values) { // slow
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

  /**
   * 
   * @param table Which table to insert data to
   * @param data An iterable of subreddit, links, or comments
   * @param debugPrints if true, prints debug data to console
   */
  public void batchInsertion(String table, Collection<? extends AbstractData> data, boolean debugPrints) {
    if(debugPrints) System.out.println("====  Batch Insertions into " + table + " ====");
    try {
      // builds a comma-separated string "?, ?, ?, ..." with a parameter(?) for each column
      // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
      
      String colsString = data.iterator().next().getInsertCols();
      
      StringBuilder parameterBuilder = new StringBuilder();
      for(int i = 0; i < colsString.split(",").length; i++) {
        parameterBuilder.append("?, ");
      }
      String parameterString = parameterBuilder.substring(0, parameterBuilder.length() - 2);
      
      PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + "(" + colsString + ") VALUES (" + parameterString + ")");
      
      long timestamp = System.currentTimeMillis();
      
      for(AbstractData dataObject : data) {
        String[] insertValues = dataObject.getInsertValues();
        for(int i = 1; i <= insertValues.length; i++) {
          // replaces each parameter(?) with data values
          stmt.setString(i, insertValues[i - 1]);
        }
        stmt.addBatch();
      }
      double seconds = (double) (System.currentTimeMillis() - timestamp) / 1000;
      if(debugPrints) System.out.println("Statement prepared in " + seconds + " s");
      
      // execute batch insertions
      int successfulInsertions = 0;
      int[] affectedRows = stmt.executeBatch();
      for(int i : affectedRows) {
          successfulInsertions += i;
      }
      if(debugPrints) {
        seconds = (double) (System.currentTimeMillis() - timestamp) / 1000;
        System.out.println(successfulInsertions + " successful batch insertions of " + data.size() + " in total done in " + seconds + " s");
        System.out.println("============================");
      }
    } catch(MySQLIntegrityConstraintViolationException | BatchUpdateException e) {
      System.err.println(e.getMessage());
      // duplicate entry
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
