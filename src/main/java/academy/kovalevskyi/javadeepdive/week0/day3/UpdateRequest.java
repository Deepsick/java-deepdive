package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.Objects;

public class UpdateRequest extends AbstractRequest<Csv> {
  private final Selector where;
  private final Selector updateTo;

  private UpdateRequest(final Csv csv,
                        final Selector where,
                        final Selector updateTo
  ) {
    super(csv);
    this.where = where;
    this.updateTo = updateTo;
  }

  private String[] updateRow(final String[] row, final Selector updateTo) {
    String[] newRow = row.clone();
    int columnIndex = this.indexOfColumn(this.csv.header(), updateTo.fieldName());
    if (columnIndex == -1) {
      return newRow;
    }
    newRow[columnIndex] = updateTo.value();

    return newRow;
  }

  @Override
  protected Csv execute() throws RequestException {
    if (!Objects.nonNull(this.csv)) {
      throw new RequestException();
    }

    var indexes = this.findRowIndexes(this.csv, this.where);
    if (indexes == null) {
      return this.csv.clone();
    }

    String[] header = this.csv.header();
    String[][] newValues = this.csv.values().clone();

    for (int index : indexes) {
      newValues[index] = this.updateRow(newValues[index], this.updateTo);
    }

    return new Csv(header, newValues);
  }

  public static class Builder {
    private Selector where;
    private Selector updateTo;
    private Csv csv;

    public Builder where(final Selector where) {
      this.where = where;
      return this;
    }

    public Builder update(final Selector updateTo) {
      this.updateTo = updateTo;
      return this;
    }

    public Builder from(final Csv csv) {
      this.csv = csv;
      return this;
    }

    public UpdateRequest build() {
      return new UpdateRequest(this.csv, this.where, this.updateTo);
    }

  }
}
