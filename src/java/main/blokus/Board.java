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
import java.util.logging.Logger;

public class Board implements Comparable<Board> {
  private static final boolean USE_MIRROR = false;

  private Logger logger = Logger.getLogger(Board.class.getName());

  private final Map<Piece, List<YX>> playLog;
  private final Set<YX> receptors;
  private final int[][] board;

  public Board() {
    this.receptors = new HashSet<>();
    this.receptors.add(new YX(0, 0));
    this.playLog = new LinkedHashMap<>();
    this.board = new int[40][40];
  }

  // copy constructor
  private Board(Set<YX> receptors, int[][] board, Map<Piece, List<YX>> playLog) {
    this.receptors = receptors;
    this.board = board;
    this.playLog = playLog;
  }

  public Board copy() {
    int[][] newBoard = new int[board.length][];
    for(int i = 0; i < board.length; i++) {
      int[] row = board[i];
      int rowLength = row.length;
      newBoard[i] = new int[rowLength];
      System.arraycopy(row, 0, newBoard[i], 0, rowLength);
    }
    Map<Piece, List<YX>> newPlayLog = new LinkedHashMap<>();
    for (Piece piece : playLog.keySet()) {
      newPlayLog.put(piece, new ArrayList<>(playLog.get(piece)));
    }
    return new Board(new HashSet<>(receptors), newBoard, newPlayLog);
  }

  private int[][] getBoardMirror() {
    int[][] newBoard = new int[board.length][board[0].length];
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
    for (Map.Entry<Piece, List<YX>> entry : playLog.entrySet()) {
      newPlayLog.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return new Board(newReceptors, getBoardMirror(), newPlayLog);
  }

  public Set<Piece> getPiecesPlayedInOrder() {
    return playLog.keySet();
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
    playPieceInternal(piece, boardReceptor, pieceCell);
    List<YX> log = new ArrayList<>();
    log.add(boardReceptor);
    log.add(pieceCell);
    playLog.put(piece, log);
  }

  private void playPieceInternal(Piece piece, YX boardReceptor, YX pieceCell) {
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
    boolean result = isFirstMove
        ? !matedBoardReceptors.isEmpty()
        : !matedPieceReceptors.isEmpty();
    return result;
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
    //this.receptors.equals(that.receptors)
    //        &&
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (Piece piece : playLog.keySet()) {
      sb.append("id=")
          .append(piece.getPieceId())
          .append(":uniqueid=")
          .append(piece.getUniquePieceId())
          .append(":rot=")
          .append(piece.getRotationId())
          .append(":flipped=")
          .append(Boolean.toString(piece.isFlipped()).substring(0, 1))
          .append(":cell=")
          .append(playLog.get(piece).get(1))
          .append(":at=")
          .append(playLog.get(piece).get(0))
          .append(System.lineSeparator());
    }

    int max = 18;
    for (int y = 0; y < max; y++) {
      for (int x = 0; x < max; x++) {
        if (board[y][x] == 1) {
          sb.append("#");
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

  public int compareToPlayLoag(Board board) {
    if (this.playLog.size() != board.playLog.size()) {
      return Integer.compare(this.playLog.size(), board.playLog.size());
    }
    return compare(this.playLog, board.playLog);
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

  private int compare(Map<Piece, List<YX>> a, Map<Piece, List<YX>> b) {
    Iterator<Map.Entry<Piece, List<YX>>> aa = a.entrySet().iterator();
    Iterator<Map.Entry<Piece, List<YX>>> bb = b.entrySet().iterator();
    for (int i = 0; i < a.size(); i++) {
      Map.Entry<Piece, List<YX>> aaa = aa.next();
      Map.Entry<Piece, List<YX>> bbb = bb.next();
      if (!aaa.getKey().equals(bbb.getKey())) {
        return aaa.getKey().compareTo(bbb.getKey());
      }
      if (!aaa.getValue().equals(bbb.getValue())) {
        return compare(aaa.getValue(), bbb.getValue());
      }
    }
    return 0;
  }

  private int compare(List<YX> a, List<YX> b) {
    if (a.size() != b.size()) {
      return Integer.compare(a.size(), b.size());
    }
    for (int j = 0; j < a.size(); j++) {
      if (!a.get(j).equals(b.get(j))) {
        return a.get(j).compareTo(b.get(j));
      }
    }
    return 0;
  }
}
