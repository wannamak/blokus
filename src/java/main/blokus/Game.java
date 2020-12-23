package blokus;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Game implements Comparable<Game> {
  private final Map<Color, SortedSet<Integer>> pieceIds;
  private final Map<Piece, List<YX>> playLog;
  private final int numPlayers;
  private final Board board;

  private Color currentPlayer;

  public Game(int numPlayers, Set<Integer> allPieceIds) {
    this.numPlayers = numPlayers;
    this.pieceIds = new HashMap<>();
    int colorIndex = 0;
    for (int i = 0; i < numPlayers; i++) {
      SortedSet<Integer> playerPieceIds = new TreeSet<>(allPieceIds);
      pieceIds.put(Color.values()[colorIndex++], playerPieceIds);
    }
    this.currentPlayer = Color.values()[0];
    this.board = new Board();
    this.playLog = new LinkedHashMap<>();
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public Color getCurrentPlayer() {
    return currentPlayer;
  }

  private Game(Map<Color, SortedSet<Integer>> pieceIds, Map<Piece, List<YX>> playLog,
      int numPlayers, Color currentPlayer, Board board) {
    this.pieceIds = pieceIds;
    this.playLog = playLog;
    this.numPlayers = numPlayers;
    this.currentPlayer = currentPlayer;
    this.board = board;
  }

  public boolean hasWinner() {
    return true;
  }

  public void playPiece(Piece piece, YX boardReceptor, YX pieceCell) {
    board.playPiece(piece, boardReceptor, pieceCell);
    removePiece(piece.getPieceId());
    List<YX> log = new ArrayList<>();
    log.add(boardReceptor);
    log.add(pieceCell);
    playLog.put(piece, log);
  }

  public void removePiece(int pieceId) {
    Preconditions.checkState(pieceIds.get(currentPlayer).remove(pieceId));
  }

  public SortedSet<Integer> getAvailablePieces() {
    return pieceIds.get(currentPlayer);
  }

  public Set<Piece> getPiecesPlayedInOrder() {
    return playLog.keySet();
  }

  public Board getBoard() {
    return board;
  }

  private Color getNextPlayer() {
    int currentColorIndex = Arrays.asList(Color.values()).indexOf(currentPlayer);
    return Color.values()[currentColorIndex == numPlayers - 1 ? 0 : currentColorIndex + 1];
  }

  public Game copy() {
    Map<Color, SortedSet<Integer>> newPieces = new HashMap<>();
    for (Color color : pieceIds.keySet()) {
      newPieces.put(color, new TreeSet<>(pieceIds.get(color)));
    }
    Map<Piece, List<YX>> newPlayLog = new LinkedHashMap<>();
    for (Piece piece : playLog.keySet()) {
      newPlayLog.put(piece, new ArrayList<>(playLog.get(piece)));
    }
    return new Game(newPieces, playLog, numPlayers, currentPlayer, board.copy());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    //sb.append("current=" + currentPlayer + ", piecesAvail=" + pieceIds.get(currentPlayer));
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
    sb.append(board);
    return sb.toString();
  }

  @Override
  public int compareTo(Game game) {
    return this.board.compareTo(game.board);
  }

  @Override
  public int hashCode() {
    return board.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Game)) {
      return false;
    }
    return this.board.equals(((Game) obj).board);
  }

  public int badcompareTo(Game game) {
    if (this.numPlayers != game.numPlayers) {
      return Integer.compare(this.numPlayers, game.numPlayers);
    }
    if (this.currentPlayer != game.currentPlayer) {
      return currentPlayer.compareTo(game.currentPlayer);
    }
    for (Color color : Color.values()) {
      if (!this.pieceIds.get(color).equals(game.pieceIds.get(color))) {
         return this.pieceIds.get(color).toString().compareTo(
             game.pieceIds.get(color).toString());
      }
    }
    return this.board.compareTo(game.board);
  }

  private int comparePlayLogs(Map<Piece, List<YX>> a, Map<Piece, List<YX>> b) {
    Iterator<Map.Entry<Piece, List<YX>>> aa = a.entrySet().iterator();
    Iterator<Map.Entry<Piece, List<YX>>> bb = b.entrySet().iterator();
    for (int i = 0; i < a.size(); i++) {
      Map.Entry<Piece, List<YX>> aaa = aa.next();
      Map.Entry<Piece, List<YX>> bbb = bb.next();
      if (!aaa.getKey().equals(bbb.getKey())) {
        return aaa.getKey().compareTo(bbb.getKey());
      }
      if (!aaa.getValue().equals(bbb.getValue())) {
        return comparePlayLogLists(aaa.getValue(), bbb.getValue());
      }
    }
    return 0;
  }

  private int comparePlayLogLists(List<YX> a, List<YX> b) {
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
