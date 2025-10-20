package model;

import chess.ChessGame.TeamColor;

public record JoinGameData(TeamColor playerColor, int gameID) {
}
