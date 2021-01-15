package blokus;

import blokus.codec.GameCodec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedSet;
import java.util.logging.Logger;

public class GameIterator {
  private final Logger logger = Logger.getLogger(GameIterator.class.getName());
  private final PieceLibrary pieceLibrary;

  public static void main(String[] args) throws Exception {
    new GameIterator().run(new File(args[0]), new File(args[1]), new File(args[2]));
  }

  public GameIterator() {
    this.pieceLibrary = new PieceLibrary();
  }

  public void run(File input, File isolatedOutput, File interfereOutput) throws Exception {
    try (OutputStream isolatedOutputStream = new BufferedOutputStream(new FileOutputStream(isolatedOutput));
         OutputStream interfereOutputStream = new BufferedOutputStream(new FileOutputStream(interfereOutput));
         InputStream inputStream = new BufferedInputStream(new FileInputStream(input))) {
      iterate(inputStream, isolatedOutputStream, interfereOutputStream);
    }
  }

  public void iterate(InputStream inputStream,
      OutputStream isolatedOutputStream,
      OutputStream interfereOutputStream) throws IOException {
    GameCodec gameCodec = new GameCodec();
    int inputCount = 0;
    final int[] outputCount = { 0 };
    final int[] isolatedOutputCount = { 0 };
    while (true) {
      Proto.State state = Proto.State.parseDelimitedFrom(inputStream);
      if (state == null) {
        break;
      }
      inputCount++;
      Game game = new Game(4, pieceLibrary);
      gameCodec.decode(game, state, pieceLibrary);
      iterateGame(game, (newGame) -> {
        Proto.State newState = gameCodec.encode(game);
        if (newGame.getBoard().getMaxWidth() > 9 || newGame.getBoard().getMaxHeight() > 9) {
          newState.writeDelimitedTo(interfereOutputStream);
          outputCount[0]++;
        } else {
          newState.writeDelimitedTo(isolatedOutputStream);
          isolatedOutputCount[0]++;
        }
      });
    }

    logger.info("Read " + inputCount + " games");
    logger.info("Wrote " + outputCount[0] + " games which can interfere with other quads");
    logger.info("Wrote " + isolatedOutputCount[0] + " games which fit within the quad");
    logger.info("Wrote " + (outputCount[0] + isolatedOutputCount[0]) + " games");
  }

  interface GameCallback {
    void invoke(Game game) throws IOException;
  }

  private void iterateGame(Game game, GameCallback callback) throws IOException {
    int attempts = 0;
    Color color = Color.BLUE;
    SortedSet<Integer> pieces = game.getAvailablePieceIds(color);
    for (int pieceId : pieces) {
      for (Piece piece : pieceLibrary.getPiecePermutations(pieceId)) {
        // For every available rotation of every piece, try to make a new game.
        for (YX boardReceptor : game.getBoard().getReceptors(color)) {
          for (YX pieceCell : piece.getCells()) {
            attempts++;
            if (game.getBoard().canPlay(color, piece, boardReceptor, pieceCell)) {
              Game gameCopy = game.copy();
              gameCopy.playPiece(color, piece, boardReceptor, pieceCell);
              callback.invoke(gameCopy);
            }
          }
        }
      }
    }
  }
}
