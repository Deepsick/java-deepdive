package academy.kovalevskyi.javadeepdive.week0.day2;

import academy.kovalevskyi.javadeepdive.week0.day0.StdBufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvHelper {
  public static Csv parseFile(final Reader reader) throws FileNotFoundException {
    return CsvHelper.parseFile(reader, false, ',');
  }

  public static Csv parseFile(
          final Reader reader,
          final boolean withHeader,
          final char delimiter
  ) throws  FileNotFoundException {

    StdBufferedReader bufferedReader = new StdBufferedReader(reader);
    String[] header = null;
    ArrayList<String[]> values = new ArrayList();

    try {
      if (withHeader) {
        header = CsvHelper.parseLine(bufferedReader.readLine(), delimiter);
      }

      while (bufferedReader.hasNext()) {
        String[] line = CsvHelper.parseLine(bufferedReader.readLine(), delimiter);

        if (line != null) {
          values.add(line);
        }
      }
      bufferedReader.close();

      return new Csv.Builder().header(header).values(values.toArray(new String[0][0])).build();
    } catch (IOException e) {
      throw  new FileNotFoundException();
    }
  }

  public static void writeCsv(final Writer writer,
                              final Csv csv,
                              final char delimiter
  ) throws  IOException {
    if (csv.withHeader()) {
      String line = String.join(Character.toString(delimiter), csv.header()) + "\n";
      writer.write(line);
    }

    for (String[] value : csv.values()) {
      String line = String.join(Character.toString(delimiter), value) + "\n";
      writer.write(line);
    }

    writer.close();
  }

  private static String[] parseLine(final char[] readerLine, final char delimiter) {
    String line = new String(readerLine);
    return line.length() > 0
            ? Arrays
              .stream(line.split(String.valueOf(delimiter)))
              .map(String::trim)
              .toArray(String[]::new)
            : null;
  }
}
