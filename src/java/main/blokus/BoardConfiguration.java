package blokus;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class BoardConfiguration {
  public int getWidth() {
    return 40;
  }

  public int getHeight() {
    return 40;
  }

  private static final Map<Color, YX> INITIAL_RECEPTORS = ImmutableMap.of(
      Color.BLUE, new YX(0, 0),
      Color.YELLOW, new YX(0, 39),
      Color.RED, new YX(39, 39),
      Color.GREEN, new YX(39, 0));

  public Map<Color, YX> getInitialReceptors() {
    return INITIAL_RECEPTORS;
  }
}
