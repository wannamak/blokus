package blokus.server;

import blokus.Color;
import blokus.Game;
import blokus.Piece;
import blokus.PieceLibrary;
import blokus.Square;
import blokus.YX;
import com.google.common.base.Preconditions;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BlokusServer {
  private final Logger logger = Logger.getLogger(BlokusServer.class.getName());

  public static void main(String args[]) throws Exception {
    new BlokusServer().startAndJoin();
  }

  public void startAndJoin() throws Exception {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.addConnector(connector);

    server.setHandler(new BlokusHandler());
    server.start();
    server.join();
  }

  public class BlokusHandler extends AbstractHandler {
    private final PieceLibrary pieceLibrary = new PieceLibrary();
    private final List<Game> games = new ArrayList<>();

    public BlokusHandler() {
    }

    @Override
    public void handle(String target, Request jettyRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
      logger.info("Target=" + target + ", request=" + jettyRequest);

      jettyRequest.setHandled(true);
      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_OK);
      response.addHeader("Access-Control-Allow-Origin", "*");
      response.addHeader("Access-Control-Allow-Headers", "*");

      if (request.getMethod().equals("OPTIONS")) {
        return;
      }

      Game game;
      int gameId;
      if (target.equals("/blokus")) {
        game = new Game(4, pieceLibrary);
        gameId = games.size();
        games.add(game);
      } else {
        gameId = Integer.parseInt(target.substring(target.lastIndexOf('/') + 1));
        game = games.get(gameId);

        try (JsonReader reader = Json.createReader(request.getReader())) {
          JsonObject move = reader.readObject();
          Piece piece = pieceLibrary.getPieceByUniqueId(move.getInt("uniquePieceId"));
          YX boardReceptor = YX.fromJson(move.getJsonObject("boardReceptor"));
          YX pieceCell = YX.fromJson(move.getJsonObject("pieceCell"));
          game.playPiece(game.getCurrentPlayer(), piece, boardReceptor, pieceCell);
        }
      }
      writeMoveList(response.getWriter(), game, gameId);
    }
  }

  private void writeMoveList(PrintWriter writer, Game game, int gameId) {
    Color currentPlayer = game.getCurrentPlayer();

    JsonArrayBuilder moveList = Json.createArrayBuilder();
    game.iterateAvailableMoves((piece, boardReceptor, pieceCell) -> {
      JsonArrayBuilder cellList = Json.createArrayBuilder();
      getCells(piece, boardReceptor, pieceCell).forEach(cellList::add);
      moveList.add(Json.createObjectBuilder()
          .add("pieceId", piece.getPieceId())
          .add("uniquePieceId", piece.getUniquePieceId())
          .add("boardReceptor", boardReceptor.toJson())
          .add("pieceCell", pieceCell.toJson())
          .add("cells", cellList));
    });

    Set<YX> receptors = game.getBoard().getReceptors(game.getCurrentPlayer());
    JsonArrayBuilder receptorList = Json.createArrayBuilder();
    receptors.forEach(yx -> receptorList.add(yx.encode()));

    Map<Color, Set<Integer>> cellMap = game.getBoard().getCellMap();
    JsonObjectBuilder cellObject = Json.createObjectBuilder()
        .add("blue", createJsonArray(cellMap.get(Color.BLUE)))
        .add("yellow", createJsonArray(cellMap.get(Color.YELLOW)))
        .add("green", createJsonArray(cellMap.get(Color.GREEN)))
        .add("red", createJsonArray(cellMap.get(Color.RED)));

    JsonArrayBuilder hilightList = Json.createArrayBuilder();

    JsonObjectBuilder response = Json.createObjectBuilder()
        .add("gameId", gameId)
        .add("scores", Json.createObjectBuilder()
            .add("blue", 1)
            .add("yellow", 2)
            .add("green", 3)
            .add("red", 4))
        .add("receptors", receptorList)
        .add("currentPlayerColor", currentPlayer.name().toLowerCase())
        .add("moves", moveList)
        .add("cells", cellObject)
        .add("hilights", hilightList);

    writer.append(response.build().toString());
  }

  List<Integer> getCells(Piece piece, YX boardReceptor, YX pieceCell) {
    List<Integer> result = new ArrayList<>();
    int originX = boardReceptor.x - pieceCell.x;
    int originY = boardReceptor.y - pieceCell.y;
    Square[][] squares = piece.getSquares();
    for (int yy = 0; yy < squares.length; yy++) {
      for (int xx = 0; xx < squares[0].length; xx++) {
        if (squares[yy][xx] == Square.CELL) {
          int y = originY + yy;
          int x = originX + xx;
          YX yx = new YX(y, x);
          result.add(yx.encode());
        }
      }
    }
    return result;
  }

  JsonArray createJsonArray(Set<Integer> cells) {
    JsonArrayBuilder result = Json.createArrayBuilder();
    cells.forEach(result::add);
    return result.build();
  }
}
