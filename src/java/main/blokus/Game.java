package blokus;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Game implements Comparable<Game> {
  private Map<Color, SortedSet<Integer>> pieceIds;
  private int numPlayers;
  private Color currentPlayer;
  private Board board;

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
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public Color getCurrentPlayer() {
    return currentPlayer;
  }

  private Game(Map<Color, SortedSet<Integer>> pieceIds, int numPlayers, Color currentPlayer, Board board) {
    this.pieceIds = pieceIds;
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
  }

  public void removePiece(int pieceId) {
    Preconditions.checkState(pieceIds.get(currentPlayer).remove(pieceId));
  }

  public SortedSet<Integer> getAvailablePieces() {
    return pieceIds.get(currentPlayer);
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
    return new Game(newPieces, numPlayers, currentPlayer, board.copy());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("current=" + currentPlayer + ", piecesAvail=" + pieceIds.get(currentPlayer));
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


}
