package academy.kovalevskyi.javadeepdive.week0.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week0.day2.CsvHelper;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SqlParser {
  private enum Command {
    SELECT("SELECT"), UPDATE("UPDATE"), DELETE("DELETE"), INSERT("INSERT");

    private final String name;

    Command(final String name) {
      this.name = name;
    }
  }

  private static final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

  private static Csv getCsv(final String path) {
    try {
      InputStream inputStream = SqlParser.class.getClassLoader().getResourceAsStream(path);
      Reader reader = new InputStreamReader(inputStream);
      return CsvHelper.parseFile(reader, true, ',');
    } catch (FileNotFoundException e) {
      System.out.println("There is no such file as" + path);
      System.exit(1);
      return null;
    }
  }

  private static Command parseCommand(final String command) {
    try {
      return Command.valueOf(command);
    } catch (IllegalArgumentException e) {
      System.out.println(command + "is not allowed. Please, use one of "
              + Arrays.toString(Command.values()));
      System.exit(1);
      return null;
    }
  }

  private static Selector parsePairValues(final String stringValues) {
    String[] parts = stringValues.split("=");
    return new Selector(parts[0].trim(), parts[1].trim());
  }

  private static String[] parseCommaValues(final String stringValues) {
    return Arrays
            .stream(stringValues.split(","))
            .map(String::trim)
            .toList()
            .toArray(new String[0]);
  }


  private static AbstractRequest buildRequest(final Command command,
                                              final Csv csv,
                                              final Selector where,
                                              final String stringValues
  ) {
    AbstractRequest request;
    switch (command) {
      case DELETE:
        request = new DeleteRequest.Builder().where(where).from(csv).build();
        break;
      case UPDATE:
        Selector selector = SqlParser.parsePairValues(stringValues);
        request = new UpdateRequest.Builder()
                .from(csv)
                .where(where)
                .update(selector)
                .build();
        break;
      case SELECT:
        request = new SelectRequest.Builder()
                .from(csv)
                .where(where)
                .select(SqlParser.parseCommaValues(stringValues))
                .build();
        break;
      case INSERT:
        request = new InsertRequest.Builder()
                .to(csv)
                .insert(SqlParser.parseCommaValues(stringValues))
                .build();
        break;
      default:
        throw new IllegalArgumentException("There is no such command. Available comamnds: "
                + Arrays.toString(Command.values()));
    }
    return request;
  }

  public static void run(final Command command,
                         final Csv csv,
                         final Selector where,
                         final String stringValues
  ) {
    try {
      AbstractRequest request = SqlParser.buildRequest(command, csv, where, stringValues);
      var result = request.execute();
      if (command == Command.SELECT) {
        for (String[] line : (String[][]) result) {
          System.out.println(Arrays.toString(line));
        }
        return;
      }

      Csv newCsv = (Csv) result;
      System.out.println(newCsv);
    } catch (RequestException e) {
      System.out.println("Request is broken");
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    try {
      System.out.println("Please, input your command ");
      String commandName = SqlParser.scanner.next();
      System.out.println("FROM ");
      String from = SqlParser.scanner.next();
      System.out.println("VALUES ");
      String stringValues = SqlParser.scanner.next();
      System.out.println("WHERE ");
      String stringWhere = SqlParser.scanner.next();

      Command command = SqlParser.parseCommand(commandName.toUpperCase(Locale.ROOT));
      Csv csv = SqlParser.getCsv(from);
      Selector where = stringWhere.contains("=") ? SqlParser.parsePairValues(stringWhere) : null;
      SqlParser.run(command, csv, where, stringValues);
    } catch (Exception e) {
      System.out.println("Something went wrong." + e);
      System.out.println("Message: " + e.getMessage());
      System.exit(1);
    } finally {
      SqlParser.scanner.close();
    }
  }
}
