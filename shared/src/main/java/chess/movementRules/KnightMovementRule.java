package chess.movementRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.LinkedList;

public class KnightMovementRule extends MovementRule {

    /**
     * Calculates all the positions this chess piece can move to. Does not take into account moves that are illegal due
     * to leaving the king in danger.
     *
     * @param board      the current ChessBoard.
     * @param myPosition this Knight's current position.
     * @return a Collection of valid moves.
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new LinkedList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        for (ChessPosition endPosition : getPotentialEndPositions(myPosition))
            if (isValidEmptySpace(board, endPosition) || isValidCapturingSpace(board, color, endPosition)) {
                moves.add(new ChessMove(myPosition, endPosition));
            }

        return moves;
    }

    private Collection<ChessPosition> getPotentialEndPositions(ChessPosition position) {
        Collection<ChessPosition> positions = new LinkedList<>();
        positions.add(shift(position, 2, -1));
        positions.add(shift(position, 2, 1));

        positions.add(shift(position, 1, -2));
        positions.add(shift(position, 1, 2));

        positions.add(shift(position, -1, -2));
        positions.add(shift(position, -1, 2));

        positions.add(shift(position, -2, -1));
        positions.add(shift(position, -2, 1));
        return positions;
    }
}
