package util;

import java.util.Arrays;

public class VarKey {
  private final int[] keys;

  public VarKey(int... keys) {
    this.keys = keys;
  }

  public int[] getKeys() {
    return keys;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(keys);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof VarKey)) {
      return false;
    }
    return Arrays.equals(keys, ((VarKey) obj).keys);
  }

  @Override
  public String toString() {
    return Arrays.toString(keys);
  }
}
