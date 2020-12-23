package blokus.codec;

import blokus.Game;
import blokus.Proto;

import java.util.HashSet;

public class GameCodec {
  public Proto.State encode(Game game) {
    Proto.State.Builder builder = Proto.State.newBuilder();
    return builder.build();
  }

  public Game decode(Proto.State state) {
    Game game = new Game(0, new HashSet<>());
    return game;
  }
}
