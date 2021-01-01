package blokus;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

  public static class TestBoardConfiguration extends BoardConfiguration {
    private static final Map<Color, YX> INITIAL_RECEPTORS = ImmutableMap.of(
        Color.BLUE, new YX(0, 0),
        Color.YELLOW, new YX(0, 1),
        Color.RED, new YX(0, 2),
        Color.GREEN, new YX(0, 3));

    @Override
    public Map<Color, YX> getInitialReceptors() {
      return INITIAL_RECEPTORS;
    }
  }

  private final PieceLibrary pieceLibrary = new PieceLibrary();

  @Test
  public void testAdjacentColors() {
    Board board = new Board(new TestBoardConfiguration());
    assertTrue(board.canPlay(Color.BLUE, pieceLibrary.getPieceByUniqueId(0),
        new YX(0, 0), new YX(1, 1)));
    board.playPiece(Color.BLUE, pieceLibrary.getPieceByUniqueId(0),
        new YX(0, 0), new YX(1, 1));
    assertTrue(board.isAdjacentToColor(new YX(1, 0), Color.BLUE));
    assertTrue(board.isAdjacentToColor(new YX(0, 1), Color.BLUE));

    assertTrue(board.canPlay(Color.YELLOW, pieceLibrary.getPieceByUniqueId(2),
        new YX(0, 1), new YX(1, 1)));
    board.playPiece(Color.YELLOW, pieceLibrary.getPieceByUniqueId(2),
        new YX(0, 1), new YX(1, 1));
    assertTrue(board.isAdjacentToColor(new YX(1, 0), Color.BLUE));
    assertTrue(board.isAdjacentToColor(new YX(1, 0), Color.YELLOW));

    assertFalse(board.canPlay(Color.YELLOW, pieceLibrary.getPieceByUniqueId(0),
        new YX(0, 2), new YX(1,1)));
    assertFalse(board.canPlay(Color.GREEN, pieceLibrary.getPieceByUniqueId(0),
        new YX(0, 2), new YX(1,1)));
    assertTrue(board.canPlay(Color.RED, pieceLibrary.getPieceByUniqueId(0),
        new YX(0, 2), new YX(1,1)));
  }
}
