package blokus;

import java.util.List;

public class Scorer {
  private final Game game;
  public Scorer(Game game) {
    this.game = game;
  }

  public int score(Color color) {
    int score = 0;
    for (int pieceId : game.getAvailablePieceIds(color)) {
      score -= game.getPieceLibrary().getNumCells(pieceId);
    }
    if (game.getAvailablePieceIds(color).isEmpty()) {
      score += 15;
      // If the last played was a 1x1, the bonus is 20, not 15.
      List<Piece> played = game.getPlayLog().getPiecesPlayed(color);
      if (!played.isEmpty() && played.get(played.size() - 1).getPieceId() == 0) {
        score += 5;
      }
    }
    return score;
  }
}
