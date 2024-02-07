import client.REPL;

public class Main {
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";

    public static void main(String[] args) {
        System.out.println("Running chess client");

        String serverUrl = DEFAULT_SERVER_URL;
        if (args.length > 0) {
            serverUrl = args[0];
        }

        new REPL(serverUrl).run();
        System.out.println("Chess client terminated");
    }
}
