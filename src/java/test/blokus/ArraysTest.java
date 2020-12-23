package blokus;

import org.junit.jupiter.api.Test;

import static blokus.Square.CELL;
import static blokus.Square.EMPTY;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArraysTest {
  @Test
  public void testFlipVerticallyOddRows() {
    Square[][] actual = new Square[][] {
        new Square[] { CELL, EMPTY, CELL },
        new Square[] { CELL, CELL, EMPTY },
        new Square[] { EMPTY, EMPTY, EMPTY }
    };
    Arrays.flipVertically(actual);
    assertTrue(java.util.Arrays.deepEquals(
        new Square[][] {
          new Square[] { EMPTY, EMPTY, EMPTY },
          new Square[] { CELL, CELL, EMPTY },
          new Square[] { CELL, EMPTY, CELL }
        },
        actual));
  }

  @Test
  public void testFlipVerticallyEvenRows() {
    Square[][] actual = new Square[][] {
        new Square[] { CELL, EMPTY, CELL },
        new Square[] { CELL, CELL, EMPTY },
        new Square[] { CELL, EMPTY, EMPTY },
        new Square[] { EMPTY, EMPTY, EMPTY }
    };
    Arrays.flipVertically(actual);
    assertTrue(java.util.Arrays.deepEquals(
        new Square[][] {
            new Square[] { EMPTY, EMPTY, EMPTY },
            new Square[] { CELL, EMPTY, EMPTY },
            new Square[] { CELL, CELL, EMPTY },
            new Square[] { CELL, EMPTY, CELL }
        },
        actual));
  }

  @Test
  public void testRotate() {
    Square[][] actual = new Square[][] {
        new Square[] { CELL, EMPTY, CELL },
        new Square[] { CELL, CELL, EMPTY },
        new Square[] { EMPTY, EMPTY, EMPTY }
    };
    actual = Arrays.rotate(actual);
    assertTrue(java.util.Arrays.deepEquals(
        new Square[][] {
            new Square[] { EMPTY, CELL, CELL },
            new Square[] { EMPTY, CELL, EMPTY },
            new Square[] { EMPTY, EMPTY, CELL }
        },
        actual));
  }
}
