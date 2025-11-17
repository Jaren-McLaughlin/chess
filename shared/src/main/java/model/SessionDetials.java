package model;

import chess.ChessGame;

public class SessionDetials {
    private int gameId;
    private Boolean isPlayingGame;
    private ChessGame.TeamColor gameColor;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Boolean getPlayingGame() {
        return isPlayingGame;
    }

    public void setPlayingGame(Boolean playingGame) {
        isPlayingGame = playingGame;
    }

    public ChessGame.TeamColor getGameColor() {
        return gameColor;
    }

    public void setGameColor(ChessGame.TeamColor gameColor) {
        this.gameColor = gameColor;
    }
}
