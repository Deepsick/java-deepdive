package academy.kovalevskyi.javadeepdive.week1.day0;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class HttpServer implements Runnable {
  private ServerSocket serverSocket;
  private final int port = 8080;

  public HttpServer() {
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public void stop() {
    try {
      this.serverSocket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void run() {
    try {
      while (this.isLive()) {
        HttpRequestsHandler requestHandler = new HttpRequestsHandler(this.serverSocket.accept());
        requestHandler.executeRequest();
      }
    } catch (IOException e) {
      if (this.isLive()) {
        System.out.println(e.getMessage());
      }
    }
  }

  public boolean isLive() {
    return !this.serverSocket.isClosed();
  }

  public static void main(String[] args) {
    HttpServer server = new HttpServer();
    new Thread(server).start();

    Scanner scanner = new Scanner(System.in);
    System.out.println("Input 'stop' to stop the server: ");
    while (true) {
      String line = scanner.nextLine();
      if (line.equals("stop")) {
        server.stop();
        break;
      }
    }
    scanner.close();
  }
}