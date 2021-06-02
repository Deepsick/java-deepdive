package academy.kovalevskyi.javadeepdive.week2.day2;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week0.day3.InsertRequest;
import academy.kovalevskyi.javadeepdive.week0.day3.RequestException;
import academy.kovalevskyi.javadeepdive.week0.day3.SelectRequest;
import academy.kovalevskyi.javadeepdive.week0.day3.Selector;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class UserDb {
  private Csv csv;

  public UserDb(final Csv csv) {
    this.csv = csv;
  }

  public void addUser(User user) throws RequestException {
    InsertRequest insertRequest = new InsertRequest.Builder()
            .to(this.csv)
            .insert(this.adaptUserToRecord(user))
            .build();
    this.csv = insertRequest.request();
  }

  public String[] getUsersMails() throws RequestException {
    SelectRequest selectRequest = new SelectRequest.Builder()
            .from(this.csv)
            .select(new String[]{"mail"})
            .build();
    return Arrays
            .stream(selectRequest.request())
            .map(row -> row[0])
            .toArray(String[]::new);
  }

  public Optional<User> first() throws RequestException {
    return Optional.ofNullable(this.adaptRecordToUser(this.csv.values()[0]));
  }

  public Optional<User> getUser(String mail) throws RequestException {
    Selector selector = new Selector("mail", mail);
    SelectRequest selectRequest = new SelectRequest.Builder()
            .from(this.csv)
            .where(selector)
            .select(this.csv.header())
            .build();

    String[] record = selectRequest.request()[0];
    return Optional.ofNullable(this.adaptRecordToUser(record));
  }

  private User adaptRecordToUser(final String[] record) {
    try {
      User user = new User();
      for (int i = 0; i < this.csv.header().length; i++) {
        String columnName = this.csv.header()[i];
        Field field = user.getClass().getDeclaredField(columnName);
        field.setAccessible(true);
        field.set(user, record[i]);
      }

      return user;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return null;
  }

  private String[] adaptUserToRecord(final User user) {
    try {
      String[] record = new String[this.csv.header().length];
      for (int i = 0; i < this.csv.header().length; i++) {
        String columnName = this.csv.header()[i];
        Field field = user.getClass().getDeclaredField(columnName);
        field.setAccessible(true);
        record[i] = field.get(user).toString();
      }

      return record;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return null;
  }
}
