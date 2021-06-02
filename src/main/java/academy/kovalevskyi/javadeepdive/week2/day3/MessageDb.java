package academy.kovalevskyi.javadeepdive.week2.day3;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week0.day3.InsertRequest;
import academy.kovalevskyi.javadeepdive.week0.day3.RequestException;
import academy.kovalevskyi.javadeepdive.week0.day3.SelectRequest;
import java.lang.reflect.Field;
import java.util.Arrays;

public class MessageDb {
  private Csv csv;

  public MessageDb(final Csv csv) {
    this.csv = csv;
  }

  public Message[] getAll() {
    try {
      SelectRequest selectRequest = new SelectRequest.Builder()
              .from(this.csv)
              .select(this.csv.header())
              .build();
      String[][] records = selectRequest.request();
      return Arrays
              .stream(records)
              .map(record -> this.adaptRecordToMessage(record))
              .toArray(Message[]::new);
    } catch (RequestException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean create(final Message message) {
    System.out.println(message);
    InsertRequest insertRequest = new InsertRequest.Builder()
            .to(this.csv)
            .insert(this.adaptMessageToRecord(message))
            .build();
    try {
      this.csv = insertRequest.request();
      return true;
    } catch (RequestException e) {
      e.printStackTrace();
      return false;
    }
  }

  private String[] adaptMessageToRecord(final Message message) {
    try {
      String[] record = new String[this.csv.header().length];
      for (int i = 0; i < this.csv.header().length; i++) {
        String columnName = this.csv.header()[i];
        Field field = message.getClass().getDeclaredField(columnName);
        field.setAccessible(true);
        record[i] = field.get(message).toString();
      }

      return record;
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
      return null;
    }
  }

  private Message adaptRecordToMessage(final String[] record) {
    try {
      Message message = new Message();
      for (int i = 0; i < this.csv.header().length; i++) {
        String columnName = this.csv.header()[i];
        Field field = message.getClass().getDeclaredField(columnName);
        field.setAccessible(true);
        field.set(message, record[i]);
      }

      return message;
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
      return null;
    }
  }
}
