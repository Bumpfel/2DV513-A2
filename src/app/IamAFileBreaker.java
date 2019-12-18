package app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

class IamAFileBreaker {

  public static void main(String[] args) {
    int batch = 1000000;

    File file = new File("data/big/RC_2011-07"); // 5.62 GB

    try(Scanner scanner = new Scanner(file)) {
      scanner.useDelimiter("\n");

      StringBuilder builder = new StringBuilder();
      for(int i = 0; scanner.hasNext() && i < batch; i++) {
        builder.append(scanner.next());
      }
      
      PrintWriter writer = new PrintWriter(new File("data/test"));
      writer.write(builder.toString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}