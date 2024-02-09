package client;

import chess.ChessPiece;
import chess.ChessPosition;
import ui.InvalidUserInputException;

public class ChessInputParser {
    public static ChessPosition parseToPosition(String rawPositionString)
            throws InvalidUserInputException, CommandCancelException {
        String positionString = rawPositionString.strip().toLowerCase();
        if (positionString.isEmpty()) {
            throw new CommandCancelException("Cancelled by player");
        } else if (positionString.length() != 2) {
            throw new InvalidUserInputException(positionString, "Invalid positionString: '" + positionString + "'");
        }
        int col = positionString.charAt(0) - 'a' + 1;
        int row = positionString.charAt(1) - '1' + 1;
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new InvalidUserInputException(positionString, "Invalid positionString: '" + positionString + "'");
        }
        return new ChessPosition(row, col);
    }

    public static ChessPiece.PieceType parseToPromotionPiece(String rawPromotionString)
            throws InvalidUserInputException {
        String promotionString = rawPromotionString.strip().toLowerCase();
        if (promotionString.isEmpty()) {
            return null;
        } else if (promotionString.length() != 1) {
            throw new InvalidUserInputException(promotionString, "Invalid promotionString: '" + promotionString + "'");
        }

        return switch (promotionString) {
            case "q" -> ChessPiece.PieceType.QUEEN;
            case "b" -> ChessPiece.PieceType.BISHOP;
            case "r" -> ChessPiece.PieceType.ROOK;
            case "n" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new InvalidUserInputException(promotionString, "Invalid promotionString: '" + promotionString + "'");
        };
    }

}
