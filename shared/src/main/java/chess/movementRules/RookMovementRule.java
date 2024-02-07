package chess.movementRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.LinkedList;

public class RookMovementRule extends MovementRule {

    // TODO castling

    /**
     * Calculates all the positions this chess piece can move to. Does not take into account moves that are illegal due
     * to leaving the king in danger.
     *
     * @param board      the current ChessBoard.
     * @param myPosition this Rook's current position.
     * @return a Collection of valid moves.
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new LinkedList<>();

        RelativeChessMove moveUp = pos -> shift(pos, 1, 0);
        RelativeChessMove moveDown = pos -> shift(pos, -1, 0);
        RelativeChessMove moveLeft = pos -> shift(pos, 0, -1);
        RelativeChessMove moveRight = pos -> shift(pos, 0, 1);

        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveUp));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveDown));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveLeft));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveRight));

        return moves;
    }
}
