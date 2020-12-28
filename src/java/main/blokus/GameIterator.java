package blokus;

import blokus.codec.GameCodec;
import com.google.common.base.Preconditions;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Logger;

public class GameIterator {
  private final Logger logger = Logger.getLogger(GameIterator.class.getName());
  private final PieceLibrary library;

  public static void main(String[] args) throws Exception {
    new GameIterator().run();
  }

  public GameIterator() {
    this.library = new PieceLibrary();
  }

  public void run() throws Exception {
    int depthLimit = 2; // allPieces.size();
    File output = new File("/tmp/depth-" + depthLimit + ".bin");
    iterate(depthLimit, output);
  }

  public void iterate(int depthLimit, File output) throws IOException {
    Game initialGame = new Game(1, library.getAllPieceIds());
    Set<Game> games = new HashSet<>();
    games.add(initialGame);

    for (int i = 0; i < depthLimit; ++i) {
      logger.info("Level=" + i + ", game set size=" + games.size());
      games = iterateGames(games, i);
    }

    logger.info("Final game size=" + games.size());

    GameCodec codec = new GameCodec();
    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(output))) {
      for (Game game : games) {
        Proto.State state = codec.encode(game);
        state.writeDelimitedTo(outputStream);
      }
    }
    printStats(games);
  }

  public void printStats(Set<Game> games) {
    Map<Integer, Set<Integer>> firstPieceToSecondUniqueId = new LinkedHashMap<>();
    for (Game game : games) {
      logger.info("\n" + game.getBoard() + "\n");
      List<Piece> piecesPlayed = game.getPlayLog().getPiecesPlayed(Color.BLUE);
      int initialPieceId = piecesPlayed.get(0).getPieceId();
      if (!firstPieceToSecondUniqueId.containsKey(initialPieceId)) {
        firstPieceToSecondUniqueId.put(initialPieceId, new HashSet<>(library.getAllUniquePieceIds()));
      }
      Set<Integer> uniques = firstPieceToSecondUniqueId.get(initialPieceId);
      uniques.remove(piecesPlayed.get(1).getUniquePieceId());
    }
    for (int i : firstPieceToSecondUniqueId.keySet()) {
      logger.info("1st move piece " + i + " was not able to play next unique ids " +
          firstPieceToSecondUniqueId.get(i));
    }
  }

  private Set<Game> iterateGames(Set<Game> games, int currentDepth) {
    Set<Game> gamesWithNPieces = new LinkedHashSet<>();

    int attempts = 0;
    int gameCount = 0;
    for (Game game : games) {
      SortedSet<Integer> pieces = game.getAvailablePieces();
      for (int pieceId : pieces) {
        for (Piece piece : library.getPiecePermutations(pieceId)) {
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
    return gamesWithNPieces;
  }
}
