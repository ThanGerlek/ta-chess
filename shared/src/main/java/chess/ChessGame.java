package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private WinState winState = WinState.IN_PROGRESS;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void resign(TeamColor color) {
        winState = (color == TeamColor.BLACK) ? WinState.WHITE_WIN : WinState.BLACK_WIN;
    }

    public WinState getWinState() {
        return winState;
    }

    private boolean isGameOver() {
        return getWinState() == WinState.WHITE_WIN || getWinState() == WinState.BLACK_WIN;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : potentialMoves) {
            if (!wouldLeaveInCheck(move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();

        if (!board.hasPieceAt(startPosition)) {
            throw new InvalidMoveException("Called makeMove() on an empty position");
        }

        if (board.getPiece(startPosition).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Called makeMove() on the opponent's turn");
        }

        if (!validMoves(startPosition).contains(move)) {
            throw new InvalidMoveException("Called makeMove() on an invalid move");
        }

        if (isGameOver()) {
            throw new InvalidMoveException("Called makeMove() after the game is over");
        }

        board.forceApplyMove(move);

        ChessPiece piece = board.getPiece(move.getEndPosition());
        piece.markAsHavingMoved();
        promoteIfValid(move.getEndPosition(), move.getPromotionPiece());
        changeTeamTurn();
        updateGameOver();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (!board.containsKing(teamColor)) {
            return false;
        }

        ChessPosition kingPosition = board.getKingPosition(teamColor);
        return isPositionUnderAttackFrom(kingPosition, ChessPieces.not(teamColor));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // TODO optimize?
        if (!isInCheck(teamColor)) {
            return false;
        }

        ChessPosition kingPosition = board.getKingPosition(teamColor);
        if (canEscapeCheckWithPiece(kingPosition)) return false;

        Collection<ChessPosition> teamPositions = board.getTeamPieces(teamColor);
        for (ChessPosition teammatePosition : teamPositions) {
            if (canEscapeCheckWithPiece(teammatePosition)) return false;
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (ChessPosition position : board.getTeamPieces(teamColor))
            if (!validMoves(position).isEmpty()) return false;

        return true;
    }

    private boolean wouldLeaveInCheck(ChessMove move) {
        TeamColor color = board.getPiece(move.getStartPosition()).getTeamColor();
        ChessPiece capturedPiece = board.forceApplyMove(move);
        boolean result = isInCheck(color);
        board.forceRestoreFromMove(move, capturedPiece);
        return result;
    }

    private boolean isPositionUnderAttackFrom(ChessPosition position, TeamColor attackColor) {
        // TODO Implement caching?

        for (ChessPosition attackPosition : board.getTeamPieces(attackColor)) {

            ChessPiece attacker = board.getPiece(attackPosition);
            Collection<ChessMove> attackerMoves = attacker.pieceMoves(board, attackPosition);
            ChessMove attackMove = new ChessMove(attackPosition, position);

            for (ChessMove validAttackerMove : attackerMoves) {
                if (attackMove.equalsIgnorePromotion(validAttackerMove)) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void changeTeamTurn() {
        teamTurn = ChessPieces.not(teamTurn);
    }

    private void updateGameOver() {
        if (isGameOver()) {
            return;
        }
        if (isInCheckmate(TeamColor.WHITE)) {
            winState = WinState.BLACK_WIN;
        } else if (isInCheckmate(TeamColor.BLACK)) {
            winState = WinState.WHITE_WIN;
        } else if (isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK)) {
            winState = WinState.STALEMATE;
        }
    }

    private void promoteIfValid(ChessPosition position, ChessPiece.PieceType promotionPiece)
            throws InvalidMoveException {
        if (!board.hasPieceAt(position)) {
            throw new IllegalArgumentException("Tried to promote an empty space");
        }
        ChessPiece piece = board.getPiece(position);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && promotionPiece != null) {
            if (ChessPieces.isValidPromotionPiece(promotionPiece)) {
                ChessPiece newPiece = ChessPieces.promote(piece, promotionPiece);
                board.removePiece(position);
                board.addPiece(position, newPiece);
            } else {
                throw new InvalidMoveException("Tried to promote to an invalid type: " + promotionPiece);
            }
        }
    }

    private boolean canEscapeCheckWithPiece(ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        for (ChessMove potentialMove : piece.pieceMoves(board, position)) {
            if (!wouldLeaveInCheck(potentialMove)) {
                return true;
            }
        }
        return false;
    }
}
