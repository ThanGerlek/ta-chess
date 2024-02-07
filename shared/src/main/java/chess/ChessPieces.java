package chess;

public final class ChessPieces {
    private ChessPieces() {
    }

    public static ChessPiece FromType(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        return FromType(type, color, true);
    }

    public static ChessPiece FromType(ChessPiece.PieceType type, ChessGame.TeamColor color, boolean hasNeverMoved) {
        return switch (type) {
            case KING -> new ChessPiece(color, ChessPiece.PieceType.KING, hasNeverMoved);
            case QUEEN -> new ChessPiece(color, ChessPiece.PieceType.QUEEN, hasNeverMoved);
            case BISHOP -> new ChessPiece(color, ChessPiece.PieceType.BISHOP, hasNeverMoved);
            case KNIGHT -> new ChessPiece(color, ChessPiece.PieceType.KNIGHT, hasNeverMoved);
            case ROOK -> new ChessPiece(color, ChessPiece.PieceType.ROOK, hasNeverMoved);
            case PAWN -> new ChessPiece(color, ChessPiece.PieceType.PAWN, hasNeverMoved);
        };
    }

    public static char symbol(ChessPiece piece) {
        return symbol(piece.getPieceType(), piece.getTeamColor());
    }

    public static char symbol(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        char symbol = switch (type) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case ROOK -> 'R';
            case PAWN -> 'P';
        };
        if (color == ChessGame.TeamColor.BLACK) {
            symbol = (char) (symbol - 'A' + 'a');
        }
        return symbol;
    }

    public static ChessGame.TeamColor not(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    public static boolean isValidPromotionPiece(ChessPiece.PieceType type) {
        return type != ChessPiece.PieceType.PAWN && type != ChessPiece.PieceType.KING;
    }

    public static ChessPiece promote(ChessPiece piece, ChessPiece.PieceType promotionPiece) {
        return FromType(promotionPiece, piece.getTeamColor(), false);
    }
}
