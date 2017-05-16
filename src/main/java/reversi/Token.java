package reversi;

/**
 * Created by Claudio on 08.02.2017.
 */
public class Token {
    public enum Color {
        UNDEFINED("UNDEF"),
        WHITE("WHITE"),
        BLACK("BLACK");

        private String name;
        Color(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    public static Color getOpposite(Color color) {
        if (color == Color.WHITE) return Color.BLACK;
        else if (color == Color.BLACK) return Color.WHITE;
        else return Color.UNDEFINED;
    }

    private Color color;
    private byte selectable;
    private final int u, v;

    public Token(int u, int v) {
        this.color = Color.UNDEFINED;
        this.selectable = 0;
        this.u = u;
        this.v = v;
    }

    /**
     * Copy constructor
     * @param token Original to clone
     */
    private Token(Token token) {
        this.color = token.color;
        this.selectable = token.selectable;
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

    public byte isSelectable() {
        return selectable;
    }

    public void setSelectable(byte selectable) {
        this.selectable = selectable;
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
