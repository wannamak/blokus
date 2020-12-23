package blokus;

public enum Square {
  CELL('#'),
  RECEPTOR('+'),
  ADJACENT('x'),
  EMPTY(' ');

  private final char representation;

  Square(char representation) {
    this.representation = representation;
  }

  public char getRepresentation() {
    return representation;
  }
}
