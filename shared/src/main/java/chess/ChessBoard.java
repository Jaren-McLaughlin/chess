package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private ChessPiece[][] gameBoard = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        gameBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
//        System.out.println(gameBoard);
        return gameBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPosition getLocationByPiece(ChessPiece piece) {
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        ChessPiece.PieceType pieceType = piece.getPieceType();
        for (int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition locationPosition = new ChessPosition(i,j);
                ChessPiece locationPiece = getPiece(locationPosition);
//            System.out.println(locationPiece);
                if (locationPiece == null) {
                    continue;
                }
                ChessGame.TeamColor locationColor = locationPiece.getTeamColor();
                ChessPiece.PieceType locationType = locationPiece.getPieceType();
                if (
                        locationType == pieceType &&
                                locationColor == pieceColor
                ) {
                    return locationPosition;
                }
            }
        }
        return null;
    }

    public void movePiece(ChessMove myMove) {
        ChessPosition startPosition = myMove.getStartPosition();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();

        ChessPosition endPosition = myMove.getEndPosition();
        int endRow = endPosition.getRow();
        int endCol = endPosition.getColumn();

        ChessPiece piece = getPiece(startPosition);
        System.out.println("The piece, if promoted should be different" + piece.getPieceType());
        if (
            piece.getPieceType() == ChessPiece.PieceType.PAWN &&
            myMove.getPromotionPiece() != null
        ) {
            piece.updatePieceType(myMove.getPromotionPiece());
        }
        System.out.println("The piece, if promoted should be different" + piece.getPieceType());
        gameBoard[startRow - 1][startCol - 1] = null;
        gameBoard[endRow - 1][endCol - 1] = piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
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
        gameBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            gameBoard[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, pieces[i]);
            gameBoard[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            gameBoard[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            gameBoard[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, pieces[i]);
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "gameBoard=" + Arrays.deepToString(gameBoard) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(gameBoard, that.gameBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(gameBoard);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            // Comment these lines out to see what happens with a shallow copy that contains a mutable instance variable
            ChessPiece[][] cloneBoard = new ChessPiece[8][8];
            for (int i = 0; i < 8 ; i++) {
                for(int j = 0; j < 8; j++) {
                    if (gameBoard[i][j] != null) {
                        cloneBoard[i][j] = gameBoard[i][j].clone();
                    }
                }
            }

            clone.gameBoard = cloneBoard;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
