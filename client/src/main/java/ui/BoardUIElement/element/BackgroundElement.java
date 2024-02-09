package ui.BoardUIElement.element;

import ui.EscapeSequences;

public class BackgroundElement {
    public static final BackgroundElement BORDER = new BackgroundElement(ElemType.BORDER);
    public static final BackgroundElement BLACK_SQUARE = new BackgroundElement(ElemType.BLACK_SQUARE);
    public static final BackgroundElement WHITE_SQUARE = new BackgroundElement(ElemType.WHITE_SQUARE);
    public static final BackgroundElement HIGHLIGHTED_SQUARE = new BackgroundElement(ElemType.HIGHLIGHTED_SQUARE);

    private final ElemType type;

    private BackgroundElement(ElemType type) {
        this.type = type;
    }

    public void appendTo(StringBuilder builder) {
        String str = switch (type) {
            case BORDER -> EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE;
            case BLACK_SQUARE -> EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
            case WHITE_SQUARE -> EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case HIGHLIGHTED_SQUARE -> EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_GREEN;
        };
        builder.append(str);
    }

    private enum ElemType {
        BORDER,
        BLACK_SQUARE,
        WHITE_SQUARE,
        HIGHLIGHTED_SQUARE
    }
}
