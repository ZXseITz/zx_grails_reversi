package reversi;

/**
 * Created by Claudio on 08.02.2017.
 */
public class Token {
    public enum Color {
        UNDEFINED("UNDEF", "UNDEF"),
        WHITE("WHITE", "WHITEFADE"),
        BLACK("BLACK", "BLACKFADE");

        private String name, fade;
        Color(String name, String fade) {
            this.name = name;
            this.fade = fade;
        }
        @Override
        public String toString() {
            return name;
        }
        public String getFade() {
            return fade;
        }
    }

    public static Color getOpposite(Color color) {
        if (color == Color.WHITE) return Color.BLACK;
        else if (color == Color.BLACK) return Color.WHITE;
        else return Color.UNDEFINED;
    }

    private Color color, fade;
    private final int u, v;

    public Token(int u, int v) {
        this.color = Color.UNDEFINED;
        this.fade = Color.UNDEFINED;
        this.u = u;
        this.v = v;
    }

    /**
     * Copy constructor
     * @param token Original to clone
     */
    private Token(Token token) {
        this.color = token.color;
        this.fade = token.fade;
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

    public Color getFade() {
        return fade;
    }

    public void setFade(Color fade) {
        this.fade = fade;
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
