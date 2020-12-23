package blokus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Piece implements Comparable<Piece> {
  private final int rotationId;
  private final int pieceId;
  private final int uniquePieceId;
  private final boolean isFlipped;
  private final Square[][] square;
  private final Set<YX> receptors;
  private final Set<YX> cells;

  public Piece(int pieceId, int uniquePieceId, int rotationId, boolean isFlipped, Square[][] square) {
    this.pieceId = pieceId;
    this.uniquePieceId = uniquePieceId;
    this.rotationId = rotationId;
    this.isFlipped = isFlipped;
    this.square = square;
    this.receptors = new HashSet<>();
    this.cells = new HashSet<>();
    for (int y = 0; y < square.length; y++) {
      for (int x = 0; x < square[0].length; x++) {
        if (square[y][x] == Square.RECEPTOR) {
          receptors.add(new YX(y, x));
        } else if (square[y][x] == Square.CELL) {
          cells.add(new YX(y, x));
        }
      }
    }
  }

  public int getPieceId() {
    return pieceId;
  }

  public int getUniquePieceId() {
    return uniquePieceId;
  }

  public int getRotationId() {
    return rotationId;
  }

  public boolean isFlipped() {
    return isFlipped;
  }

  public Set<YX> getReceptors() {
    return receptors;
  }

  public Set<YX> getCells() {
    return cells;
  }

  public Square[][] getSquares() {
    return square;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Piece)) {
      return false;
    }
    Piece that = (Piece) o;
    return Arrays.deepEquals(this.square, that.square);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(square);
  }

  @Override
  public String toString() {
    return String.format("id=%d:uniqueId=%d:rot=%d:flipped=%s\n",
        pieceId,
        uniquePieceId,
        rotationId,
        isFlipped)
        + blokus.Arrays.toString(square);
  }

  @Override
  public int compareTo(Piece piece) {
    return blokus.Arrays.compare(this.square, piece.square);
  }
}
