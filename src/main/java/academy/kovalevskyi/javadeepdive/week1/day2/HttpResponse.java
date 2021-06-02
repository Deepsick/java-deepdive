package academy.kovalevskyi.javadeepdive.week1.day2;

public record HttpResponse(ResponseStatus status, ContentType contentType, String
        body, HttpVersion httpVersion) {
  public static final  HttpResponse ERROR_404 =
          new Builder().status(ResponseStatus.ERROR_404).build();
  public static final  HttpResponse OK_200 =
          new Builder().status(ResponseStatus.OK).build();
  public static final HttpResponse ERROR_500 =
          new Builder().status(ResponseStatus.ERROR_500).build();

  public static class Builder {
    private ResponseStatus status = ResponseStatus.OK;
    private ContentType contentType = ContentType.TEXT_HTML;
    private String body;
    private HttpVersion version = HttpVersion.HTTP_1_1;

    public Builder status(final ResponseStatus status) {
      this.status = status;
      return this;
    }

    public Builder contentType(final ContentType contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder body(final String body) {
      this.body = body;
      return this;
    }

    public Builder httpVersion(final HttpVersion version) {
      this.version = version;
      return this;
    }

    public HttpResponse build() {
      return new HttpResponse(this.status, this.contentType, this.body, this.version);
    }
  }

  public enum ResponseStatus {
    OK(200, "OK"),
    ERROR_404(404, "not found"),
    ERROR_500(500, "server error");

    private final String text;
    private final int status;
    ResponseStatus(final int status, final String text) {
      this.text = text;
      this.status = status;
    }

    public int getStatus() {
      return this.status;
    }

    public String getText() {
      return this.text;
    }
  }

  @Override
  public String toString() {
    return "HttpResponse{"
            + "status=" + status
            + ", contentType=" + contentType
            + ", body='" + body + '\''
            + ", httpVersion=" + httpVersion
            + '}';
  }
}
