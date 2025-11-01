import chess.*;
import ui.ChessBoardUi;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessBoardUi temp = new ChessBoardUi();

        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        chessBoard.movePiece(new ChessMove(new ChessPosition(1,1), new ChessPosition(5,5), null));
        chessBoard.movePiece(new ChessMove(new ChessPosition(8,4), new ChessPosition(4,4), null));

        temp.drawFromWhite(chessBoard);
        temp.drawFromBlack(chessBoard);
    }
}