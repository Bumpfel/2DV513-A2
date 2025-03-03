package handlers;

import java.sql.*;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import models.AbstractData;

public class DBHandler {
  private Connection conn;
  private int attemptedInsertions;
  private long totalTimeTaken;

  public long getTotalTimeTaken() { return totalTimeTaken; }

  public String getDBHandlerStatus() {
    return "==========  DBHandler  ==========\n" +
    attemptedInsertions + " attempted insertions (including duplicates) in " + TimeFormatter.format(totalTimeTaken);
  }

  /**
   * Using rewriteBatchedStatements=true for performance
   * @param database
   */
  public void connect(String database) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?rewriteBatchedStatements=true", "root", ""); // running on localhost only, so root user is fine
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public boolean exec(String query) {
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
   * 
   * @param table Which table to insert data to
   * @param data An iterable of subreddit, links, or comments
   * @param debugPrints if true, prints debug data to console
   */
  public void batchInsertion(String table, Iterable<? extends AbstractData> data) {
    if(!data.iterator().hasNext()) {
      return;
    }

    try {
      // builds a comma-separated string "?, ?, ?, ..." with a parameter(?) for each column
      // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
      
      String colsString = data.iterator().next().getInsertCols();
      
      StringBuilder parameterBuilder = new StringBuilder();
      for(int i = 0; i < colsString.split(",").length; i++) {
        parameterBuilder.append("?, ");
      }
      String parameterString = parameterBuilder.substring(0, parameterBuilder.length() - 2);
      
      PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO " + table + "(" + colsString + ") VALUES (" + parameterString + ")");
      long timestamp = System.currentTimeMillis();

      for(AbstractData dataObject : data) {
        String[] insertValues = dataObject.getInsertValues();
        for(int i = 1; i <= insertValues.length; i++) {
          // replaces each parameter(?) with data values
          stmt.setString(i, insertValues[i - 1]);
        }
        stmt.addBatch();
      }
      // execute batch and count insertions
      attemptedInsertions += stmt.executeBatch().length;

      // done
      totalTimeTaken += System.currentTimeMillis() - timestamp;
    } catch(MySQLIntegrityConstraintViolationException | BatchUpdateException e) {
      // duplicate entry
      System.err.println("Error inserting into " + table + ". " + e.getMessage());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
