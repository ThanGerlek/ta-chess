package ui.BoardUIElement;

import ui.BoardUIElement.element.BoardElement;

public interface BoardUIElementReader {

    BoardElement get(int row, int col);
}
