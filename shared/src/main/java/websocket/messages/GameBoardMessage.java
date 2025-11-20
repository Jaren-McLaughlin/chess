package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class GameBoardMessage {
    private final ChessGame chessGame;
    private final ChessGame.TeamColor displayFrom;
    private Collection<ChessMove> possibleMoves = new ArrayList<>();

    public GameBoardMessage (ChessGame chessGame, ChessGame.TeamColor displayFrom) {
        this.chessGame = chessGame;
        this.displayFrom = displayFrom;
    }

    public ChessGame.TeamColor getDisplayFrom() {
        return displayFrom;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public Collection<ChessMove> getPossibleMoves() {
        return possibleMoves;
    }

    public Collection<ChessMove> createPossibleMoves (ChessPosition chessPiece) {
        Collection<ChessMove> possibleMoves = chessGame.validMoves(chessPiece);
        System.out.println("List of possible moves: " + possibleMoves);
        if (possibleMoves == null) {
            return possibleMoves;
        }
        this.possibleMoves = possibleMoves;
        return possibleMoves;
    }
}
