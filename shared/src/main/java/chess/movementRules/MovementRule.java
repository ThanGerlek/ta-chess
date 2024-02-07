package chess.movementRules;

import chess.*;

import java.util.Collection;
import java.util.LinkedList;

public abstract class MovementRule {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    /**
     * Returns a version of the given ChessPosition shifted by the given amounts towards the opposing side. Positive
     * values shift up and right relative to the player who owns the piece.
     *
     * @param position the starting ChessPosition.
     * @param color the color of the player who owns the piece.
     * @param deltaRow the amount to shift the row.
     * @param deltaCol the amount to shift the column.
     * @return a shifted ChessPosition.
     */
    protected ChessPosition shiftRelative(ChessPosition position, ChessGame.TeamColor color, int deltaRow,
            int deltaCol) {
        if (color == ChessGame.TeamColor.WHITE) {
            return shift(position, deltaRow, deltaCol);
        } else {
            return shift(position, -deltaRow, -deltaCol);
        }
    }

    /**
     * Returns a version of the given ChessPosition shifted by the given amounts. Positive values shift up and right
     * relative to the white player.
     *
     * @param position the starting ChessPosition.
     * @param deltaRow the amount to shift the row.
     * @param deltaCol the amount to shift the column.
     * @return a shifted ChessPosition.
     */
    protected static ChessPosition shift(ChessPosition position, int deltaRow, int deltaCol) {
        return new ChessPosition(position.getRow() + deltaRow, position.getColumn() + deltaCol);
    }

    /**
     * Returns a Collection of all ChessMoves obtained by repeatedly applying the given RelativeChessMove to the given
     * starting ChessPosition any number of times until an invalid move is made. Application stops when applying the
     * RelativeChessMove again would land either outside the board, on an occupied space, or back on the start position
     * (the start position is not included in the Collection). If the final move is a capture (one that ends on a space
     * occupied by an enemy piece), it is included in the Collection, but counts as an end condition and the
     * relativeMove is not applied again.
     *
     * @param board         the ChessBoard.
     * @param startPosition the starting ChessPosition.
     * @param relativeMove  the RelativeChessMove to apply to the startPosition.
     * @return a Collection of ChessMoves made by repeatedly applying relativeMove.
     */
    protected Collection<ChessMove> getMovesFromRepeatedRelativeMove(ChessBoard board, ChessPosition startPosition, RelativeChessMove relativeMove) {
        Collection<ChessMove> moves = new LinkedList<>();

        ChessPosition currentEndPosition = relativeMove.apply(startPosition);

        while (isValidEmptySpace(board, currentEndPosition) && !currentEndPosition.equals(startPosition)) {
            moves.add(new ChessMove(startPosition, currentEndPosition));
            currentEndPosition = relativeMove.apply(currentEndPosition);
        }

        ChessPiece previousOccupant = board.getPiece(currentEndPosition);
        ChessGame.TeamColor movingPieceColor = board.getPiece(startPosition).getTeamColor();
        if (previousOccupant != null && previousOccupant.getTeamColor() != movingPieceColor) {
            moves.add(new ChessMove(startPosition, currentEndPosition));
        }

        return moves;
    }

    protected boolean isValidEmptySpace(ChessBoard board, ChessPosition position) {
        return position.isValidPosition() && board.getPiece(position) == null;
    }

    protected boolean isValidCapturingSpace(ChessBoard board, ChessGame.TeamColor movingPieceColor, ChessPosition position) {
        return position.isValidPosition()
                && board.getPiece(position) != null
                && board.getPiece(position).getTeamColor() != movingPieceColor;
    }
}