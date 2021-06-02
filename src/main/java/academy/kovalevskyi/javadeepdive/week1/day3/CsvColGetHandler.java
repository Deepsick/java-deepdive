package academy.kovalevskyi.javadeepdive.week1.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week0.day3.RequestException;
import academy.kovalevskyi.javadeepdive.week0.day3.SelectRequest;
import academy.kovalevskyi.javadeepdive.week1.day2.ContentType;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpMethod;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpRequest;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpRequestsHandler;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpResponse;
import java.util.Arrays;

public class CsvColGetHandler implements HttpRequestsHandler {
  private final String path;
  private final String colName;
  private final Csv csv;

  public CsvColGetHandler(final Csv csv, final String colName, final String path) {
    this.path = path;
    this.colName = colName;
    this.csv = csv;
  }

  @Override
  public String path() {
    return this.path;
  }

  @Override
  public HttpMethod method() {
    return HttpMethod.GET;
  }

  private String stringifyArray(final String[] array) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < array.length; i++) {
      builder.append("\"");
      builder.append(array[i]);
      builder.append("\"");
      if (i != array.length - 1) {
        builder.append(",");
      }
    }
    builder.append("]");

    return builder.toString();
  }

  @Override
  public HttpResponse process(final HttpRequest request) {
    SelectRequest selectRequest = new SelectRequest.Builder()
            .from(this.csv)
            .select(new String[]{this.colName})
            .build();

    String[] results = new String[0];
    HttpResponse.ResponseStatus status = HttpResponse.ResponseStatus.OK;
    try {
      results = Arrays
              .stream(selectRequest.request())
              .map(row -> row[0])
              .toArray(String[]::new);
      if (results.length == 0) {
        status = HttpResponse.ResponseStatus.ERROR_404;
      }
    } catch (RequestException e) {
      e.printStackTrace();
      status = HttpResponse.ResponseStatus.ERROR_500;
    }

    return new HttpResponse.Builder()
            .body(this.stringifyArray(results))
            .status(status)
            .contentType(ContentType.APPLICATION_JSON)
            .build();
  }
}
