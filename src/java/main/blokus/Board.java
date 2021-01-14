package blokus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

public class Board implements Comparable<Board> {
  private static final boolean USE_MIRROR = false;

  private final Logger logger = Logger.getLogger(Board.class.getName());
  private final Map<Color, Set<YX>> receptors;
  private final Map<Color, YX> initialReceptors;
  private final char[][] board;

  private int maxY;
  private int maxX;

  public Board() {
    this(new BoardConfiguration());
  }

  public Board(BoardConfiguration config) {
    this.initialReceptors = config.getInitialReceptors();
    this.receptors = new HashMap<>();
    for (Color color : Color.values()) {
      this.receptors.put(color, new TreeSet<>());
      this.receptors.get(color).add(initialReceptors.get(color));
    }
    this.board = new char[config.getHeight()][config.getWidth()];
    this.maxX = 0;
    this.maxY = 0;
  }

  // copy constructor
  Board(Map<Color, YX> initialReceptors, Map<Color, Set<YX>> receptors, char[][] board, int maxX, int maxY) {
    this.initialReceptors = initialReceptors;
    this.receptors = receptors;
    this.board = board;
    this.maxX = maxX;
    this.maxY = maxY;
  }

  public Board copy() {
    char[][] newBoard = new char[board.length][];
    for(int i = 0; i < board.length; i++) {
      char[] row = board[i];
      int rowLength = row.length;
      newBoard[i] = new char[rowLength];
      System.arraycopy(row, 0, newBoard[i], 0, rowLength);
    }
    Map<Color, Set<YX>> newReceptors = new HashMap<>();
    for (Color color : Color.values()) {
      newReceptors.put(color, new TreeSet<>(receptors.get(color)));
    }
    return new Board(initialReceptors, newReceptors, newBoard, maxX, maxY);
  }

  private char[][] getBoardMirror() {
    char[][] newBoard = new char[board.length][board[0].length];
    for (int y = 0; y < board.length; y++) {
      for (int x = 0; x < board[0].length; x++) {
        newBoard[x][y] = board[y][x];
      }
    }
    return newBoard;
  }

  public Board mirror() {
    Map<Color, Set<YX>> newReceptors = new HashMap<>();
    for (Color color : Color.values()) {
      newReceptors.put(color, new TreeSet<>());
      for (YX receptor : receptors.get(color)) {
        //noinspection SuspiciousNameCombination
        newReceptors.get(color).add(new YX(receptor.x, receptor.y));
      }
    }
    Map<Piece, List<YX>> newPlayLog = new TreeMap<>();
    //noinspection SuspiciousNameCombination
    return new Board(initialReceptors, newReceptors, getBoardMirror(), maxY, maxX);
  }

  public Set<YX> getReceptors(Color color) {
    return receptors.get(color);
  }

