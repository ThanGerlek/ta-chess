package chess;

import chess.movementRules.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor color;
    private boolean hasNeverMoved;

    public ChessPiece(ChessGame.TeamColor color, PieceType type) {
        this(color, type, true);
    }

    public ChessPiece(ChessGame.TeamColor color, PieceType type, boolean hasNeverMoved) {
        this.type = type;
        this.color = color;
        this.hasNeverMoved = hasNeverMoved;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return which team this chess piece belongs to.
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is.
     */
    public PieceType getPieceType() {
        return type;
    }

    public ChessPiece copy() {
        return ChessPieces.FromType(getPieceType(), getTeamColor(), hasNeverMoved());
    }

    public void markAsHavingMoved() {
        hasNeverMoved = false;
    }

    public boolean hasNeverMoved() {
        return hasNeverMoved;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch(board.getPiece(myPosition).getPieceType()) {
            case KING -> new KingMovementRule().pieceMoves(board, myPosition);
            case QUEEN -> new QueenMovementRule().pieceMoves(board, myPosition);
            case BISHOP -> new BishopMovementRule().pieceMoves(board, myPosition);
            case KNIGHT -> new KnightMovementRule().pieceMoves(board, myPosition);
            case ROOK -> new RookMovementRule().pieceMoves(board, myPosition);
            case PAWN -> new PawnMovementRule().pieceMoves(board, myPosition);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return type == piece.type && color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, hasNeverMoved);
    }
}
