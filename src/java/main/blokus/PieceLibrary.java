package blokus;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static blokus.Square.ADJACENT;
import static blokus.Square.CELL;
import static blokus.Square.EMPTY;
import static blokus.Square.RECEPTOR;

public class PieceLibrary {
  private static final Square[][][] PIECES = new Square[][][] {
      // 1x1
      new Square[][] {
          new Square[] { CELL },
      },
      // 2x1
      new Square[][] {
          new Square[] { CELL, CELL },
      },
      // 2x2 L
      new Square[][] {
          new Square[] { CELL,  CELL },
          new Square[] { EMPTY, CELL },
      },
      // 2x2 square
      new Square[][] {
          new Square[] { CELL, CELL },
          new Square[] { CELL, CELL },
      },
      // 3x1
      new Square[][] {
          new Square[] { CELL, CELL, CELL },
      },
      // 3x2 T
      new Square[][] {
          new Square[] { EMPTY, CELL, EMPTY },
          new Square[] { CELL,  CELL, CELL  },
      },
      // 4x1
      new Square[][] {
          new Square[] { CELL, CELL, CELL, CELL },
      },
      // 3x2 L
      new Square[][] {
          new Square[] { EMPTY, EMPTY, CELL },
          new Square[] { CELL,  CELL,  CELL },
      },
      // 3x2 S
      new Square[][] {
          new Square[] { EMPTY, CELL, CELL },
          new Square[] { CELL,  CELL, EMPTY },
      },
      // 4x2 L
      new Square[][] {
          new Square[] { CELL, EMPTY, EMPTY, EMPTY },
          new Square[] { CELL, CELL,  CELL,  CELL },
      },
      // 3x3 T
      new Square[][] {
          new Square[] { EMPTY, CELL, EMPTY },
          new Square[] { EMPTY, CELL, EMPTY },
          new Square[] { CELL,  CELL, CELL  },
      },
      // 3x3 L
      new Square[][] {
          new Square[] { CELL, EMPTY, EMPTY },
          new Square[] { CELL, EMPTY, EMPTY },
          new Square[] { CELL, CELL,  CELL },
      },
      // 4x2 S
      new Square[][] {
          new Square[] { EMPTY, CELL, CELL,  CELL },
          new Square[] { CELL,  CELL, EMPTY, EMPTY },
      },
      // 3x3 N
      new Square[][] {
          new Square[] { EMPTY, EMPTY, CELL },
          new Square[] { CELL,  CELL,  CELL },
          new Square[] { CELL,  EMPTY, EMPTY },
      },
      // 5x1
      new Square[][] {
        new Square[] { CELL, CELL, CELL, CELL, CELL },
      },
      // 3x2 B
      new Square[][] {
          new Square[] { CELL, EMPTY },
          new Square[] { CELL, CELL },
          new Square[] { CELL, CELL },
      },
      // 3x3 W
      new Square[][] {
          new Square[] { EMPTY, CELL,  CELL },
          new Square[] { CELL,  CELL,  EMPTY },
          new Square[] { CELL,  EMPTY, EMPTY },
      },
      // 3x2 C
      new Square[][] {
          new Square[] { CELL, CELL },
          new Square[] { CELL, EMPTY },
          new Square[] { CELL, CELL },
      },
      // 3x3 F
      new Square[][] {
          new Square[] { EMPTY, CELL, CELL },
          new Square[] { CELL,  CELL, EMPTY },
          new Square[] { EMPTY, CELL, EMPTY },
      },
      // 3x3 plus
      new Square[][] {
          new Square[] { EMPTY, CELL, EMPTY },
          new Square[] { CELL,  CELL, CELL },
          new Square[] { EMPTY, CELL, EMPTY },
      },
      // 4x2 extended T
      new Square[][] {
          new Square[] { EMPTY, CELL, EMPTY, EMPTY },
          new Square[] { CELL,  CELL, CELL,  CELL },
      },
  };

  private int uniquePieceId = 0;
  private final Map<Integer, Set<Piece>> piecesByPieceId;
  private final Map<Integer, Piece> piecesByUniqueId;
  private final Map<Integer, Integer> cellsByPieceId;

