package server.handlers;

import com.google.gson.Gson;
import dataAccess.*;
import http.MessageResponse;
import spark.Request;
import spark.Response;

public abstract class HttpHandler {
    protected Gson gson = new Gson();

    public Object handleRequest(Request req, Response res) {
        return defaultErrorHandler(req, res);
    }

    protected Object defaultErrorHandler(Request req, Response res) {
        try {
            return route(req, res);
        } catch (NoSuchItemException | BadRequestException e) {
            return handleError(res, 400, e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return handleError(res, 401, e.getMessage());
        } catch (ValueAlreadyTakenException e) {
            return handleError(res, 403, e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Threw an unknown error: " + e.getMessage());
            return handleError(res, 500, e.getMessage());
        }
    }

    protected abstract Object route(Request request, Response response) throws DataAccessException;

    protected String handleError(Response res, int status, String errMsg) {
        MessageResponse response = new MessageResponse(String.format("Error: %s", errMsg));
        return parseToBody(res, response, status);
    }

    public static String parseToBody(Response res, Object response, int status) {
        res.status(status);
        return parseToBody(res, response);
    }

    public static String parseToBody(Response res, Object response) {
        String bodyStr = (new Gson()).toJson(response);
        res.type("application/json");
        res.body(bodyStr);
        return bodyStr;
    }

}
