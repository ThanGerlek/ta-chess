package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] pieces;

    public ChessBoard() {
        this.pieces = new ChessPiece[8][8];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece thisPiece = pieces[i][j];
                ChessPiece otherPiece = that.pieces[i][j];
                boolean equal = Objects.equals(thisPiece, otherPiece);
                if (!Objects.equals(pieces[i][j], that.pieces[i][j])) {
                    return false;
                }
            }
        }
        return true;

//        return Arrays.deepEquals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(pieces);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                char symbol = ' ';
                if (hasPieceAt(position)) {
                    ChessPiece piece = getPiece(position);
                    symbol = ChessPieces.symbol(piece);
                }
                builder.append("|").append(symbol);
            }
            builder.append("|\n");
        }
        return builder.toString();
    }

    /**
     * Adds a chess piece to the chessboard.
     *
     * @param position at which to add the piece.
     * @param piece    the piece to add.
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (hasPieceAt(position)) {
            String errMsg = String.format(
                    "Tried to add a piece to a nonempty position. Position: '%s', piece to add: '%s', existing piece:" +
                            " '%s'", position.toString(), piece.toString(), getPiece(position).toString());
            throw new IllegalArgumentException(errMsg);
        }
        forceSetPiece(position, piece);
    }

    public void removePiece(ChessPosition position) {
        forceSetPiece(position, null);
    }

    private void forceSetPiece(ChessPosition position, ChessPiece piece) {
        pieces[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard.
     *
     * @param position the position to get the piece from.
     * @return the piece at the position, or null if no piece is at that position.
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (!position.isValidPosition()) return null;
        return pieces[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean hasPieceAt(ChessPosition position) {
        return position.isValidPosition() && getPiece(position) != null;
    }

    /**
     * Sets the board to the default starting board (how the game of chess normally starts).
     */
    public void resetBoard() {
        clearBoard();
        placePawns();
        placeKnights();
        placeBishops();
        placeRooks();
        placeRoyals();
    }

    public void clearBoard() {
        pieces = new ChessPiece[8][8];
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == color) {
                    return new ChessPosition(i + 1, j + 1);
                }
            }
        }
        throw new InvalidBoardException("Called getKingPosition() but no King piece was found");
    }

    public Collection<ChessPosition> getTeamPieces(ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];
                if (piece != null && piece.getTeamColor() == teamColor) {
                    positions.add(new ChessPosition(i + 1, j + 1));
                }
            }
        }
        return positions;
    }

    public ChessPiece forceApplyMove(ChessMove move) {
        ChessPiece capturedPiece = getPiece(move.getEndPosition());
        if (capturedPiece != null) {
            capturedPiece = capturedPiece.copy();
        }
        removePiece(move.getEndPosition());

        ChessPiece piece = getPiece(move.getStartPosition()).copy();
        removePiece(move.getStartPosition());

        addPiece(move.getEndPosition(), piece);
        return capturedPiece;
    }

    public void forceRestoreFromMove(ChessMove move, ChessPiece capturedPiece) {
        ChessPiece piece = getPiece(move.getEndPosition()).copy();
        removePiece(move.getEndPosition());

        addPiece(move.getStartPosition(), piece);
        if (capturedPiece != null) {
            addPiece(move.getEndPosition(), capturedPiece);
        }
    }

    public boolean containsKing(ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == color) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placePawns() {
        for (int col = 1; col <= 8; col++) {
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition whitePosition = new ChessPosition(2, col);
            ChessPosition blackPosition = new ChessPosition(7, col);
            addPiece(whitePosition, whitePawn);
            addPiece(blackPosition, blackPawn);
        }
    }

    private void placeKnights() {
        ChessPiece whiteKnight1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteKnight2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition whiteKnightPosition1 = new ChessPosition(1, 2);
        ChessPosition whiteKnightPosition2 = new ChessPosition(1, 7);
        ChessPosition blackKnightPosition1 = new ChessPosition(8, 2);
        ChessPosition blackKnightPosition2 = new ChessPosition(8, 7);
        addPiece(whiteKnightPosition1, whiteKnight1);
        addPiece(whiteKnightPosition2, whiteKnight2);
        addPiece(blackKnightPosition1, blackKnight1);
        addPiece(blackKnightPosition2, blackKnight2);
    }

    private void placeBishops() {
        ChessPiece whiteBishop1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteBishop2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition whiteBishopPosition1 = new ChessPosition(1, 3);
        ChessPosition whiteBishopPosition2 = new ChessPosition(1, 6);
        ChessPosition blackBishopPosition1 = new ChessPosition(8, 3);
        ChessPosition blackBishopPosition2 = new ChessPosition(8, 6);
        addPiece(whiteBishopPosition1, whiteBishop1);
        addPiece(whiteBishopPosition2, whiteBishop2);
        addPiece(blackBishopPosition1, blackBishop1);
        addPiece(blackBishopPosition2, blackBishop2);
    }

    private void placeRooks() {
        ChessPiece whiteRook1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteRook2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition whiteRookPosition1 = new ChessPosition(1, 1);
        ChessPosition whiteRookPosition2 = new ChessPosition(1, 8);
        ChessPosition blackRookPosition1 = new ChessPosition(8, 1);
        ChessPosition blackRookPosition2 = new ChessPosition(8, 8);
        addPiece(whiteRookPosition1, whiteRook1);
        addPiece(whiteRookPosition2, whiteRook2);
        addPiece(blackRookPosition1, blackRook1);
        addPiece(blackRookPosition2, blackRook2);
    }

    private void placeRoyals() {
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition whiteKingPosition = new ChessPosition(1, 5);
        ChessPosition blackKingPosition = new ChessPosition(8, 5);
        ChessPosition whiteQueenPosition = new ChessPosition(1, 4);
        ChessPosition blackQueenPosition = new ChessPosition(8, 4);
        addPiece(whiteKingPosition, whiteKing);
        addPiece(blackKingPosition, blackKing);
        addPiece(whiteQueenPosition, whiteQueen);
        addPiece(blackQueenPosition, blackQueen);
    }


}
