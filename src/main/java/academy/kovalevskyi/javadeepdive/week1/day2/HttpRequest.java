package academy.kovalevskyi.javadeepdive.week1.day2;

import java.util.Optional;

public record HttpRequest(String path,
                          HttpMethod httpMethod,
                          Optional<String> body,
                          ContentType contentType,
                          HttpVersion httpVersion
) {

  public static class Builder {
    private String path = "/";
    private HttpMethod method = HttpMethod.GET;
    private Optional<String> body;
    private   ContentType contentType = ContentType.TEXT_HTML;
    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;

    public Builder path(final String path) {
      this.path = path;
      return this;
    }

    public Builder method(final HttpMethod method) {
      this.method = method;
      return this;
    }

    public Builder body(final String body) {
      this.body = Optional.ofNullable(body);
      return  this;
    }

    public Builder contentType(final ContentType contentType) {
      this.contentType = contentType;
      return  this;
    }

    public Builder httpVersion(final HttpVersion httpVersion) {
      this.httpVersion = httpVersion;
      return  this;
    }

    public HttpRequest build() {
      return new HttpRequest(this.path, this.method, this.body, this.contentType, this.httpVersion);
    }
  }

  @Override
  public String toString() {
    return "HttpRequest{"
            + "path='" + path + '\''
            + ", httpMethod=" + httpMethod
            + ", body=" + body
            + ", contentType=" + contentType
            + ", httpVersion=" + httpVersion
            + '}';
  }
}
