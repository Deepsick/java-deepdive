package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.Arrays;
import java.util.Objects;

public class InsertRequest extends AbstractRequest<Csv> {
  private final String[] row;

  private InsertRequest(final Csv csv, final String[] row) {
    super(csv);
    this.row = row;
  }

  @Override
  protected Csv execute() throws RequestException {
    if (!Objects.nonNull(this.csv)) {
      throw new RequestException();
    }

    if (this.row == null) {
      return this.csv.clone();
    }

    String[] header = this.csv.header();
    String[][] oldValues = this.csv.values();
    String[][] newValues = Arrays.copyOf(oldValues, oldValues.length + 1);
    newValues[newValues.length - 1] = this.row;

    return new Csv(header, newValues);
  }

  public static class Builder {
    private String[] row;
    private Csv csv;

    public Builder insert(final String[] row) {
      this.row = row;
      return this;
    }

    public Builder to(final Csv csv) {
      this.csv = csv;
      return this;
    }

    public InsertRequest build() {
      return new InsertRequest(this.csv, this.row);
    }
  }
}
