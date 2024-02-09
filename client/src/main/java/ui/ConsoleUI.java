package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessInputParser;
import client.CommandCancelException;

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

    public ChessPosition promptChessPosition(String prompt) throws InvalidUserInputException, CommandCancelException {
        String startString = promptInput(prompt);
        return ChessInputParser.parseToPosition(startString);
    }

    public ChessMove promptChessMove() throws InvalidUserInputException, CommandCancelException {
        return promptChessMove("");
    }

    public ChessMove promptChessMove(String prompt) throws InvalidUserInputException, CommandCancelException {
        if (!"".equals(prompt)) {
            println(prompt);
        }
        ChessPosition startPosition = promptChessPosition("Enter the starting position: ");
        ChessPosition endPosition = promptChessPosition("Enter the ending position: ");

        String promotionString = promptInput("(Optional) Enter the promotion piece [Q|R|B|N]: ");
        ChessPiece.PieceType promotionPiece = ChessInputParser.parseToPromotionPiece(promotionString);

        return new ChessMove(startPosition, endPosition, promotionPiece);
    }

    public void reprintPrompt() {
        print(previousPrompt);
    }
}
