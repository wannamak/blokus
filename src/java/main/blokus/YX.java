package blokus;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Objects;

/**
 * Sorted by y then x.
 */
public class YX implements Comparable<YX> {
  public final int x;
  public final int y;

  public static final int ENCODING_Y_WIDTH = 20;

  public YX(int y, int x) {
    this.y = y;
    this.x = x;
  }

  public int encode() {
    return y * ENCODING_Y_WIDTH + x;
  }

  public static YX decode(int coded) {
    return new YX(coded / ENCODING_Y_WIDTH, coded % ENCODING_Y_WIDTH);
  }

  public YX minus(YX operand) {
    return new YX(this.y - operand.y, this.x - operand.x);
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

  public JsonObjectBuilder toJson() {
    return Json.createObjectBuilder()
        .add("x", x)
        .add("y", y);
  }

  public static YX fromJson(JsonObject object) {
    return new YX(object.getInt("y"), object.getInt("x"));
  }

  @Override
  public int compareTo(YX yx) {
    if (this.y != yx.y) {
      return Integer.compare(this.y, yx.y);
    }
    return Integer.compare(this.x, yx.x);
  }
}
