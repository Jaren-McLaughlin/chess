package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardUi {
//    Draw the chess board
    public void drawChessBoard() {
        //ChessGame chessGame) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        char[] col = { '1', '2', '3', '4', '5', '6', '7', '8'};
        char[] row = { 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        String[] basePiece = { ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
        String[] pawnPiece = {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN};
        String[] emptyPiece = {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY};
        String[] colorArray = {SET_TEXT_COLOR_BLACK, SET_TEXT_COLOR_BLACK, SET_TEXT_COLOR_BLACK,SET_TEXT_COLOR_BLACK,SET_TEXT_COLOR_BLACK,SET_TEXT_COLOR_BLACK,SET_TEXT_COLOR_BLACK,SET_TEXT_COLOR_BLACK};
        String[] whiteColorArray = {SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_WHITE};
        out.print(ERASE_SCREEN);

        out.print(BG_DARK_BROWN);
        out.print(TXT_GOLD);
        boarder(out,("    " + col[0] + " \u2003" + col[1] + " \u2003" + col[2] + " \u2003" + col[3] + " \u2003" + col[4] + " \u2003" + col[5] + " \u2003" + col[6] + " \u2003" + col[7] + "    " + NEW_LINE));
        boarder(out, ("  " + "┌" + "─".repeat(29) + "┐" + "  " + NEW_LINE)); // + "─" + "┐"
        for (int i = 0; i < 8; i++) {
            boarder(out, " " + row[i] + "│"); //+ " " + "│"
            if (i % 2 == 0) {
                startLightRow(out, basePiece, colorArray);
            } else {
                startDarkRow(out, basePiece, whiteColorArray);
            }
            boarder(out, ("│" + row[i] + " " + NEW_LINE));
        }


        boarder(out, ("  " + "└" + "─".repeat(29) + "┘" + "  " + NEW_LINE)); // + "─" + "┐"
        boarder(out,("    " + col[0] + " \u2003" + col[1] + " \u2003" + col[2] + " \u2003" + col[3] + " \u2003" + col[4] + " \u2003" + col[5] + " \u2003" + col[6] + " \u2003" + col[7] + "    " + NEW_LINE));
//        boarder(out, " " + row[0] + "│"); //+ " " + "│"
//        startLightRow(out, basePiece, colorArray);
//        boarder(out, ("│" + row[0] + " " + NEW_LINE));
//        boarder(out, " " + row[1] + "│"); //+ " " + "│"
//        startDarkRow(out, basePiece, whiteColorArray);
//        boarder(out, ("│" + row[1] + " " + NEW_LINE));
//
//        out.print(BG_DARK_BROWN);
//        out.print(TXT_GOLD);
//        out.print(" G" + "│"); //+ " " + "│"
//        out.print(BG_MEDIUM_BROWN);
//        out.print(SET_TEXT_COLOR_WHITE);
//        out.print(ROOK);
//        out.print(BG_LIGHT_BROWN);
//        out.print(KNIGHT);
//        out.print(BG_MEDIUM_BROWN);
//        out.print(BISHOP);
//        out.print(BG_LIGHT_BROWN);
//        out.print(QUEEN);
//        out.print(BG_MEDIUM_BROWN);
//        out.print(KING);
//        out.print(BG_LIGHT_BROWN);
//        out.print(BISHOP);
//        out.print(BG_MEDIUM_BROWN);
//        out.print(KNIGHT);
//        out.print(BG_LIGHT_BROWN);
//        out.print(ROOK);
//        out.print(BG_DARK_BROWN);
//        out.print(TXT_GOLD);
//        out.print("│" + "G " + CLEAR + '\n');
//
//        out.print(BG_DARK_BROWN);
//        out.print(TXT_GOLD);
//        out.println("  " + "└"  + CLEAR); //+ "─" + "┘"
//        out.print(BG_DARK_BROWN);
//        out.print(TXT_GOLD);
//        out.print("   ");

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

    private void startLightRow(PrintStream out, String[] text, String[] pieceColors) {
        lightSquare(out, text[0], pieceColors[0]);
        darkSquare(out, text[1], pieceColors[1]);
        lightSquare(out, text[2], pieceColors[2]);
        darkSquare(out, text[3], pieceColors[3]);
        lightSquare(out, text[4], pieceColors[4]);
        darkSquare(out, text[5], pieceColors[5]);
        lightSquare(out, text[6], pieceColors[6]);
        darkSquare(out, text[7], pieceColors[7]);
    }

    private void startDarkRow(PrintStream out, String[] text, String[] pieceColors) {
        darkSquare(out, text[0], pieceColors[0]);
        lightSquare(out, text[1], pieceColors[1]);
        darkSquare(out, text[2], pieceColors[2]);
        lightSquare(out, text[3], pieceColors[3]);
        darkSquare(out, text[4], pieceColors[4]);
        lightSquare(out, text[5], pieceColors[5]);
        darkSquare(out, text[6], pieceColors[6]);
        lightSquare(out, text[7], pieceColors[7]);
    }

    public void drawFromWhite() {
        // Arrange text in order for the perspective of white
        // Should be the normal way that the board looks
    }

    public void drawFromBlack() {
        // Arrange text in order of black perspective
        // Just start in the top right 7,7 of a game board and read backwards
        //
    }
}
