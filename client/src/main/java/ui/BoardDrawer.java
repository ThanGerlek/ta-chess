package ui;

import chess.ChessBoard;
import chess.ChessGame;
import ui.BoardUIElement.BoardToUIElementParser;
import ui.BoardUIElement.BoardUIElementReader;
import ui.BoardUIElement.BoardUIElementRotator;

public class BoardDrawer {
    private final ConsoleUI ui;
    private final BoardToUIElementParser parser;
    private BoardUIElementReader reader;

    public BoardDrawer(ConsoleUI ui, ChessBoard board) {
        this.ui = ui;
        this.parser = new BoardToUIElementParser(board);
        this.reader = parser;
        setViewerTeamColor(ChessGame.TeamColor.WHITE);
    }

    public void setViewerTeamColor(ChessGame.TeamColor color) {
        if (color == null) {
            color = ChessGame.TeamColor.WHITE;
        }
        this.reader = new BoardUIElementRotator(parser, color);
    }

    public void draw() {
        StringBuilder builder = new StringBuilder();
        appendNewLine(builder);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                reader.get(row, col).appendTo(builder);
            }
            appendNewLine(builder);
        }

        ui.println(builder.toString());
    }

    private void appendNewLine(StringBuilder builder) {
        appendReset(builder);
        builder.append("\n");
    }

    private void appendReset(StringBuilder builder) {
        builder.append(EscapeSequences.RESET_TEXT_AND_BG);
    }
}