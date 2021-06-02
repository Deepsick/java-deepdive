package academy.kovalevskyi.javadeepdive.week2.day2;

import academy.kovalevskyi.javadeepdive.week0.day2.Csv;
import academy.kovalevskyi.javadeepdive.week0.day3.RequestException;
import academy.kovalevskyi.javadeepdive.week2.day1.Controller;
import academy.kovalevskyi.javadeepdive.week2.day1.Get;
import academy.kovalevskyi.javadeepdive.week2.day1.Path;
import academy.kovalevskyi.javadeepdive.week2.day1.Post;
import java.util.Arrays;

@Controller
public class Users {
  private final UserDb db;

  public Users() {
    this.db = new UserDb(new Csv(this.header(), new String[0][0]));
  }

  private String[] header() {
    return Arrays
            .stream(User.class.getDeclaredFields())
            .map(field -> field.getName())
            .toArray(String[]::new);
  }

  @Get
  @Path("/users")
  public String[] users() {
    try {
      return db.getUsersMails();
    } catch (RequestException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Get
  @Path("/first")
  public User firstUser() {
    try {
      return db.first().get();
    } catch (RequestException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Post
  @Path("/users")
  public void addUser(final User user) {
    try {
      db.addUser(user);
    } catch (RequestException e) {
      e.printStackTrace();
    }
  }
}
