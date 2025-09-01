package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    Map<PieceType, int[][]> pieceMoves = Map.of(
        // Sliding pieces
        PieceType.ROOK, new int[][] {{1,0},{0,1},{-1,0},{0,-1}},
        PieceType.BISHOP, new int[][] {{1,1},{-1,1},{1,-1},{-1,-1}},
        PieceType.QUEEN, new int[][] {{1,0},{0,1},{-1,0},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}},
        // Static moves
        PieceType.KNIGHT, new int[][] {{2,1},{1,2},{-2,1},{-1,2},{2,-1},{1,-2},{-2,-1},{-1,-2}},
        PieceType.KING, new int[][] {{1,0},{0,1},{-1,0},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}}
    );

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (
            this.type == PieceType.ROOK ||
            this.type == PieceType.BISHOP ||
            this.type == PieceType.QUEEN
        ) {
            int[][] directions = pieceMoves.get(this.type);

            for (int[] direction : directions) {
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();
                while(true) {
                    newRow = newRow + direction[0];
                    newCol = newCol + direction[1];
                    ChessPosition newMove;
                    try {
                        newMove = new ChessPosition(newRow, newCol);
                    } catch (Exception e) {
                        break;
                    }
                    ChessPiece isPiece = board.getPiece(newMove);
                    if (isPiece != null) {
                        if (this.pieceColor != isPiece.pieceColor) {
                            moves.add(new ChessMove(myPosition, newMove, null));
                        }
                        break;
                    }
                    moves.add(new ChessMove(myPosition, newMove, null));
                }
            }
        }
        if (
            this.type == PieceType.KNIGHT ||
            this.type == PieceType.KING
        ) {
            int[][] directions = pieceMoves.get(this.type);

            for (int[] direction : directions) {
                int newRow = myPosition.getRow() + direction[0];
                int newCol = myPosition.getColumn() + direction[1];
                ChessPosition newMove;
                try {
                    newMove = new ChessPosition(newRow, newCol);
                } catch (Exception e) {
                    continue;
                }
                ChessPiece isPiece = board.getPiece(newMove);
                if (isPiece != null) {
                    if (this.pieceColor != isPiece.pieceColor) {
                        moves.add(new ChessMove(myPosition, newMove, null));
                    }
                    continue;
                }
                moves.add(new ChessMove(myPosition, newMove, null));
            }
        }
        return moves;
    }
}
