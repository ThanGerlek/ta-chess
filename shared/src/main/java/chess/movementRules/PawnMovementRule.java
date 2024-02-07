package chess.movementRules;

import chess.*;

import java.util.Collection;
import java.util.LinkedList;

public class PawnMovementRule extends MovementRule {

    // TODO en passant
    //  ??? 3-valued variable (left, right, none) that the Board can set
    //  when this pawn becomes capable of attacking? Make sure it
    //  resets it afterward!

    /**
     * Calculates all the positions this chess piece can move to. Does not take into account moves that are illegal due
     * to leaving the king in danger.
     *
     * @param board         the current ChessBoard.
     * @param startPosition this Pawn's current position.
     * @return a Collection of valid moves.
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessPosition> endPositions = new LinkedList<>();

        addForwardEndPositionsIfValid(endPositions, board, startPosition);
        addDiagonalAttackEndPositionsIfValid(endPositions, board, startPosition, 1);
        addDiagonalAttackEndPositionsIfValid(endPositions, board, startPosition, -1);

        return createMovesFromEndPositions(endPositions, startPosition, board);
    }

    private Collection<ChessMove> createMovesFromEndPositions(Collection<ChessPosition> endPositions,
            ChessPosition startPosition, ChessBoard board) {
        Collection<ChessMove> moves = new LinkedList<>();
        for (ChessPosition endPosition : endPositions) {
            ChessGame.TeamColor color = board.getPiece(startPosition).getTeamColor();
            if (canPromote(endPosition, color)) {
                addPromotionMovesFromMove(moves, color, startPosition, endPosition);
            } else {
                moves.add(new ChessMove(startPosition, endPosition));
            }
        }
        return moves;
    }

    private boolean canPromote(ChessPosition endPosition, ChessGame.TeamColor color) {
        return endPosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE ||
                endPosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK;
    }

    private void addPromotionMovesFromMove(Collection<ChessMove> moves, ChessGame.TeamColor color,
            ChessPosition startPosition, ChessPosition endPosition) {
        if (endPosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE ||
                endPosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK) {
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        }
    }

    private void addForwardEndPositionsIfValid(Collection<ChessPosition> endPositions, ChessBoard board,
            ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        ChessPosition oneStepPosition = shiftRelative(myPosition, piece.getTeamColor(), 1, 0);
        if (isValidEmptySpace(board, oneStepPosition)) {
            endPositions.add(oneStepPosition);

            ChessPosition twoStepPosition = shiftRelative(myPosition, piece.getTeamColor(), 2, 0);
            if (piece.hasNeverMoved() && isOnStartRow(myPosition, piece.getTeamColor()) &&
                    isValidEmptySpace(board, twoStepPosition)) {
                endPositions.add(twoStepPosition);
            }
        }
    }

    private void addDiagonalAttackEndPositionsIfValid(Collection<ChessPosition> endPositions, ChessBoard board,
            ChessPosition startPosition, int direction) {
        int deltaCol = (direction > 0) ? 1 : -1;
        ChessGame.TeamColor color = board.getPiece(startPosition).getTeamColor();
        ChessPosition endPosition = shiftRelative(startPosition, color, 1, deltaCol);

        if (isValidCapturingSpace(board, color, endPosition)) endPositions.add(endPosition);
    }

    private boolean isOnStartRow(ChessPosition position, ChessGame.TeamColor color) {
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        return position.getRow() == startRow;
    }

}