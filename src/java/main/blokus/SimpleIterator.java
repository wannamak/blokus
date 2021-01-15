package blokus;

import blokus.codec.GameCodec;
import util.VarKey;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Logger;

public class SimpleIterator {
  private final Logger logger = Logger.getLogger(SimpleIterator.class.getName());
  private final PieceLibrary library;

  public static void main(String[] args) throws Exception {
    new SimpleIterator().run();
  }

  public SimpleIterator() {
    this.library = new PieceLibrary();
  }

  public void run() throws Exception {
    int depthLimit = 2; // allPieces.size();
    File output = new File("/tmp/depth-" + depthLimit + ".bin");
    iterate(depthLimit, output);
  }

  public void iterate(int depthLimit, File output) throws IOException {
    Game initialGame = new Game(1, library);
    Set<Game> games = new HashSet<>();
    games.add(initialGame);

    Set<Game> nextGames;
    for (int i = 0; i < depthLimit; ++i) {
      logger.info("Level=" + i + ", game set size=" + games.size());
      int count = 0;
      nextGames = new LinkedHashSet<>();
      for (Game game : games) {
        Set<Game> result = iterateGame(game);
        logger.info("--] game " + count++ + " [-----------"
            + " yielded " + result.size() + " games");
        nextGames.addAll(result);
      }
      games = nextGames;
    }

    logger.info("Total next games=" + games.size());
    GameCodec codec = new GameCodec();
    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(output))) {
      for (Game game : games) {
        Proto.State state = codec.encode(game);
        state.writeDelimitedTo(outputStream);
      }
    }
//    printStats(games);
  }

  public void printStats(Set<Game> games) {
    Map<VarKey, Integer> counts = new HashMap<>();
    Map<Integer, Integer> pieceIdToCount = new LinkedHashMap<>();
    Map<Integer, Set<Integer>> firstPieceToSecondUniqueId = new LinkedHashMap<>();
    for (Game game : games) {
      List<Piece> piecesPlayed = game.getPlayLog().getPiecesPlayed(Color.BLUE);
      int initialPieceId = piecesPlayed.get(0).getPieceId();
      int secondPieceId = piecesPlayed.get(1).getPieceId();
      VarKey key = new VarKey(initialPieceId, secondPieceId);
      counts.put(key, counts.getOrDefault(key, 0) + 1);
      int gameId = pieceIdToCount.getOrDefault(initialPieceId, 0) + 1;
      if (initialPieceId == 9 && secondPieceId == 20) {
        logger.info("This is game # " + gameId + "\n" + game.getBoard() + "\n");
      }
      pieceIdToCount.put(initialPieceId, gameId);
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
    int total = 0;
    for (int i : pieceIdToCount.keySet()) {
      logger.info("1st move piece id " + i + " yielded " + pieceIdToCount.get(i) + " games");
      total += pieceIdToCount.get(i);
    }
    for (int fp : library.getAllPieceIds()) {
      boolean printed = false;
      for (int sp : library.getAllPieceIds()) {
        VarKey key = new VarKey(fp, sp);
        if (printed) {
          System.out.print(",");
        }
        if (fp != sp) {
          System.out.print("" + counts.get(key));
        }
        printed = true;
      }
      System.out.println();
    }
    logger.info("Total games: " + total);
  }

  private Set<Game> iterateGame(Game game) {
    Color color = Color.BLUE;
    Set<Game> gamesWithNPieces = new LinkedHashSet<>();
    game.iterateAvailableMoves((piece, boardReceptor, pieceCell) -> {
      Game gameCopy = game.copy();
      gameCopy.playPiece(color, piece, boardReceptor, pieceCell);
      gamesWithNPieces.add(gameCopy);
    });
    return gamesWithNPieces;
  }
}
