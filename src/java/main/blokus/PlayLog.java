package blokus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayLog {

  public static class PlayLogEntry {
    public final Color color;
    public final Piece piece;
    public final YX boardReceptor;
    public final YX pieceCell;

    public PlayLogEntry(Color color, Piece piece, YX boardReceptor, YX pieceCell) {
      this.color = color;
      this.piece = piece;
      this.boardReceptor = boardReceptor;
      this.pieceCell = pieceCell;
    }

    @Override
    public int hashCode() {
      return Objects.hash(color, piece, boardReceptor, pieceCell);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof PlayLogEntry)) {
        return false;
      }
      PlayLogEntry that = (PlayLogEntry) o;
      return this.color.equals(that.color)
          && this.piece.equals(that.piece)
          && this.boardReceptor.equals(that.boardReceptor)
          && this.pieceCell.equals(that.pieceCell);
    }
  }

  private final List<PlayLogEntry> entries;

  public PlayLog() {
    this.entries = new ArrayList<>();
  }

  private PlayLog(List<PlayLogEntry> entries) {
    this();
    this.entries.addAll(entries);
  }

  public void log(Piece piece, YX boardReceptor, YX pieceCell) {
    entries.add(new PlayLogEntry(Color.BLUE, piece, boardReceptor, pieceCell));
  }

  public List<PlayLogEntry> getEntries() {
    return entries;
  }

  public List<Piece> getPiecesPlayed(Color color) {
    return entries.stream().filter(e -> e.color.equals(color)).map(e -> e.piece).collect(Collectors.toList());
  }

  public PlayLog copy() {
    return new PlayLog(entries);
  }

  public void append(StringBuilder sb) {
    //sb.append("current=" + currentPlayer + ", piecesAvail=" + pieceIds.get(currentPlayer));
    for (PlayLogEntry entry : entries) {
      sb.append("id=")
          .append(entry.piece.getPieceId())
          .append(":uniqueid=")
          .append(entry.piece.getUniquePieceId())
          .append(":rot=")
          .append(entry.piece.getRotationId())
          .append(":flipped=")
          .append(Boolean.toString(entry.piece.isFlipped()).charAt(0))
          .append(":cell=")
          .append(entry.pieceCell)
          .append(":at=")
          .append(entry.boardReceptor)
          .append(System.lineSeparator());
    }
  }
}
