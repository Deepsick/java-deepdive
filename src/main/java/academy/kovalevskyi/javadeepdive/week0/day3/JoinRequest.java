package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class JoinRequest extends AbstractRequest<Csv> {
  private final Csv right;
  private final String by;

  private JoinRequest(final Csv left, final Csv right, final String by) {
    super(left);
    this.right = right;
    this.by = by;
  }

  private String[] mergeRows(final String[] left, final String[] right, final int byRightIndex) {
    String[] row = new String[left.length + right.length - 1];
    int index = 0;
    for (String columnValue : left) {
      row[index] = columnValue;
      index++;
    }
    for (int i = 0; i < right.length; i++) {
      if (i == byRightIndex) {
        continue;
      }
      row[index] = right[i];
      index++;
    }

    return row;
  }

  private String[][] sortRows(final int columnIndex, final String[][] rows) {
    String[][] copiedRows = rows.clone();
    Arrays.sort(copiedRows, Comparator.comparing((String[] row) -> row[columnIndex]));
    return copiedRows;
  }

  private String[][] joinRows(final String[][] left, final String[][] right) {
    int leftColumnIndex = this.indexOfColumn(this.csv.header(), this.by);
    int rightColumnIndex = this.indexOfColumn(this.right.header(), this.by);

    String[][] newValues = new String[this.csv.values().length][];
    String[][] sortedLeft = this.sortRows(leftColumnIndex, left);
    String[][] sortedRight = this.sortRows(rightColumnIndex, right);

    for (int i = 0; i < this.csv.values().length; i++) {
      String[] leftRow = sortedLeft[i];
      String[] rightRow = sortedRight[i];
      String[] newRow = this.mergeRows(leftRow, rightRow, rightColumnIndex);

      newValues[i] = newRow;
    }

    return newValues;
  }

  @Override
  protected Csv execute() throws RequestException {
    if (!Objects.nonNull(this.csv)) {
      return this.right;
    }
    if (!Objects.nonNull(this.right)) {
      return this.csv.clone();
    }
    if (!this.csv.withHeader() || !this.right.withHeader()) {
      throw new RequestException();
    }
    if (this.csv.values().length != this.right.values().length) {
      throw new RequestException();
    }

    int rightByColumnIndex = this.indexOfColumn(this.right.header(), this.by);
    String[] newHeader = this.mergeRows(this.csv.header(), this.right.header(), rightByColumnIndex);
    String[][] newValues = this.joinRows(this.csv.values(), this.right.values());

    return new Csv(newHeader, newValues);
  }

  public static class Builder {
    private Csv left;
    private Csv right;
    private String by;

    public Builder from(final Csv left) {
      this.left = left;
      return this;
    }

    public Builder on(final Csv right) {
      this.right = right;
      return this;
    }

    public Builder by(final String by) {
      this.by = by;
      return this;
    }

    public JoinRequest build() {
      return new JoinRequest(this.left, this.right, this.by);
    }
  }
}