  public void playPiece(Color color, Piece piece, YX boardReceptor, YX pieceCell) {
    YX origin = boardReceptor.minus(pieceCell);
    Square[][] squares = piece.getSquares();
    for (int yy = 0; yy < squares.length; yy++) {
      for (int xx = 0; xx < squares[0].length; xx++) {
        int y = origin.y + yy;
        int x = origin.x + xx;
        YX yx = new YX(y, x);
        switch (squares[yy][xx]) {
          case CELL -> {
            Preconditions.checkState(!(y < 0 || y >= board.length));
            Preconditions.checkState(!(x < 0 || x >= board[0].length));
            Preconditions.checkState(!isACellOfAnyColor(yx),
                "Expected " + yx + " to be blank\n" + toString());
            board[y][x] = color.getCellRepresentation();
            receptors.get(color).remove(yx);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
          }
          case RECEPTOR -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length) {
              if (board[y][x] == color.getCellRepresentation()) {
                receptors.get(color).remove(yx);
                // TODO: isn't this wrong?  As long as the square is not adjacent it should be a receptor?
              } else if (board[y][x] == 0) {
                receptors.get(color).add(yx);
              }
            }
          }
          case ADJACENT -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && !isACellOfAnyColor(yx)) {
              board[y][x] |= color.getAdjacentBitmap();
              // TODO: test this fixes receptor bug.
              receptors.get(color).remove(yx);
            }
          }
          case EMPTY -> {
          }
        }
      }
    }
  }

  public boolean canPlay(Color color, Piece piece, YX boardReceptor, YX pieceCell) {
    Set<YX> matedBoardReceptors = new HashSet<>();
    Set<YX> matedPieceReceptors = new HashSet<>();
    YX origin = boardReceptor.minus(pieceCell);
    Square[][] squares = piece.getSquares();
    for (int yy = 0; yy < squares.length; yy++) {
      for (int xx = 0; xx < squares[0].length; xx++) {
        int y = origin.y + yy;
        int x = origin.x + xx;
        YX yx = new YX(y, x);
        switch (squares[yy][xx]) {
          case CELL -> {
            if (y < 0 || y >= board.length) {
              return false;
            }
            if (x < 0 || x >= board[0].length) {
              return false;
            }
            if (isACellOfAnyColor(yx)
                || isAdjacentToColor(yx, color)) {
              return false;
            }
            if (receptors.get(color).contains(yx)) {
              matedBoardReceptors.add(yx);
            }
          }
          case RECEPTOR -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && board[y][x] == color.getCellRepresentation()) {
              matedPieceReceptors.add(yx);
            }
          }
          case ADJACENT -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && board[y][x] == color.getCellRepresentation()) {
              return false;
            }
          }
          case EMPTY -> {
          }
        }
      }
    }

    boolean isFirstMove = boardReceptor.equals(initialReceptors.get(color));
    return isFirstMove
        ? !matedBoardReceptors.isEmpty()
        : !matedPieceReceptors.isEmpty();
  }

  boolean isACellOfAnyColor(YX yx) {
    return Color.REPRESENTATION_TO_COLOR.containsKey(board[yx.y][yx.x]);
  }

  public Map<Color, Set<Integer>> getCellMap() {
    Map<Color, Set<Integer>> result = new HashMap<>();
    for (Color color : Color.values()) {
      result.put(color, new TreeSet<>());
    }
    for (int y = 0; y < board.length; y++) {
      for (int x = 0; x < board[0].length; x++) {
        if (Color.REPRESENTATION_TO_COLOR.containsKey(board[y][x])) {
          result.get(Color.REPRESENTATION_TO_COLOR.get(board[y][x])).add(new YX(y, x).encode());
        }
      }
    }
    return result;
  }

  boolean isAdjacentToColor(YX yx, Color color) {
    return ((char) (board[yx.y][yx.x] & color.getAdjacentBitmap())) > 0;
  }

  public int getMaxWidth() {
    return maxX;
  }

  public int getMaxHeight() {
    return maxY;
  }

  @Override
  public int hashCode() {
    return USE_MIRROR ? hashCodeMirror() : hashCodeNoMirror();
  }

  public int hashCodeNoMirror() {
    return Arrays.deepHashCode(board);
  }

  public int hashCodeMirror() {
    return Math.min(
        Arrays.deepHashCode(board),
        Arrays.deepHashCode(getBoardMirror()));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Board)) {
      return false;
    }
    return USE_MIRROR ? equalsMirror((Board) o) : equalsNoMirror((Board) o);
  }

  public boolean equalsNoMirror(Board that) {
    return Arrays.deepEquals(this.board, that.board);
  }

  public boolean equalsMirror(Board that) {
    if (Arrays.deepEquals(this.board, that.board)) {
      return true;
    }
    return Arrays.deepEquals(this.getBoardMirror(), that.board);
  }

  private static final int PRETTY_PADDING = 2;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    int localMaxY = Math.min(board.length - 1, maxY + PRETTY_PADDING);
    int localMaxX = Math.min(board[0].length - 1, maxX + PRETTY_PADDING);
    for (int y = 0; y < localMaxY; y++) {
      for (int x = 0; x < localMaxX; x++) {
        if (Color.REPRESENTATION_TO_COLOR.containsKey(board[y][x])) {
          sb.append(board[y][x]);
        } else {
          sb.append(".");
//        } else {
//          sb.append((char) (board[y][x] + '0'));
        }
      }
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }

  @Override
  public int compareTo(Board board) {
    return USE_MIRROR ? compareToMirror(board) : compareToNoMirror(board);
  }

  public int compareToNoMirror(Board board) {
    return blokus.Arrays.compare(this.board, board.board);
  }

  public int compareToMirror(Board board) {
    int result = blokus.Arrays.compare(this.board, board.board);
    if (result == 0) {
      return 0;
    }
    if (blokus.Arrays.compare(this.getBoardMirror(), board.board) == 0) {
      return 0;
    } else {
      return result;
    }
  }
}
