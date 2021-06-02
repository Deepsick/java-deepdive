package academy.kovalevskyi.javadeepdive.week1.day2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHttpServerWithPath extends Thread {
  private ServerSocket serverSocket;
  private final int port = 8080;
  private final ExecutorService executorService;
  private CopyOnWriteArrayList<HttpRequestsHandler> handlers =
          new CopyOnWriteArrayList<HttpRequestsHandler>();

  public ConcurrentHttpServerWithPath() {
    this.executorService = Executors.newCachedThreadPool();
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addHandler(final HttpRequestsHandler handler) {
    handlers.add(handler);
  }

  private HttpRequestsHandler findHandler(final HttpRequest request) {
    for (HttpRequestsHandler handler : handlers) {
      if (handler.method().equals(request.httpMethod()) && handler.path().equals(request.path())) {
        return handler;
      }
    }

    return null;
  }

  private void processRequest(final academy
          .kovalevskyi.javadeepdive.week1.day0.HttpRequestsHandler handler) {
    try {
      HttpRequest request = handler.getRequest();
      HttpRequestsHandler pathHandler = this.findHandler(request);
      if (pathHandler == null) {
        HttpResponse response = HttpResponse.ERROR_404;
        handler.sendResponse(response);
        return;
      }

      System.out.println(request);
      HttpResponse response = pathHandler.process(request);
      System.out.println(response);
      handler.sendResponse(response);
    } catch (Exception e) {
      handler.sendResponse(HttpResponse.ERROR_500);
    }
  }

  public void run() {
    while (this.isLive()) {
      try {
        academy.kovalevskyi.javadeepdive.week1.day0.HttpRequestsHandler handler =
                new academy.kovalevskyi.javadeepdive.week1.day0.HttpRequestsHandler(
                        this.serverSocket.accept());
        executorService.execute(() -> this.processRequest(handler));

      } catch (IOException e) {
        if (this.isLive()) {
          e.printStackTrace();
        }
      }
    }
  }

  public void stopServer() {
    try {
      this.executorService.shutdown();
      this.serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isLive() {
    return !this.serverSocket.isClosed();
  }

  public static void main(String... args) {
    var serverThread = new ConcurrentHttpServerWithPath();
    serverThread.addHandler(new HttpRequestsHandler() {
      @Override
      public String path() {
        return "/hi";
      }

      @Override
      public HttpMethod method() {
        return HttpMethod.GET;
      }

      @Override
      public HttpResponse process(HttpRequest request) {
        return new HttpResponse.Builder().body("<h1>HI</h1>").build();
      }
    });
    serverThread.start();
    Scanner scanner = new Scanner(System.in);

    System.out.println("Input 'stop' to stop the server: ");
    String line = scanner.nextLine();
    while (!line.equals("stop")) {
      line = scanner.nextLine();
    }

    serverThread.stopServer();
    scanner.close();
  }
}
