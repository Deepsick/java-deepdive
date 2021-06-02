package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectRequest extends AbstractRequest<String[][]> {
  private final Selector where;
  private final String[] columns;

  private SelectRequest(final Csv csv, final Selector where, final String[] columns) {
    super(csv);
    this.where = where;
    this.columns = columns;
  }

  private String[] selectFields(final String[] row, final int[] columnIndexes) {
    String[] newRow = new String[columnIndexes.length];
    int index = 0;
    for (int columnIndex : columnIndexes) {
      newRow[index] = row[columnIndex];
      index++;
    }

    return newRow;
  }

  @Override
  protected String[][] execute() throws RequestException {
    if (!Objects.nonNull(this.csv)) {
      throw new RequestException();
    }

    int[] columnIndexes = IntStream
            .range(0, this.columns.length)
            .map(i -> this.indexOfColumn(this.csv.header(), this.columns[i]))
            .filter(i -> i != -1)
            .toArray();

    var indexes = this.where != null
            ? this.findRowIndexes(this.csv, this.where)
            : IntStream.range(0, this.csv.values().length).boxed().collect(Collectors.toList());
    if (indexes == null) {
      return null;
    }

    String[][] values = IntStream
            .range(0, this.csv.values().length)
            .filter(i -> indexes.contains(i))
            .mapToObj(i -> this.csv.values()[i])
            .map(row -> this.selectFields(row, columnIndexes))
            .toArray(String[][]::new);

    return values;
  }

  public static class Builder {
    private Csv csv;
    private String[] columns;
    private Selector selector = null;

    public Builder where(final Selector selector) {
      this.selector = selector;
      return this;
    }

    public Builder select(final String[] columns) {
      this.columns = columns;
      return this;
    }

    public Builder from(final Csv csv) {
      this.csv = csv;
      return this;
    }

    public SelectRequest build() {
      return new SelectRequest(this.csv, this.selector, this.columns);
    }
  }
}
