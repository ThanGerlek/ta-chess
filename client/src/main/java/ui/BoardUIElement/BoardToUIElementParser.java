package ui.BoardUIElement;

import chess.ChessBoard;
import chess.ChessPosition;
import ui.BoardUIElement.element.BackgroundElement;
import ui.BoardUIElement.element.BoardElement;
import ui.BoardUIElement.element.ForegroundElement;

public class BoardToUIElementParser implements BoardUIElementReader {
    private BoardElement[][] elements;

    public BoardToUIElementParser(ChessBoard board) {
        parseElements(board);
    }

    @Override
    public BoardElement get(int row, int col) {
        return elements[row][col];
    }

    private void parseElements(ChessBoard board) {
        elements = new BoardElement[10][10];
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements[0].length; j++) {
                elements[i][j] = generateBoardElement(i, j, board);
            }
        }
    }

    private BoardElement generateBoardElement(int row, int col, ChessBoard board) {
        BackgroundElement bg = generateBackgroundElement(row, col);
        ForegroundElement fg = generateForegroundElement(row, col, board);
        return new BoardElement(bg, fg);
    }

    private BackgroundElement generateBackgroundElement(int row, int col) {
        if (isOnBorder(row, col)) {
            return BackgroundElement.BORDER;
        } else if ((row + col) % 2 == 0) {
            return BackgroundElement.BLACK_SQUARE;
        } else {
            return BackgroundElement.WHITE_SQUARE;
        }
    }

    private ForegroundElement generateForegroundElement(int row, int col, ChessBoard board) {
        if (isOnBorder(row, col)) {
            return generateBorderFGElement(row, col);
        }

        ChessPosition chessPos = new ChessPosition(row, col);
        if (board.hasPieceAt(chessPos)) {
            return new ForegroundElement(board.getPiece(chessPos));
        }

        return new ForegroundElement();
    }

    private boolean isOnBorder(int row, int col) {
        return isOnTopOrBottomBorder(row, col) || isOnLeftOrRightBorder(row, col);
    }

    private boolean isOnBorderCorner(int row, int col) {
        return isOnTopOrBottomBorder(row, col) && isOnLeftOrRightBorder(row, col);
    }

    private boolean isOnTopOrBottomBorder(int row, int col) {
        return row == 0 || row == 9;
    }

    private boolean isOnLeftOrRightBorder(int row, int col) {
        return col == 0 || col == 9;
    }

    private ForegroundElement generateBorderFGElement(int row, int col) {
        if (isOnBorderCorner(row, col)) {
            return new ForegroundElement();
        } else if (isOnLeftOrRightBorder(row, col)) {
            return new ForegroundElement(getRankCharFromRowIndex(row));
        } else if (isOnTopOrBottomBorder(row, col)) {
            return new ForegroundElement(getFileCharFromColIndex(col));
        } else {
            throw new IllegalArgumentException(
                    "Called generateBorderFGElement() on a position that is not on the border");
        }
    }

    private char getFileCharFromColIndex(int col) {
        if (col <= 0 || col >= 9) {
            throw new IllegalArgumentException(String.format("Column index %d is out of bounds", col));
        }
        return (char) (col - 1 + 'a');
    }

    private char getRankCharFromRowIndex(int row) {
        if (row <= 0 || row >= 9) {
            throw new IllegalArgumentException(String.format("Row index %d is out of bounds", row));
        }
        return (char) (row - 1 + '1');
    }

}
