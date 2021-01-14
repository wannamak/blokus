const colors = ['blue', 'yellow', 'red', 'green']
const numPieces = 21
const scoreElements = new Map()
const cellElements = []
let moveListElement;
let moveList = [];
let gameId = 0;

function onLoad() {
  createBoardElements("#board");
  colors.forEach(color => {
    let id = '#' + color + '-score'
    scoreElements.set(color, $(id))
  })
  moveListElement = $('#move-list-parent')
  $.ajax({
    url: "http://localhost:8080/blokus",
    method: "GET",
    dataType: "json",
    success: function(result, status, xhr) {
      handleResponse(result)
    }})
}

function onTakeTurnClick() {
//  handleResponse();
}

function handleResponse(response) {
  // const json =
  // '{"scores": {"blue":5, "yellow":7, "green":8, "red":9 }, ' +
  //     '"currentPlayerColor": "blue", ' +
  //      '"receptors": [4,8,12], ' +
  //      '"moves": [' +
  //      '{"pieceId": 0, "uniquePieceId": 1, "boardReceptor": {"y":10, "x":10}, "pieceCell": {"y":1, "x":1}, "cells":   [4,21,41,61] },' +
  //      '{"pieceId": 0, "uniquePieceId": 2, "boardReceptor": {"y":11, "x":11}, "pieceCell": {"y":1, "x":1}, "cells": [35,55,75] },' +
  //      '{"pieceId": 3, "uniquePieceId": 20, "boardReceptor": {"y":12, "x":12}, "pieceCell": {"y":1, "x":1}, "cells": [35,36,37] }' +
  //      '], ' +
  //      '"message": "This is a message."}';
  //const response = JSON.parse(json)
  console.log(response)
  gameId = response.gameId
  for (let color in response.scores) {
    scoreElements.get(color).text(response.scores[color]);
  }
  updateCurrentPlayer(response.currentPlayerColor);
  drawReceptors(response.receptors, response.currentPlayerColor);
  buildMoveList(response.moves, response.currentPlayerColor);
  drawCells(response.cells);
  drawHilights(response.hilights);
}

function buildMoveList(moves, color) {
  moveListElement.empty()
  moveList = []
  const moveListElementByPieceId = new Map()
  let count = 1
  for (let pieceId = 0; pieceId < numPieces; pieceId++) {
    moves.forEach(move => {
      if (move.pieceId !== pieceId) {
        return;
      }
      if (!moveListElementByPieceId.has(pieceId)) {
        let li = $('<li>Piece ' + pieceId + '<ul class="movelist-ul"></ul></li>')
        moveListElement.append(li)
        moveListElementByPieceId.set(pieceId, $(li).find("ul"))
      }
      const index = moveList.length;
      moveListElementByPieceId.get(pieceId).append('<li ' +
        'class="movelist-li"' +
        'onmouseover="moveListOnMouseOver(' + index + ',\'' + color + '\')" ' +
        'onmouseout="moveListOnMouseOut()" ' +
        'onclick="moveListOnClick(' + index + ',\'' + color + '\')">' +
        count++ +
        ': uniq ' + move.uniquePieceId + '; ' +
        'receptor (' + move.boardReceptor.y + ',' + move.boardReceptor.x + '); ' +
        'cell (' + move.pieceCell.y + ',' + move.pieceCell.x + ')</li>');
      moveList.push(move);
    });
  }
}

function moveListOnMouseOver(index, color) {
  removeAllMoveCandidates();
  moveList[index].cells.forEach(id => cellElements[id].addClass("move-candidate-" + color));
}

function moveListOnMouseOut() {
  removeAllMoveCandidates();
}

function moveListOnClick(index) {
  $.ajax({
    url: "http://localhost:8080/blokus/" + gameId,
    method: "POST",
    contentType: "application/json; charset=utf-8",
    data: JSON.stringify(moveList[index]),
    dataType: "json",
    success: function(result) {
      handleResponse(result)
  }})
}

function updateCurrentPlayer(currentPlayerColor) {
  colors.forEach(color => scoreElements.get(color).removeClass('currentPlayer'))
  scoreElements.get(currentPlayerColor).addClass('currentPlayer')
}

function drawReceptors(receptors, color) {
  removeAllReceptors();
  receptors.forEach(id => cellElements[id].addClass("receptor-" + color))
}

function drawCells(colorMap) {
  for (let color in colorMap) {
    console.log(colorMap[color])
    colorMap[color].forEach(id => cellElements[id].addClass("cell-" + color))
  }
}

function drawHilights(hilights) {
  removeAllHilights()
  hilights.forEach(id => cellElements[id].addClass("hilight"))
}

function removeAllReceptors() {
  for (let i = 0; i < 399; i++) {
    cellElements[i].removeClass(function (index, className) {
      return (className.match(/(^|\s)receptor-\S+/g) || []).join(' ');
    });
  }
}

function removeAllMoveCandidates() {
  for (let i = 0; i < 399; i++) {
    cellElements[i].removeClass(function (index, className) {
      return (className.match(/(^|\s)move-candidate-\S+/g) || []).join(' ');
    });
  }
}

function removeAllHilights() {
  for (let i = 0; i < 399; i++) {
    cellElements[i].removeClass("hilight")
  }
}

function createBoardElements(parentElementId) {
  let parent = $(parentElementId)
  createHorizontalLabelRow(parent)
  let i = 0
  for (let y = 0; y < 20; y++) {
    parent.append("<div class=label>" + y + "</div>")
    for (let x = 0; x < 20; x++) {
      let cellElement = $("<div class='cell' id=cell" + i + ">" +
          "</div>")
      parent.append(cellElement)
      cellElements.push(cellElement)
      i++;
    }
    parent.append("<div class=label>" + y + "</div>")
  }
  createHorizontalLabelRow(parent)
}

function createHorizontalLabelRow(parent) {
  parent.append("<div></div>");
  for (let x = 0; x < 20; x++) {
    parent.append("<div class=label>" + x + "</div>");
  }
  parent.append("<div></div>");
}