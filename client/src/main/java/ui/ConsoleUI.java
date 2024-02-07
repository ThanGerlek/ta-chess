package ui;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final PrintStream printStream;
    private String previousPrompt = "";

    public ConsoleUI(Scanner scanner, PrintStream printStream) {
        this.scanner = scanner;
        this.printStream = printStream;
        resetColors();
    }

    public void resetColors() {
        print(EscapeSequences.RESET_TEXT_AND_BG);
    }

    public void print(String string) {
        printStream.print(string);
    }

    public void println(String string) {
        printStream.println(string);
    }

    public String promptInput(String prompt) {
        previousPrompt = prompt;
        print(prompt);
        return sanitize(scanner.nextLine());
    }

    private String sanitize(String input) {
        return input.strip().toLowerCase();
    }

    public Integer promptMaybeInteger(String prompt) {
        previousPrompt = prompt;
        print(prompt);
        String raw = sanitize(scanner.nextLine());
        if (raw.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(raw);
        }
    }

    public void reprintPrompt() {
        print(previousPrompt);
    }
}
