package academy.kovalevskyi.javadeepdive.week1.day0;

import academy.kovalevskyi.javadeepdive.week0.day0.StdBufferedReader;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpMethod;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpRequest;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpResponse;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpVersion;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestsHandler {
  private final Socket socket;
  private StdBufferedReader reader;
  private OutputStream outputStream;
  private HttpRequest request;
  private String body;
  private final Map<String, String> headers = new HashMap<>();

  public HttpRequestsHandler(final Socket socket) {
    this.socket = socket;
    try {
      this.reader = new StdBufferedReader(
              new InputStreamReader(this.socket.getInputStream())
      );
      this.outputStream = this.socket.getOutputStream();
      this.request = this.parseHeaders();
      this.parseRequest();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void parseRequest() {

    try {
      String line = new String(this.reader.readLine());
      while (this.reader.hasNext()) {
        System.out.println(line);
        line = new String(this.reader.readLine());
        if (line.length() == 0) {
          continue;
        }
        if (!line.startsWith("{")) {
          String[] parts = line.split(":");
          headers.put(parts[0].trim(), parts[1].trim());
        }
      }
      System.out.println(line);
      this.body = line;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private HttpRequest parseHeaders() {
    try {
      String line = new String(this.reader.readLine());

      while (line.equals("")) {
        System.out.println(line);
        line = new String(this.reader.readLine());
      }
      String[] parts = line.split(" ");
      System.out.println(Arrays.toString(parts));
      return new HttpRequest.Builder()
              .method(HttpMethod.valueOf(parts[0]))
              .path(parts[1])
              .httpVersion(HttpVersion.getByValue(parts[2]))
              .build();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpRequest getRequest() {
    return request;
  }

  public String getBody() {
    return body;
  }

  public void sendResponse(HttpResponse response) {
    int bodyLength = (response.body() == null ? 0 : response.body().length() + 4);
    String body = response.body() != null ? response.body() + "\r\n\r\n" : "";
    try {
      String result = String.format("""
                      %s %d %s\r
                      Content-Type: %s\r
                      Content-Length: %d\r
                      \r
                      %s      
                      """,
              response.httpVersion().getValue(),
              response.status().getStatus(),
              response.status().getText(),
              response.contentType().getValue(),
              bodyLength,
              body);
      System.out.println(result);
      this.outputStream.write(result.getBytes());
      this.outputStream.flush();
      this.outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendRequest(String text) {
    try {
      this.outputStream.write(("HTTP/1.1 200 OK\r\n").getBytes());
      this.outputStream.write(("Content-Type: text/html" + "\r\n").getBytes());
      this.outputStream.write(("Content-Length: 20" + "\r\n").getBytes());
      this.outputStream.write("\r\n".getBytes());
      this.outputStream.write(text.getBytes());
      this.outputStream.write("\r\n\r\n".getBytes());
      this.outputStream.flush();
      this.outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void executeRequest() {
    try {
      while (this.reader.hasNext()) {
        String line = new String(this.reader.readLine());

        if (line.length() == 0) {
          continue;
        }
        System.out.println(line);
      }

      this.sendRequest("<b>It works!</b>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
