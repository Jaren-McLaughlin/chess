package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public final class ChessBoardUi {
    private static final char[] COL = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    private static final char[] COL_BACKWARDS = {'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
    private static final char[] ROW = {'8', '7', '6', '5', '4', '3', '2', '1'};
    private static final char[] ROW_BACKWARDS = {'1', '2', '3', '4', '5', '6', '7', '8'};

    public static void drawChessBoard(ChessPiece[][] chessGame, char[] col, char[] row) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        out.flush();

        String colString = (
            "    " + col[0] +
            " \u2003" + col[1] +
            " \u2003" + col[2] +
            " \u2003" + col[3] +
            " \u2003" + col[4] +
            " \u2003" + col[5] +
            " \u2003" + col[6] +
            " \u2003" + col[7] +
            "    " + NEW_LINE
        );
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

    private static void boarder(PrintStream out, String text) {
        out.print(BG_DARK_BROWN);
        out.print(TXT_GOLD);
        out.print(text);
    }

    private static void lightSquare(PrintStream out, String text, String pieceColor) {
        out.print(BG_LIGHT_BROWN);
        out.print(pieceColor);
        out.print(text);
    }

    private static void darkSquare(PrintStream out, String text, String pieceColor) {
        out.print(BG_MEDIUM_BROWN);
        out.print(pieceColor);
        out.print(text);
    }

    private static String getTextFromEnum(ChessPiece.PieceType pieceType) {
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

    private static String getColorFromEnum(ChessGame.TeamColor pieceType) {
        return switch(pieceType) {
            case ChessGame.TeamColor.WHITE -> TXT_WHITE;
            case ChessGame.TeamColor.BLACK -> TXT_BLACK;
        };
    }

    private static void startLightRow(PrintStream out, ChessPiece[] chessPiece) {
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

    private static void startDarkRow(PrintStream out, ChessPiece[] chessPiece) {
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

    public static void drawFromWhite(ChessBoard chessBoard) {
        ChessPiece[][] gameBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                gameBoard[i][k] = chessBoard.getPiece(new ChessPosition( 8 - i, k + 1));
            }
        }

        drawChessBoard(gameBoard, COL, ROW);
    }

    public static void drawFromBlack(ChessBoard chessBoard) {
        ChessPiece[][] gameBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                gameBoard[i][k] = chessBoard.getPiece(new ChessPosition(i + 1, 8 - k));
            }
        }

        drawChessBoard(gameBoard, COL_BACKWARDS, ROW_BACKWARDS);
    }
}
