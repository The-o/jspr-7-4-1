package ru.netology.servlet;

import ru.netology.controller.PostController;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainServlet extends HttpServlet {
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";

    private PostController controller;

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");

        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI().substring(getServletConfig().getServletContext().getContextPath().length());
            final var method = req.getMethod();
            // primitive routing
            switch (method) {
                case METHOD_GET:
                    handleGet(path, req, resp);
                    break;

                case METHOD_POST:
                    handlePost(path, req, resp);
                    break;

                case METHOD_DELETE:
                    handleDelete(path, req, resp);
                    break;

                default:
                    handleNotFound(resp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGet(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.equals("/api/posts")) {
            controller.all(resp);
            return;
        }
        if (path.matches("/api/posts/\\d+")) {
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            controller.getById(id, resp);
            return;
        }

        handleNotFound(resp);
    }

    private void handlePost(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.equals("/api/posts")) {
            controller.save(req.getReader(), resp);
            return;
        }

        handleNotFound(resp);
    }

    private void handleDelete(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.matches("/api/posts/\\d+")) {
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            controller.removeById(id, resp);
            return;
        }

        handleNotFound(resp);
    }

    private void handleNotFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
