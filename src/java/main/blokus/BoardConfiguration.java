package blokus;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class BoardConfiguration {
  public int getWidth() {
    return 20;
  }

  public int getHeight() {
    return 20;
  }

  private static final Map<Color, YX> INITIAL_RECEPTORS = ImmutableMap.of(
      Color.BLUE, new YX(0, 0),
      Color.YELLOW, new YX(0, 19),
      Color.RED, new YX(19, 19),
      Color.GREEN, new YX(19, 0));

  public Map<Color, YX> getInitialReceptors() {
    return INITIAL_RECEPTORS;
  }
}
