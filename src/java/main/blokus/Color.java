package blokus;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum Color {
  BLUE('B', (char) 1),
  YELLOW('Y', (char) 2),
  RED('R', (char) 4),
  GREEN('G', (char) 8);

  private final char cellRepresentation;
  private final char adjacentBitmap;

  public static final Map<Character, Color> REPRESENTATION_TO_COLOR = ImmutableMap.of(
      'B', BLUE,
      'Y', YELLOW,
      'R', RED,
      'G', GREEN);

  Color(char cellRepresentation, char adjacentBitmap) {
    this.cellRepresentation = cellRepresentation;
    this.adjacentBitmap = adjacentBitmap;
  }

  public char getCellRepresentation() {
    return cellRepresentation;
  }

  public char getAdjacentBitmap() {
    return adjacentBitmap;
  }
}
