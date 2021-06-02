package academy.kovalevskyi.javadeepdive.week2.day3;


import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week2.day1.Controller;
import academy.kovalevskyi.javadeepdive.week2.day1.Get;
import academy.kovalevskyi.javadeepdive.week2.day1.Path;
import academy.kovalevskyi.javadeepdive.week2.day1.Post;
import java.util.Arrays;

@Controller
public class Messages {
  private final MessageDb db;

  public Messages() {
    this.db = new MessageDb(new Csv(Messages.header(), new String[0][0]));
  }

  private static String[] header() {
    return Arrays
            .stream(Message.class.getDeclaredFields())
            .map(field -> field.getName())
            .toArray(String[]::new);
  }

  @Get
  @Path("/messages")
  public Message[] getMessages() {
    return this.db.getAll();
  }

  @Post
  @Path("/messages")
  public String  sendMessage(final Message message) {
    return this.db.create(message) ? "Success" : "Failure";
  }
}
