package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.Objects;
import java.util.stream.IntStream;

public class DeleteRequest extends AbstractRequest<Csv> {
  private final Selector where;

  private DeleteRequest(final Csv csv, final Selector where) {
    super(csv);
    this.where = where;
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

    String[] header = csv.header();
    String[][] oldValues = csv.values();
    String[][] newValues = IntStream
            .range(0, oldValues.length)
            .filter(i -> !indexes.contains(i))
            .mapToObj(i -> oldValues[i])
            .toArray(String[][]::new);

    return new Csv.Builder().header(header).values(newValues).build();
  }

  public static class Builder {
    private Selector where;
    private Csv csv;

    public Builder where(final Selector where) {
      this.where = where;
      return  this;
    }

    public Builder from(final Csv csv) {
      this.csv = csv;
      return  this;
    }

    public DeleteRequest build() {
      return new DeleteRequest(this.csv, this.where);
    }
  }
}
