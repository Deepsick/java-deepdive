package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractRequest<T> {
  protected final Csv csv;
  private Object mutex = new Object();

  protected AbstractRequest(final Csv csv) {
    this.csv = csv;
  }

  public T request() throws RequestException {
    synchronized (mutex) {
      return this.execute();
    }
  }

  protected int indexOfColumn(String[] header, String columnName) {
    return Arrays.asList(header).indexOf(columnName);
  }

  protected ArrayList<Integer> findRowIndexes(Csv csv, final Selector selector) {
    String columnName = selector.fieldName();
    String value = selector.value();

    int columnIndex = this.indexOfColumn(csv.header(), columnName);
    if (columnIndex == -1) {
      return null;
    }

    ArrayList<Integer> indexes = new ArrayList<>();

    for (int i = 0; i < csv.values().length; i++) {
      if (csv.values()[i][columnIndex].equals(value)) {
        indexes.add(i);
      }
    }

    return indexes;
  }

  protected abstract T execute() throws RequestException;
}
