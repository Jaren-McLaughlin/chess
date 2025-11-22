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
//    Map<TeamColor, Boolean> isInCheck = new HashMap<>();

    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
//        isInCheck.put(TeamColor.WHITE, false);
//        isInCheck.put(TeamColor.BLACK, false);
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

    public enum GameStatus {
        CHECKMATE,
        PLAYING,
        RESIGNED,
        STALEMATE,
    }

    private TeamColor getMovingColor (ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = chessBoard.getPiece(startPosition);
        return piece.getTeamColor();
    }

    private Collection<ChessMove> getTeamMoves(TeamColor teamColor, ChessBoard board) {
        // for loop over board to get all piece locations and find their moves
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                ChessPosition locationPosition = new ChessPosition(i, j);
                ChessPiece locationPiece = board.getPiece(locationPosition);
                if (locationPiece == null) {
                    continue;
                }
                ChessGame.TeamColor locationColor = locationPiece.getTeamColor();
                if (locationColor != teamColor) {
                    continue;
                }
                Collection<ChessMove> possibleMoves = locationPiece.pieceMoves(board, locationPosition);
                teamMoves.addAll(possibleMoves);
            }
        }
        return teamMoves;
    }

    private ChessBoard createTempBoard(ChessMove potentialMove) {
        // create a temp board with the piece in the new location
        ChessBoard tempBoard = chessBoard.clone();
        tempBoard.movePiece(potentialMove);
        return tempBoard;
    }

    private boolean isKingAttacked(ChessBoard tempBoard, TeamColor myTeam) {
        // Get opponent color
        TeamColor opponent = myTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        // Check all enemy moves
        Collection<ChessMove> moves = getTeamMoves(opponent, tempBoard);
        System.out.println(moves);
        // Find our king
        ChessPosition myKing = tempBoard.getLocationByPiece(new ChessPiece(myTeam, ChessPiece.PieceType.KING));
        System.out.println("King's location: " + myKing);
        // Check if one of those moves is the kings spot
        for (ChessMove move: moves) {
            if(move.getEndPosition().equals(myKing)) {
                System.out.println(move);
                return true;
            }
        }
        // No attacking piece, returning false
        return false;
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
            ChessBoard tempBoard = createTempBoard(move);
            TeamColor myTeam = getMovingColor(move);
            boolean attackKing = isKingAttacked(tempBoard, myTeam);
            if (attackKing) {
                continue;
            }
            validMoves.add(move);
        }
        // If king could be put in check, remove it.
        System.out.println(validMoves);
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

        if (movingPiece == null) {
            throw new InvalidMoveException("There is no piece selected");
        }
        TeamColor pieceColor = movingPiece.getTeamColor();
        // Check if it's this pieces turn
        if (pieceColor != teamTurn) {
            throw new InvalidMoveException("It is not your turn");
        }
        // Check if this is a valid move
        Collection<ChessMove> possibleMoves = validMoves(startLocation);

        // if it's not a valid move, throw error
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException("Not a legal move");
        }

        // Move piece to spot
        chessBoard.movePiece(move);

        // Change team turn
        TeamColor opponent = pieceColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(opponent);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isKingAttacked(chessBoard, teamColor);
    }

    // Quick little helper function for check and stalemate
    private Collection<ChessMove> validTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> myTeamMoves = getTeamMoves(teamColor, chessBoard);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : myTeamMoves) {
            Collection<ChessMove> goodMoves = validMoves(move.getStartPosition());
            if (!goodMoves.isEmpty()) {
                validMoves.addAll(goodMoves);
            }
        }
        return validMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If it's got no valid moves and is in check, then it's in checkmate
        if (isInCheck(teamColor)) {
            Collection<ChessMove> validMoves = validTeamMoves(teamColor);
            return validMoves.isEmpty();
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
        Collection<ChessMove> validMoves = validTeamMoves(teamColor);
        return !isInCheck(teamColor) && validMoves.isEmpty();
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
