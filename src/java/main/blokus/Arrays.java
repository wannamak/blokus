package blokus;

public class Arrays {
  public static Square[][] copy(Square[][] array) {
    Square[][] duplicate = new Square[array.length][array[0].length];
    for (int y = 0; y < array.length; y++) {
      for (int x = 0; x < array[0].length; x++) {
        duplicate[y][x] = array[y][x];
      }
    }
    return duplicate;
  }

  public static Square[][] rotate(Square[][] array) {
    Square[][] skew = new Square[array[0].length][array.length];
    for (int y = 0; y < array.length; y++) {
      for (int x = 0; x < array[0].length; x++) {
        skew[x][y] = array[array.length - 1 - y][x];
      }
    }
    return skew;
  }

  public static void flipVertically(Square[][] array) {
    for (int y = 0; y < array.length / 2; y++) {
      for (int x = 0; x < array[0].length; x++) {
        Square tmp = array[y][x];
        array[y][x] = array[array.length - 1 - y][x];
        array[array.length - 1 - y][x] = tmp;
      }
    }
  }

  public static int compare(int[][] a, int[][] b) {
    for (int y = 0; y < a.length; y++) {
      if (!java.util.Arrays.equals(a[y], b[y])) {
        return java.util.Arrays.compare(a[y], b[y]);
      }
    }
    return 0;
  }

  public static int compare(Square[][] a, Square[][] b) {
    for (int y = 0; y < a.length; y++) {
      if (!java.util.Arrays.equals(a[y], b[y])) {
        return java.util.Arrays.compare(a[y], b[y]);
      }
    }
    return 0;
  }

  public static String toString(Square[][] square) {
    StringBuilder sb = new StringBuilder();
    for (int y = 0; y < square.length; y++) {
      for (int x = 0; x < square[0].length; x++) {
        sb.append(square[y][x].getRepresentation());
      }
      if (y < square.length - 1) {
        sb.append(System.lineSeparator());
      }
    }
    return sb.toString();
  }
}
