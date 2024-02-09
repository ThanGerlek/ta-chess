package ui.BoardUIElement;

import chess.ChessGame;
import ui.BoardUIElement.element.BoardElement;

public class BoardUIElementTransformer {
    private final ChessGame.TeamColor color;

    public BoardUIElementTransformer(ChessGame.TeamColor color) {
        this.color = color;
    }

    public BoardElement getFrom(BoardUIElementReader reader, int outputRow, int outputCol) {
        if (color == ChessGame.TeamColor.WHITE) {
            return reader.get(outputRow, outputCol);
        } else {
            return reader.get(9 - outputRow, 9 - outputCol);
        }

    }
}
