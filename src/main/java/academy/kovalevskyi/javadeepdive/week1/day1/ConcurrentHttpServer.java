package academy.kovalevskyi.javadeepdive.week1.day1;

import academy.kovalevskyi.javadeepdive.week1.day0.HttpRequestsHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHttpServer implements Runnable {
  private ServerSocket serverSocket;
  private final int port = 8080;
  private final int threadCount = 10;
  private final ExecutorService executorService;

  public ConcurrentHttpServer() {
    this.executorService = Executors.newFixedThreadPool(this.threadCount);
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String... args) {
    ConcurrentHttpServer server = new ConcurrentHttpServer();
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

  @Override
  public void run() {
    try {
      while (this.isLive()) {
        HttpRequestsHandler handler = new HttpRequestsHandler(this.serverSocket.accept());
        this.executorService.execute(() -> handler.executeRequest());
      }
    } catch (IOException e) {
      // Hack
      if (this.isLive()) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    try {
      this.serverSocket.close();
      this.executorService.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isLive() {
    return !this.serverSocket.isClosed();
  }
}
