package academy.kovalevskyi.javadeepdive.week1.day2;

public enum ContentType {
  TEXT_HTML("text/html"),
  TEXT_PLAIN("text/plain"),
  APPLICATION_JSON("application/json");

  private String value;
  ContentType(final String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static ContentType getByValue(final String value) {
    if (value.equals(TEXT_PLAIN.getValue())) {
      return TEXT_HTML;
    }

    for (ContentType type : ContentType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return null;
  }
}
