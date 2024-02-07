package chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BoardMap {
    private final ChessPiece[] board = new ChessPiece[64];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardMap boardMap = (BoardMap) o;
        return Arrays.equals(board, boardMap.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    public void remove(ChessPosition pos) {
        board[index(pos)] = null;
    }

    private int index(ChessPosition pos) {
        return (pos.getRow() - 1) * 8 + (pos.getColumn() - 1);
    }

    public void put(ChessPosition pos, ChessPiece piece) {
        board[index(pos)] = piece;
    }

    public Set<ChessPosition> keySet() {
        Set<ChessPosition> positions = new HashSet<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if (get(pos) != null) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    public ChessPiece get(ChessPosition pos) {
        if (pos.getColumn() > 0 && pos.getRow() > 0 && pos.getColumn() <= 8 && pos.getRow() <= 8) {
            return board[index(pos)];
        } else {
            return null;
        }
    }
}
