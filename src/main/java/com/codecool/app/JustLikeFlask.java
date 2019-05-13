package com.codecool.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class JustLikeFlask
{

    private static final List<Method> routeMethods = getRoutingMethods();
    private static final String PAGE404 = "Page not found";

    public static void main(String[] args )
    {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    String notAnnotated() {
        return null;
    }

    @WebRoute("/test")
    String onTest() {
        return addCSS(Color.cornsilk) + "<h1>Test page</h1>";
    }

    @WebRoute("/might")
    String onMight() {
        return addCSS(Color.azure) + "<h1>Might page</h1>";
    }

    @WebRoute("/and")
    String onAnd() {
        return addCSS(Color.aliceblue) + "<h1>And page</h1>";
    }

    @WebRoute("/magic")
    String onMagic() {
        return addCSS(Color.linen) + "<h1>Magic page</h1>";
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestedPath = t.getRequestURI().toString();
            String response = getResponse(requestedPath);

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static String getResponse(String requestedPath) {
        Method method = getMethod(requestedPath);
        if (method == null) { return PAGE404; }

        Class<?> type = JustLikeFlask.class;
        Constructor<?> constructor = null;

        try { constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Object instance = null;
        try { instance = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        String response = "";
        try { response = (String) method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static Method getMethod(String requestedPath) {
        Method requestedMethod = null;
        for (Method method : routeMethods) {
            String methodPath = method.getAnnotation(WebRoute.class).value();
            if (methodPath.equals(requestedPath)) {
                requestedMethod = method;
                break;
            }
        }
        return requestedMethod;
    }

    private static List<Method> getRoutingMethods() {
        List<Method> routeMethods = new ArrayList<>();

        Method[] methods = JustLikeFlask.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(WebRoute.class) != null) {
                routeMethods.add(method);
            }
        }

        return routeMethods;
    }

    private static String addCSS(Color color) {
        return "<style>" +
                "body { background-color: " + color + ";}" +
                "h1 { text-align: center; margin-top: 46vh;}" +
                "</style>";
    }
}
