package chess.movementRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.LinkedList;

public class BishopMovementRule extends MovementRule {

    /**
     * Calculates all the positions this chess piece can move to. Does not take into account moves that are illegal due
     * to leaving the king in danger.
     *
     * @param board      the current ChessBoard.
     * @param myPosition this Bishop's current position.
     * @return a Collection of valid moves.
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new LinkedList<>();

        RelativeChessMove moveUpLeft = pos -> shift(pos, 1, -1);
        RelativeChessMove moveUpRight = pos -> shift(pos, 1, 1);
        RelativeChessMove moveDownLeft = pos -> shift(pos, -1, -1);
        RelativeChessMove moveDownRight = pos -> shift(pos, -1, 1);

        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveUpLeft));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveUpRight));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveDownLeft));
        moves.addAll(getMovesFromRepeatedRelativeMove(board, myPosition, moveDownRight));

        return moves;
    }
}
