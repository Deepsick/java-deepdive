package academy.kovalevskyi.javadeepdive.week2.day1;

import academy.kovalevskyi.javadeepdive.week1.day0.HttpRequestsHandler;
import academy.kovalevskyi.javadeepdive.week1.day2.ContentType;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpMethod;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpRequest;
import academy.kovalevskyi.javadeepdive.week1.day2.HttpResponse;
import academy.kovalevskyi.javadeepdive.week2.day0.JsonHelper;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reflections.Reflections;


public class RestServer extends Thread {
  private final int port = 8080;
  private ServerSocket serverSocket;
  private final ExecutorService executorService;
  private final List<Object> controllers;
  private final Map<HttpMethod, Class<? extends Annotation>> httpMethodsMap;

  public RestServer(final String packagePath) {
    this.controllers = this.getControllers(packagePath);
    this.httpMethodsMap = this.mapMethodToAnnotation();
    this.executorService = Executors.newCachedThreadPool();
    try {
      this.serverSocket = new ServerSocket(this.port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getHost() {
    return "localhost";
  }

  public int getPort() {
    return port;
  }

  private Map<HttpMethod, Class<? extends Annotation>> mapMethodToAnnotation() {
    Map<HttpMethod, Class<? extends Annotation>> httpMethodsMap = new HashMap();
    httpMethodsMap.put(HttpMethod.GET, Get.class);
    httpMethodsMap.put(HttpMethod.PUT, Put.class);
    httpMethodsMap.put(HttpMethod.POST, Post.class);
    httpMethodsMap.put(HttpMethod.DELETE, Delete.class);

    return httpMethodsMap;
  }

  private boolean isPathCorrect(final String path, final Method method) {
    return method.isAnnotationPresent(Path.class)
            && method.getAnnotation(Path.class).value().equals(path);
  }

  private Map<Object, Method> findController(final String path, final HttpMethod httpMethod) {
    for (Object controller : this.controllers) {
      Method[] controllerMethods = controller.getClass().getDeclaredMethods();
      var annotationMethodClass = this.httpMethodsMap.get(httpMethod);
      for (Method method : controllerMethods) {
        if (!this.isPathCorrect(path, method)
                || !method.isAnnotationPresent(annotationMethodClass)) {
          continue;
        }

        Map<Object, Method> result = new HashMap<>();
        result.put(controller, method);
        return result;
      }
    }
    return null;
  }

  private List<Object> getControllers(final String packageName) {
    Reflections reflections = new Reflections(packageName);

    Set<Class<? extends Object>> classes = reflections.getTypesAnnotatedWith(Controller.class);

    Iterator<Class<?>> iterator = classes.iterator();
    List<Object> controllers = new ArrayList<>();
    while (iterator.hasNext()) {
      Class<?> classObject = iterator.next();
      try {
        var instance = classObject.getConstructor().newInstance();
        controllers.add(instance);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    return controllers;
  }

  public void handleRequest(Socket socket) {
    var handler = new HttpRequestsHandler(socket);
    HttpRequest request = handler.getRequest();
    String stringBody = handler.getBody();
    ContentType contentType = ContentType.APPLICATION_JSON;
    if (handler.getHeaders().containsKey("Content-Type")) {
      String header = handler.getHeaders().get("Content-Type").split(";")[0];
      contentType = header != null
              ? ContentType.getByValue(header)
              : contentType;
    }

    Map<Object, Method> controller = this.findController(request.path(), request.httpMethod());

    if (controller == null) {
      handler.sendResponse(HttpResponse.ERROR_404);
      return;
    }
    try {
      var clazz = controller.keySet().toArray()[0];
      Method method = controller.get(clazz);
      Object body = method.getParameterTypes().length == 0
              ? method.invoke(clazz)
              : method.invoke(clazz, JsonHelper.fromJsonString(
              stringBody,
              method.getParameterTypes()[0]));
      var response = new HttpResponse.Builder()
              .body(JsonHelper.toJsonString(body))
              .contentType(contentType)
              .build();
      handler.sendResponse(response);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      handler.sendResponse(HttpResponse.ERROR_500);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      handler.sendResponse(HttpResponse.ERROR_500);
    } catch (InstantiationException e) {
      e.printStackTrace();
      handler.sendResponse(HttpResponse.ERROR_500);
    }
  }

  public void run() {
    while (this.isLive()) {
      try {
        Socket client = this.serverSocket.accept();
        System.out.println("hello");
        this.executorService.execute(() -> this.handleRequest(client));
      } catch (IOException e) {
        if (this.isLive()) {
          e.printStackTrace();
        }
      }
    }
  }

  public void stopServer() {
    try {
      this.serverSocket.close();
      this.executorService.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isLive() {
    return !this.serverSocket.isClosed();
  }
}
