package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard chessBoard;
    Map<TeamColor, Boolean> isInCheck = new HashMap<>();

    public ChessGame() {
        this.chessBoard = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        isInCheck.put(TeamColor.WHITE, false);
        isInCheck.put(TeamColor.BLACK, false);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(chessBoard, chessGame.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, chessBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", chessBoard=" + chessBoard +
                '}';
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private boolean attacksKing(ChessMove potentialMove) {
        // create a temp board with the piece in the new location
        ChessBoard tempBoard = chessBoard;
        tempBoard.movePiece(potentialMove);

        // Check all enemy moves
        // Check if one of those moves is the kings spot
        // return true if it does attack the king
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece movingPiece = chessBoard.getPiece(startPosition);
        if (movingPiece == null) {
            return null;
        }

        Collection<ChessMove> moves = movingPiece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        // Check if this move puts my king in check
        for (ChessMove move: moves) {
            boolean attackKing = attacksKing(move);
            if (attackKing) {
                continue;
            }
            validMoves.add(move);
        }
        // If king could be put in check, remove it.
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Get piece color
        ChessPosition startLocation = move.getStartPosition();
        ChessPiece movingPiece = chessBoard.getPiece(startLocation);
        TeamColor pieceColor = movingPiece.getTeamColor();
        // Check if it's this pieces turn
        if (pieceColor != teamTurn) {
            throw new InvalidMoveException("It is not your turn");
        }
        // Check if this is a valid move
        Collection<ChessMove> possibleMoves = validMoves(startLocation);

        // if it's not a valid move, throw error
        // Move piece to array spot

        // Take this move and find all it's move
        // Check if one of the moves is the opponents king
        // if it is, set flag to true
        if (isKing != null) {
            isInCheck.put(pieceColor, true);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck.get(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If it's got no valid moves and is in check, then it's in checkmate
        if (isInCheck.get(teamColor)) {
            // Check if it has any valid moves

        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // if it's got no valid moves, it's a stalemate

        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
