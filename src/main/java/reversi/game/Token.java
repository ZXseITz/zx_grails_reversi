package reversi.game;

/**
 * Created by Claudio on 08.02.2017.
 */
public class Token {
    public enum Color {
        UNDEFINED("UNDEF", 0),
        WHITE("WHITE", 1),
        BLACK("BLACK", 2);

        private String name;
        private int value;
        Color(String name, int value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return name;
        }
        public int getValue() {
            return value;
        }
    }

    public static Color getColorFromValue(int value) {
        if (value == 1) return Color.WHITE;
        else if (value == 2) return Color.BLACK;
        else return Color.UNDEFINED;
    }

    public static Color getOpposite(Color color) {
        if (color == Color.WHITE) return Color.BLACK;
        else if (color == Color.BLACK) return Color.WHITE;
        else return Color.UNDEFINED;
    }

    public static String arrayToString(Token[] tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        if (tokens.length > 0) {
            sb.append(tokens[0]);
            for (int i = 1; i < tokens.length; i++) sb.append(", ").append(tokens[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }

    private volatile Color color;
    private final int u, v;

    public Token(int u, int v) {
        this.color = Color.UNDEFINED;
        this.u = u;
        this.v = v;
    }

    /**
     * Copy constructor
     * @param token Original to clone
     */
    private Token(Token token) {
        this.color = token.color;
        this.u = token.u;
        this.v = token.v;
    }

    public boolean isWhite() {
        return color == Color.WHITE;
    }
    public boolean isBlack() {
        return color == Color.BLACK;
    }
    public boolean isPlaced() {
        return color != Color.UNDEFINED;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public Token clone() {
        return new Token(this);
    }

    @Override
    public String toString() {
        return u + ", " + v + ", " + color;
    }
}
