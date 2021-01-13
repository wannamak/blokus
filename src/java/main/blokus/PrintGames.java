package blokus;

import blokus.codec.GameCodec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class PrintGames {
  private final Logger logger = Logger.getLogger(SimpleIterator.class.getName());
  private final PieceLibrary pieceLibrary;

  public static void main(String args[]) throws Exception {
    new PrintGames().run(new File(args[0]));
  }

  public PrintGames() {
    this.pieceLibrary = new PieceLibrary();
  }

  public void run(File inputFile) throws Exception {
    try (InputStream input = new BufferedInputStream(new FileInputStream(inputFile))) {
      GameCodec gameCodec = new GameCodec();
      int count = 0;
      while (true) {
        Proto.State state = Proto.State.parseDelimitedFrom(input);
        if (state == null) {
          break;
        }
        Game game = new Game(4, pieceLibrary);
        gameCodec.decode(game, state, pieceLibrary);
        logger.info(game.toString());
        count++;
      }
      logger.info("Total games: " + count);
    }
  }
}
