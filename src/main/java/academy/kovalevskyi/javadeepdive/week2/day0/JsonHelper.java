package academy.kovalevskyi.javadeepdive.week2.day0;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class JsonHelper {

  private static <T> String stringifyArray(final T array) {
    StringBuilder builder = new StringBuilder("[");
    int arrayLength = Array.getLength(array);

    for (int i = 0; i < arrayLength; i++) {
      builder.append(JsonHelper.toJsonString(Array.get(array, i)));
      if (i != arrayLength - 1) {
        builder.append(",");
      }
    }
    builder.append("]");

    return builder.toString();
  }

  private static <T> String stringifyObject(final T object) {
    StringBuilder builder = new StringBuilder("{");
    var fields = object.getClass().getDeclaredFields();

    for (int i = 0; i < fields.length; i++) {
      try {
        fields[i].setAccessible(true);
        String key = JsonHelper.toJsonString(fields[i].getName());
        String value = JsonHelper.toJsonString(fields[i].get(object));

        builder.append(key);
        builder.append(":");
        builder.append(value);
        if (i != fields.length - 1) {
          builder.append(",");
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    builder.append("}");
    return builder.toString();
  }

  public static <T> String toJsonString(final T target) {
    if (target == null) {
      return "null";
    }

    if (target instanceof Number) {
      return target.toString();
    }

    if (target instanceof String) {
      return "\"" + target + "\"";
    }

    if (target.getClass().isArray()) {
      return JsonHelper.stringifyArray(target);
    }

    return JsonHelper.stringifyObject(target);
  }

  public static boolean isNumeric(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static String processStringValue(final String value) {
    return value.replace("\"", "");
  }

  private static boolean isJsonArray(final String json) {
    return json.startsWith("[") && json.endsWith("]");
  }

  private static boolean isJsonObject(final String json) {
    return json.startsWith("{") && json.endsWith("}");
  }

  private static String[] processObject(final String json) {
    return Arrays.stream(json
            .replaceAll("\\]|\\[|\\s|\\}|\\{", "")
            .trim()
            .split(","))
            .map(value -> JsonHelper.processStringValue(value))
            .toArray(String[]::new);
  }

  private static boolean isNullvalue(final String json) {
    if (json == null) {
      return true;
    }
    String trimmedJson = json.trim();
    return trimmedJson.equals("null") || trimmedJson.equals("") || trimmedJson.equals("{}");
  }

  public static <T> T fromJsonString(final String json, final Class<T> cls) throws
          IllegalAccessException, InvocationTargetException, InstantiationException {
    if (JsonHelper.isNullvalue(json)) {
      return null;
    }
    String trimmedJson = json.trim();

    if (cls.getSimpleName().equals("String")) {
      return cls.cast(trimmedJson);
    }

    if (JsonHelper.isNumeric(trimmedJson)) {
      return (T) Integer.valueOf(trimmedJson);
    }

    if (JsonHelper.isJsonArray(trimmedJson)) {
      String[] parts = JsonHelper.processObject(trimmedJson);
      var array = Array.newInstance(
              cls.getComponentType(),
              trimmedJson.length() == 2 ? 0 : parts.length
      );

      for (int i = 0; i < Array.getLength(array); i++) {
        Array.set(array, i, JsonHelper.fromJsonString(parts[i], cls.getComponentType()));
      }
      return (T) array;
    }


    if (JsonHelper.isJsonObject(trimmedJson)) {
      String[] parts = JsonHelper.processObject(trimmedJson);

      try {
        var instance = cls.getDeclaredConstructor().newInstance();

        for (int i = 0; i < parts.length; i++) {
          try {
            String[] pair = parts[i].split(":");
            String key = JsonHelper.fromJsonString(pair[0], String.class);
            Field field = cls.getDeclaredField(key);
            field.setAccessible(true);
            Object value = JsonHelper.fromJsonString(pair[1], field.getType());

            field.set(instance, value);
          } catch (NoSuchFieldException e) {
            e.printStackTrace();
          }
        }
        return instance;
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    return cls.cast(trimmedJson);
  }
}
