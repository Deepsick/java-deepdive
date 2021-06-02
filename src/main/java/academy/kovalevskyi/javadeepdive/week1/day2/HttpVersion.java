package academy.kovalevskyi.javadeepdive.week1.day2;

public enum HttpVersion {
  HTTP_1_1("HTTP/1.1");

  private final String value;
  HttpVersion(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static HttpVersion getByValue(final String value) {
    for (HttpVersion version : HttpVersion.values()) {
      if (version.getValue().equals(value)) {
        return version;
      }
    }
    return null;
  }
}
