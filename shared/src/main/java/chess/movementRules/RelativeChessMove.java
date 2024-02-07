package chess.movementRules;

import chess.ChessPosition;

@FunctionalInterface
public interface RelativeChessMove {
    ChessPosition apply(ChessPosition position);
}
