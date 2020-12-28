package blokus;

import blokus.codec.GameCodec;
import com.google.protobuf.CodedInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class PrintGames {
  private final Logger logger = Logger.getLogger(GameIterator.class.getName());
  private final PieceLibrary pieceLibrary;

  public static void main(String args[]) throws Exception {
    new PrintGames().run();
  }

  public PrintGames() {
    this.pieceLibrary = new PieceLibrary();
  }

  public void run() throws Exception {
    File file = new File("/tmp/depth-2.bin");
    try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
      GameCodec gameCodec = new GameCodec();
      while (true) {
        Proto.State state = Proto.State.parseDelimitedFrom(input);
        if (state == null) {
          break;
        }
        Game game = new Game(4, pieceLibrary.getAllPieceIds());
        gameCodec.decode(game, state, pieceLibrary);
        logger.info(game.toString());
      }
    }
  }
}
