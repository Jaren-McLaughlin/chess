package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";

    public static final String KING = " ♚ ";
    public static final String QUEEN = " ♛ ";
    public static final String BISHOP = " ♝ ";
    public static final String KNIGHT = " ♞ ";
    public static final String ROOK = " ♜ ";
    public static final String PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";
    public static final String BG_DARK_BROWN = UNICODE_ESCAPE + "[48;2;69;23;0m";
    public static final String BG_LIGHT_BROWN = UNICODE_ESCAPE + "[48;2;188;137;95m";
    public static final String BG_MEDIUM_BROWN = UNICODE_ESCAPE + "[48;2;152;81;45m";
    public static final String TXT_GOLD = UNICODE_ESCAPE + "[38;2;255;199;0m";
    public static final String CLEAR = UNICODE_ESCAPE + "[0m";
    public static final String NEW_LINE = CLEAR + "\n";
    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }
}
