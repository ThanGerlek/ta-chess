package ui.BoardUIElement.element;

public record BoardElement(BackgroundElement bgElem, ForegroundElement fgElem) {

    public void appendTo(StringBuilder builder) {
        bgElem.appendTo(builder);
        fgElem.appendTo(builder);
    }

}