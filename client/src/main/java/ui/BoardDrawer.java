package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import ui.BoardUIElement.BoardToUIElementParser;
import ui.BoardUIElement.BoardUIElementHighlighter;
import ui.BoardUIElement.BoardUIElementReader;
import ui.BoardUIElement.BoardUIElementTransformer;

import java.util.Collection;

public class BoardDrawer {
    private final ConsoleUI ui;
    private final BoardUIElementReader reader;
    private final BoardUIElementTransformer transformer;

    public BoardDrawer(ConsoleUI ui, ChessBoard board, ChessGame.TeamColor color) {
        this.ui = ui;
        this.reader = new BoardToUIElementParser(board);
        this.transformer = new BoardUIElementTransformer(color);
    }

    public void drawWithHighlightedPositions(Collection<ChessPosition> highlightedPositions) {
        BoardUIElementReader highlightReader = new BoardUIElementHighlighter(reader, highlightedPositions);
        drawFromReader(highlightReader);
    }

    public void draw() {
        drawFromReader(this.reader);
    }

    private void drawFromReader(BoardUIElementReader reader) {
        StringBuilder builder = new StringBuilder();
        appendNewLine(builder);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                transformer.getFrom(reader, 9 - row, col).appendTo(builder);
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