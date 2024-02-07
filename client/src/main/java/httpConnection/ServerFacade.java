package httpConnection;

import com.google.gson.Gson;
import http.MessageResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public void makeRequest(RequestData reqData) throws FailedResponseException, FailedConnectionException {
        makeRequest(reqData, null);
    }

    public <T> T makeRequest(RequestData reqData, Class<T> responseClass)
            throws FailedResponseException, FailedConnectionException {
        HttpURLConnection http = setUpConnection(reqData.method(), serverURL + reqData.path());
        writeRequest(reqData.request(), http, reqData.authTokenString());
        connect(http);
        return readResponse(http, responseClass);
    }

    private static HttpURLConnection setUpConnection(String method, String urlString) throws FailedConnectionException {
        try {
            URL url = (new URI(urlString)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput("POST".equals(method) || "PUT".equals(method));
            return http;
        } catch (IOException | URISyntaxException e) {
            throw new FailedConnectionException("Failed to set up HTTP connection: " + e.getMessage());
        }
    }

    private static void writeRequest(Object request, HttpURLConnection http, String authTokenString)
            throws FailedConnectionException {
        try {
            writeHeaders(http, authTokenString);
            writeRequestBody(request, http);
        } catch (IOException e) {
            throw new FailedConnectionException("Could not write request body: " + e.getMessage());
        }
    }

    private static void connect(HttpURLConnection http) throws FailedConnectionException {
        try {
            http.connect();
        } catch (IOException e) {
            throw new FailedConnectionException("Failed to connect to server: " + e.getMessage());
        }
    }

    private static <T> T readResponse(HttpURLConnection http, Class<T> responseClass) throws FailedResponseException {
        int responseCode;
        try {
            responseCode = http.getResponseCode();
        } catch (IOException e) {
            throw new FailedResponseException("Could not get response code: " + e.getMessage());
        }

        if (responseCode >= 400) {
            readErrorResponse(http);
            return null;
        } else {
            return readResponseBody(http, responseClass);
        }

    }

    private static void writeHeaders(HttpURLConnection http, String authTokenString) {
        http.addRequestProperty("Content-type", "application/json");
        if (authTokenString != null) {
            http.addRequestProperty("authorization", authTokenString);
        }
    }

    private static void writeRequestBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            String requestData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(requestData.getBytes());
            }
        }
    }

    private static void readErrorResponse(HttpURLConnection http) throws FailedResponseException {
        MessageResponse errorResponse;
        try (InputStream errorBody = http.getErrorStream()) {
            if (errorBody != null) {
                InputStreamReader reader = new InputStreamReader(errorBody);
                errorResponse = new Gson().fromJson(reader, MessageResponse.class);
                String errMsg = errorResponse.message();
                throw new FailedResponseException(errMsg);
            }
            throw new RuntimeException("Unreachable code! Called readErrorResponse() but errorBody was null");
        } catch (IOException e) {
            throw new FailedResponseException("Failed to read error response body: " + e.getMessage());
        }
    }

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> responseClass)
            throws FailedResponseException {
        if (responseClass == null) {
            return null;
        }

        T response;
        try (InputStream responseBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(responseBody);
            response = new Gson().fromJson(reader, responseClass);
        } catch (IOException e) {
            throw new FailedResponseException("Failed to read response body: " + e.getMessage());
        }
        return response;
    }
}
