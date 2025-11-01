package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardUi {
    private static final char[] col = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    private static final char[] colBackwards = {'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
    private static final char[] row = {'8', '7', '6', '5', '4', '3', '2', '1'};
    private static final char[] rowBackwards = {'1', '2', '3', '4', '5', '6', '7', '8'};

    public void drawChessBoard(ChessPiece[][] chessGame, char[] col, char[] row) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        String colString = ("    " + col[0] + " \u2003" + col[1] + " \u2003" + col[2] + " \u2003" + col[3] + " \u2003" + col[4] + " \u2003" + col[5] + " \u2003" + col[6] + " \u2003" + col[7] + "    " + NEW_LINE);
        boarder(out, colString);
        boarder(out, ("  " + "┌" + "─".repeat(29) + "┐" + "  " + NEW_LINE));
        for (int i = 0; i < 8; i++) {
            boarder(out, " " + row[i] + "│");
            if (i % 2 == 0) {
                startLightRow(out, chessGame[i]);
            } else {
                startDarkRow(out, chessGame[i]);
            }
            boarder(out, ("│" + row[i] + " " + NEW_LINE));
        }
        
        boarder(out, ("  " + "└" + "─".repeat(29) + "┘" + "  " + NEW_LINE));
        boarder(out, colString);
    }

    private void boarder(PrintStream out, String text) {
        out.print(BG_DARK_BROWN);
        out.print(TXT_GOLD);
        out.print(text);
    }

    private void lightSquare(PrintStream out, String text, String pieceColor) {
        out.print(BG_LIGHT_BROWN);
        out.print(pieceColor);
        out.print(text);
    }

    private void darkSquare(PrintStream out, String text, String pieceColor) {
        out.print(BG_MEDIUM_BROWN);
        out.print(pieceColor);
        out.print(text);
    }

    private String getTextFromEnum(ChessPiece.PieceType pieceType) {
        if (pieceType == null) {
            return EMPTY;
        }
        return switch(pieceType) {
            case ChessPiece.PieceType.KING -> KING;
            case ChessPiece.PieceType.QUEEN -> QUEEN;
            case ChessPiece.PieceType.BISHOP -> BISHOP;
            case ChessPiece.PieceType.KNIGHT -> KNIGHT;
            case ChessPiece.PieceType.ROOK -> ROOK;
            case ChessPiece.PieceType.PAWN -> PAWN;
        };
    }

    private String getColorFromEnum(ChessGame.TeamColor pieceType) {
        return switch(pieceType) {
            case ChessGame.TeamColor.WHITE -> TXT_BLACK;
            case ChessGame.TeamColor.BLACK -> TXT_WHITE;
        };
    }

    private void startLightRow(PrintStream out, ChessPiece[] chessPiece) {
        for (int i = 0; i < 8; i++) {
            ChessPiece piece = chessPiece[i];
            String text = EMPTY;
            String pieceColor = TXT_BLACK;
            if (piece != null) {
                text = getTextFromEnum(chessPiece[i].getPieceType());
                pieceColor = getColorFromEnum(chessPiece[i].getTeamColor());
            }
            if (i % 2 == 0) {
                lightSquare(out, text, pieceColor);
            } else {
                darkSquare(out, text, pieceColor);
            }
        }
    }

    private void startDarkRow(PrintStream out, ChessPiece[] chessPiece) {
        for (int i = 0; i < 8; i++) {
            ChessPiece piece = chessPiece[i];
            String text = EMPTY;
            String pieceColor = TXT_BLACK;
            if (piece != null) {
                text = getTextFromEnum(chessPiece[i].getPieceType());
                pieceColor = getColorFromEnum(chessPiece[i].getTeamColor());
            }
            if (i % 2 == 0) {
                darkSquare(out, text, pieceColor);
            } else {
                lightSquare(out, text, pieceColor);
            }
        }
    }

    public void drawFromWhite() {
        // Arrange text in order for the perspective of white
        // Should be the normal way that the board looks
        //Temp
        // Piece array
        ChessPiece.PieceType[] pieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        //Reset board
        ChessPiece[][] gameBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            gameBoard[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, pieces[i]);
            gameBoard[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            gameBoard[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            gameBoard[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, pieces[i]);
        }

        drawChessBoard(gameBoard, col, row);
    }

    public void drawFromBlack() {
        // Arrange text in order of black perspective
        // Just start in the top right 7,7 of a game board and read backwards
        //
        ChessPiece.PieceType[] pieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        //Reset board
        ChessPiece[][] gameBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            gameBoard[7][i] = new ChessPiece(ChessGame.TeamColor.WHITE, pieces[i]);
            gameBoard[6][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            gameBoard[1][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            gameBoard[0][i] = new ChessPiece(ChessGame.TeamColor.BLACK, pieces[i]);
        }

        drawChessBoard(gameBoard, colBackwards, rowBackwards);
    }
}