  public PieceLibrary() {
    this.cellsByPieceId = new HashMap<>();
    this.piecesByPieceId = generate();
    this.piecesByUniqueId = new HashMap<>();
    for (int pieceId : piecesByPieceId.keySet()) {
      for (Piece piece : piecesByPieceId.get(pieceId)) {
        piecesByUniqueId.put(piece.getUniquePieceId(), piece);
      }
    }
  }

  public Set<Integer> getAllPieceIds() {
    return piecesByPieceId.keySet();
  }

  public Set<Integer> getAllUniquePieceIds() {
    return piecesByUniqueId.keySet();
  }

  public Set<Piece> getPiecePermutations(int pieceId) {
    return piecesByPieceId.get(pieceId);
  }

  public Map<Integer, Set<Piece>> getAllPieces() {
    return piecesByPieceId;
  }

  public Piece getPieceByUniqueId(int uniquePieceId) {
    return piecesByUniqueId.get(uniquePieceId);
  }

  public int getNumCells(int pieceId) {
    return cellsByPieceId.get(pieceId);
  }

  private Set<Piece> getPermutations(int pieceId) {
    return piecesByPieceId.get(pieceId);
  }

  private Square[][] expand(Square[][] input) {
    Square[][] result = new Square[input.length + 2][input[0].length + 2];
    for (int y = 0; y < result.length; y++) {
      for (int x = 0; x < result[0].length; x++) {
        result[y][x] = EMPTY;
      }
    }
    for (int y = 0; y < input.length; y++) {
      for (int x = 0; x < input[0].length; x++) {
        if (input[y][x] == CELL) {
          result[y + 1][x + 1] = CELL;
          if (result[y][x + 1] != CELL) result[y][x + 1] = ADJACENT;
          if (result[y + 2][x + 1] != CELL) result[y + 2][x + 1] = ADJACENT;
          if (result[y + 1][x] != CELL) result[y + 1][x] = ADJACENT;
          if (result[y + 1][x + 2] != CELL) result[y + 1][x + 2] = ADJACENT;
          if (result[y][x] == EMPTY) result[y][x] = RECEPTOR;
          if (result[y][x + 2] == EMPTY) result[y][x + 2] = RECEPTOR;
          if (result[y + 2][x] == EMPTY) result[y + 2][x] = RECEPTOR;
          if (result[y + 2][x + 2] == EMPTY) result[y + 2][x + 2] = RECEPTOR;
        }
      }
    }
    return result;
  }

  private Map<Integer, Set<Piece>> generate() {
    Map<Integer, Set<Piece>> pieces = new LinkedHashMap<>();
    for (int pieceId = 0; pieceId < PIECES.length; pieceId++) {
      Square[][] pieceDefinition = expand(PIECES[pieceId]);
      Set<Piece> permutations = new LinkedHashSet<>();
      addRotations(permutations, pieceId, false, pieceDefinition);
      Arrays.flipVertically(pieceDefinition);
      addRotations(permutations, pieceId, true, pieceDefinition);
      pieces.put(pieceId, permutations);
      cellsByPieceId.put(pieceId, countCells(pieceDefinition));
    }
    return pieces;
  }

  private void addRotations(Set<Piece> set, int pieceId, boolean isFlipped, Square[][] pieceDefinition) {
    add(set, pieceId,0, isFlipped, Arrays.copy(pieceDefinition));
    for (int rotationId = 1; rotationId < 4; rotationId++) {
      pieceDefinition = Arrays.rotate(pieceDefinition);
      add(set, pieceId, rotationId, isFlipped, pieceDefinition);
    }
  }

  private void add(Set<Piece> set, int pieceId, int rotationId, boolean isFlipped, Square[][] pieceDefinition) {
    int size = set.size();
    if (set.add(new Piece(pieceId, uniquePieceId, rotationId, isFlipped, pieceDefinition))) {
      uniquePieceId++;
      Preconditions.checkState(set.size() == size + 1);
    } else {
      Preconditions.checkState(set.size() == size);
    }
  }

  private int countCells(Square[][] definition) {
    int numCells = 0;
    for (int y = 0; y < definition.length; y++) {
      for (int x = 0; x < definition[0].length; x++) {
        if (definition[y][x] == CELL) {
          numCells++;
        }
      }
    }
    return numCells;
  }
}
