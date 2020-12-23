package blokus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Logger;

public class BlokusSolver {
  private Logger logger = Logger.getLogger(BlokusSolver.class.getName());

  public static void main(String[] args) throws Exception {
    new BlokusSolver().run();
  }

  public void run() throws Exception {
    Map<Integer, Set<Piece>> pieces = new PieceLibrarian().generate();
    iterate(pieces);


//    Game game = new Game(4, pieces);
//    while (!game.hasWinner()) {
//
//    }
  }

  public void iterate(Map<Integer, Set<Piece>> allPieces) {
    logger.info("Iterating with " + allPieces.size() + " initial pieces");

    Game initialGame = new Game(1, allPieces.keySet());
    Set<Game> games = new HashSet<>();
    games.add(initialGame);
    int limit = 2; // allPieces.size();

    for (int i = 0; i < limit; ++i) {
      logger.info("Level=" + i + ", game set size=" + games.size());
      games = iterateGames(games, allPieces, i);
    }

    logger.info("Final game size=" + games.size());
    Map<Integer, Set<Integer>> firstPieceToSecondUniqueId = new LinkedHashMap<>();
    for (Game game : games) {
      logger.info("\n" + game.getBoard() + "\n");
      List<Piece> piecesPlayed = new ArrayList<>(game.getPiecesPlayedInOrder());
      int initialPieceId = piecesPlayed.get(0).getPieceId();
      if (!firstPieceToSecondUniqueId.containsKey(initialPieceId)) {
        firstPieceToSecondUniqueId.put(initialPieceId, getPopulated());
      }
      Set<Integer> uniques = firstPieceToSecondUniqueId.get(initialPieceId);
      uniques.remove(piecesPlayed.get(1).getUniquePieceId());
    }
    for (int i : firstPieceToSecondUniqueId.keySet()) {
      logger.info("1st move piece " + i + " was not able to play next unique ids " +
          firstPieceToSecondUniqueId.get(i));
    }
  }

  private Set<Integer> getPopulated() {
    Set<Integer> result = new LinkedHashSet<>();
    for (int i = 0; i < 91; i++) {
      result.add(i);
    }
    return result;
  }

  private Set<Game> iterateGames(Set<Game> games, Map<Integer, Set<Piece>> allPieces, int depth) {
    Set<Game> gamesWithNPieces = new LinkedHashSet<>();

    int attempts = 0;
    int gameCount = 0;
    for (Game game : games) {
      SortedSet<Integer> pieces = game.getAvailablePieces();
      for (int pieceId : pieces) {
        for (Piece piece : allPieces.get(pieceId)) {
          // For every available rotation of every piece, try to make a new game.
          for (YX boardReceptor : game.getBoard().getReceptors()) {
            for (YX pieceCell : piece.getCells()) {
              attempts++;
              if (game.getBoard().canPlay(piece, boardReceptor, pieceCell)) {
                logger.fine("POSITIVE Game=" + gameCount + " piece=" + piece.getPieceId() + " Rotation=" + piece.getRotationId() +
                    " boardReceptor=" + boardReceptor + " pieceCell=" + pieceCell);
                Game gameCopy = game.copy();
                gameCopy.playPiece(piece, boardReceptor, pieceCell);
                gamesWithNPieces.add(gameCopy);
              } else {
                logger.fine("NEGATIVE Game=" + gameCount + " piece=" + piece.getPieceId() + " Rotation=" + piece.getRotationId() +
                    " boardReceptor=" + boardReceptor + " pieceCell=" + pieceCell);
              }
            }
          }
        }
      }
      gameCount++;
    }
    logger.info("------------" + attempts + " attempts yielded " + gamesWithNPieces.size() + " games");
    if (false || depth == 4) {
      for (Game game : gamesWithNPieces) {
        logger.info("\n" + game.getBoard() + "\n");
      }
    }
    return gamesWithNPieces;
  }
}
