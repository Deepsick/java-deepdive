package academy.kovalevskyi.javadeepdive.week0.day2;

import java.util.Arrays;

public record Csv(String[] header, String[][] values) {
  public static class Builder {
    private String[] header;
    private String[][] values;

    public Builder header(final String[] header) {
      this.header = header;
      return this;
    }

    public Builder values(final String[][] values) {
      this.values = values;
      return this;
    }

    public Csv build() {
      return new Csv(this.header, this.values);
    }
  }

  public boolean withHeader() {
    return header != null && header.length > 0;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return  true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    Csv that = (Csv) o;
    if (!Arrays.equals(this.header(), that.header())
            || !Arrays.equals(this.header(), that.header())) {
      return false;
    }

    return true;
  }

  private String stringifyRow(final String[] row) {
    return Arrays.toString(row);
  }

  public Csv clone() {
    return new Csv(this.header().clone(), this.values().clone());
  }

  @Override
  public String toString() {
    String header = "Header: " + Arrays.toString(this.header());
    StringBuilder builder = new StringBuilder(header);
    builder.append("\n");
    for (String[] row : this.values()) {
      builder.append(this.stringifyRow(row));
      builder.append("\n");
    }
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.header()) + Arrays.deepHashCode(this.values());
  }
}
