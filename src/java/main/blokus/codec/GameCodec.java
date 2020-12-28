package blokus.codec;

import blokus.Color;
import blokus.Game;
import blokus.Piece;
import blokus.PieceLibrary;
import blokus.PlayLog;
import blokus.Proto;
import blokus.YX;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameCodec {
  public Proto.State encode(Game game) {
    Proto.State.Builder builder = Proto.State.newBuilder();
    for (PlayLog.PlayLogEntry entry : game.getPlayLog().getEntries()) {
      Proto.MoveList.Builder move = addMoveList(builder, entry.color);
      move.addUniqueId(entry.piece.getUniquePieceId());
      move.addBoardReceptor(entry.boardReceptor.encode());
      move.addPieceCell(entry.pieceCell.encode());
    }
    return builder.build();
  }

  private Proto.MoveList.Builder addMoveList(Proto.State.Builder builder, Color color) {
    switch (color) {
      case BLUE -> { return builder.addBlueBuilder(); }
      case YELLOW -> { return builder.addYellowBuilder(); }
      case RED -> { return builder.addRedBuilder(); }
      case GREEN -> { return builder.addGreenBuilder(); }
    }
    throw new IllegalStateException("Wrong color " + color);
  }

  public void decode(Game game, Proto.State state, PieceLibrary pieceLibrary) {
    int max = Ints.max(state.getBlueCount(), state.getYellowCount(),
        state.getRedCount(), state.getGreenCount());
    for (int i = 0; i < max; i++) {
      if (i < state.getBlueCount()) {
        decodeMoveList(game, state.getBlue(i), pieceLibrary);
      }
      if (i < state.getYellowCount()) {
        decodeMoveList(game, state.getYellow(i), pieceLibrary);
      }
      if (i < state.getRedCount()) {
        decodeMoveList(game, state.getRed(i), pieceLibrary);
      }
      if (i < state.getGreenCount()) {
        decodeMoveList(game, state.getGreen(i), pieceLibrary);
      }
    }
  }

  private void decodeMoveList(Game game, Proto.MoveList moveList, PieceLibrary pieceLibrary) {
    for (int i = 0; i < moveList.getUniqueIdCount(); i++) {
      Piece piece = pieceLibrary.getPieceByUniqueId(moveList.getUniqueId(i));
      YX boardReceptor = YX.decode(moveList.getBoardReceptor(i));
      YX pieceCell = YX.decode(moveList.getPieceCell(i));
      game.playPiece(piece, boardReceptor, pieceCell);
    }
  }
}
