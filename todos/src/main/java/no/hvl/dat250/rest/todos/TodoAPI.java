package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static spark.Spark.*;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {
    private static Long counter = 1L;
    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));

        List<Todo> todos = new ArrayList<>();

        get("/todos", (req, res) -> {
            Gson gson = new Gson();
            return gson.toJson(todos);
        });

        get("/todos/:id", (req, res) -> {
            final Long id = parseIdFromRequest(req.params(":id"));
            if(id != null) {
                final Todo todo = findById(todos, id);
                if (todo != null) {
                    return todo.toJson();
                } else {
                    return String.format("Todo with the id  \"%s\" not found!", Long.parseLong(req.params(":id")));
                }
            } else {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
        });

        post("/todos", (req, res) -> {
            Gson gson = new Gson();
            final Todo todoFromJson = gson.fromJson(req.body(), Todo.class);
            final Todo todoCreated = new Todo(counter, todoFromJson.getSummary(), todoFromJson.getDescription());
            counter++;
            todos.add(todoCreated);

            return gson.toJson(todoCreated);
        });

        put("/todos/:id", (req, res) -> {
            final Long id = parseIdFromRequest(req.params(":id"));
            if(id != null) {
                Gson gson = new Gson();
                final Todo todo = gson.fromJson(req.body(), Todo.class);
                final Todo todoUpdated = updateById(todos, id, todo);
                if (todoUpdated != null) {
                    return todoUpdated.toJson();
                } else {
                    return String.format("Todo with the id  \"%s\" not found!", id);
                }
            } else {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
        });

        delete("/todos/:id", (req, res) -> {
            final Long id = parseIdFromRequest(req.params(":id"));
            if(id != null) {
                final Boolean result = deleteById(todos, id);
                if (result) {
                    return true;
                } else {
                    return String.format("Todo with the id  \"%s\" not found!", id);
                }
            } else {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
        });
    }

    private static Todo findById(final List<Todo> todos, final Long id) {
        ListIterator<Todo> todosIterator = todos.listIterator();
        while(todosIterator.hasNext()) {
            final Todo todo = todosIterator.next();
            if(Objects.equals(todo.getId(), id)) {
                return todo;
            }
        }

        return null;
    }

    private static Todo updateById(final List<Todo> todos, final Long id, final Todo todo) {
        ListIterator<Todo> todosIterator = todos.listIterator();
        while(todosIterator.hasNext()) {
            final Todo tmp = todosIterator.next();
            if(Objects.equals(tmp.getId(), id)) {
                todos.remove(tmp);
                todos.add(todo);
                return todo;
            }
        }

        return null;
    }

    private static Boolean deleteById(final List<Todo> todos, final Long id) {
        ListIterator<Todo> todosIterator = todos.listIterator();
        while(todosIterator.hasNext()) {
            final Todo tmp = todosIterator.next();
            if(Objects.equals(tmp.getId(), id)) {
                todos.remove(tmp);
                return true;
            }
        }

        return false;
    }

    private static Long parseIdFromRequest(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
