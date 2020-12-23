package blokus;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

public class Board implements Comparable<Board> {
  private static final boolean USE_MIRROR = false;

  private Logger logger = Logger.getLogger(Board.class.getName());

  private final Set<YX> receptors;
  private final char[][] board;

  public Board() {
    this.receptors = new TreeSet<>();
    this.receptors.add(new YX(0, 0));
    this.board = new char[40][40];
  }

  // copy constructor
  private Board(Set<YX> receptors, char[][] board) {
    this.receptors = receptors;
    this.board = board;
  }

  public Board copy() {
    char[][] newBoard = new char[board.length][];
    for(int i = 0; i < board.length; i++) {
      char[] row = board[i];
      int rowLength = row.length;
      newBoard[i] = new char[rowLength];
      System.arraycopy(row, 0, newBoard[i], 0, rowLength);
    }
    return new Board(new HashSet<>(receptors), newBoard);
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
    Set<YX> newReceptors = new HashSet<>();
    for (YX receptor : receptors) {
      //noinspection SuspiciousNameCombination
      newReceptors.add(new YX(receptor.x, receptor.y));
    }
    Map<Piece, List<YX>> newPlayLog = new TreeMap<>();
    return new Board(newReceptors, getBoardMirror());
  }

  public Set<YX> getReceptors() {
    return receptors;
  }

  public boolean canPlay(Piece piece, YX boardReceptor, YX pieceCell) {
    if (!isLegalMove(piece, boardReceptor, pieceCell)) {
      return false;
    }
    return true;
  }

  public void playPiece(Piece piece, YX boardReceptor, YX pieceCell) {
    int originX = boardReceptor.x - pieceCell.x;
    int originY = boardReceptor.y - pieceCell.y;
    Square[][] squares = piece.getSquares();
    for (int yy = 0; yy < squares.length; yy++) {
      for (int xx = 0; xx < squares[0].length; xx++) {
        int y = originY + yy;
        int x = originX + xx;
        switch (squares[yy][xx]) {
          case CELL -> {
            Preconditions.checkState(!(y < 0 || y >= board.length));
            Preconditions.checkState(!(x < 0 || x >= board[0].length));
            Preconditions.checkState(board[y][x] == 0);
            board[y][x] = 1;
            YX yx = new YX(y, x);
            receptors.remove(yx);
          }
          case RECEPTOR -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length) {
              YX yx = new YX(y, x);
              if (board[y][x] == 1) {
                receptors.remove(yx);
              } else if (board[y][x] == 0) {
                receptors.add(yx);
              }
            }
          }
          case ADJACENT -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && board[y][x] == 0) {
              board[y][x] = 2;
            }
          }
          case EMPTY -> {
          }
        }
      }
    }
  }

  private boolean isLegalMove(Piece piece, YX boardReceptor, YX pieceCell) {
    Set<YX> matedBoardReceptors = new HashSet<>();
    Set<YX> matedPieceReceptors = new HashSet<>();
    int originX = boardReceptor.x - pieceCell.x;
    int originY = boardReceptor.y - pieceCell.y;
    Square[][] squares = piece.getSquares();
    for (int yy = 0; yy < squares.length; yy++) {
      for (int xx = 0; xx < squares[0].length; xx++) {
        int y = originY + yy;
        int x = originX + xx;
        switch (squares[yy][xx]) {
          case CELL -> {
            if (y < 0 || y >= board.length) {
              return false;
            }
            if (x < 0 || x >= board[0].length) {
              return false;
            }
            if (board[y][x] > 0) {
              return false;
            }
            YX yx = new YX(y, x);
            if (receptors.contains(yx)) {
              matedBoardReceptors.add(yx);
            }
          }
          case RECEPTOR -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && board[y][x] == 1) {
              matedPieceReceptors.add(new YX(y, x));
            }
          }
          case ADJACENT -> {
            if (y >= 0 && y < board.length
                && x >= 0 && x < board[0].length
                && board[y][x] == 1) {
              return false;
            }
          }
          case EMPTY -> {
          }
        }
      }
    }

    boolean isFirstMove = boardReceptor.equals(new YX(0, 0));
    return isFirstMove
        ? !matedBoardReceptors.isEmpty()
        : !matedPieceReceptors.isEmpty();
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    int max = 18;
    for (int y = 0; y < max; y++) {
      for (int x = 0; x < max; x++) {
        if (board[y][x] == 1) {
          sb.append("B");
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
