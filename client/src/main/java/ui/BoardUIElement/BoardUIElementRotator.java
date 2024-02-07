package ui.BoardUIElement;

import chess.ChessGame;
import ui.BoardUIElement.element.BoardElement;

public class BoardUIElementRotator implements BoardUIElementReader {
    private final BoardUIElementReader reader;
    private final ChessGame.TeamColor color;

    public BoardUIElementRotator(BoardUIElementReader reader, ChessGame.TeamColor color) {
        this.reader = reader;
        this.color = color;
    }

    @Override
    public BoardElement get(int outputRow, int outputCol) {
        if (color == ChessGame.TeamColor.WHITE) {
            return reader.get(9 - outputRow, outputCol);
        } else {
            return reader.get(outputRow, 9 - outputCol);
        }
    }
}
