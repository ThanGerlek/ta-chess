package ui.BoardUIElement;

import chess.ChessPosition;
import ui.BoardUIElement.element.BackgroundElement;
import ui.BoardUIElement.element.BoardElement;

import java.util.Collection;

public class BoardUIElementHighlighter implements BoardUIElementReader {
    private final BoardUIElementReader reader;
    private final Collection<ChessPosition> highlightedPositions;

    public BoardUIElementHighlighter(BoardUIElementReader reader, Collection<ChessPosition> highlightedPositions) {
        this.reader = reader;
        this.highlightedPositions = highlightedPositions;
    }

    @Override
    public BoardElement get(int row, int col) {
        if (highlightedPositions.contains(new ChessPosition(row, col))) {
            BoardElement elem = reader.get(row, col);
            return new BoardElement(BackgroundElement.HIGHLIGHTED_SQUARE, elem.fgElem());
        } else {
            return reader.get(row, col);
        }
    }
}
