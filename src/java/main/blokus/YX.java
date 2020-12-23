package blokus;

import java.util.Objects;

class YX implements Comparable<YX> {
  int x;
  int y;

  public YX(int y, int x) {
    this.y = y;
    this.x = x;
  }

  public int hashCode() {
    return Objects.hash(x, y);
  }

  public boolean equals(Object o) {
    if (!(o instanceof YX)) {
      return false;
    }
    YX that = (YX) o;
    return this.x == that.x && this.y == that.y;
  }

  public String toString() {
    return "" + y + "," + x;
  }

  @Override
  public int compareTo(YX yx) {
    if (this.y != yx.y) {
      return Integer.compare(this.y, yx.y);
    }
    return Integer.compare(this.x, yx.x);
  }
}
