package app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

class IamAFileBreaker {

  public static void main(String[] args) {
    int lines = 900000;

    File file = new File("data/big/RC_2011-07"); // 5.62 GB

    try(Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("\n");

      StringBuilder builder = new StringBuilder();
      for(int i = 0; scanner.hasNext() && i < lines; i++) {
        builder.append(scanner.next() + "\n");
      }
      
      PrintWriter writer = new PrintWriter(new File("data/test"));
      writer.write(builder.toString());
      writer.close();
      System.out.println("I was a File Breaker!");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}