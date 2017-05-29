/**
 * Created by Claudio on 15.05.2017.
 */

function TokenHandler() {
}

/*
token color objects
 */
TokenHandler.UNDEF = {value: 0, name: "forestgreen"};
TokenHandler.WHITE = {value: 1, name: "white"};
TokenHandler.BLACK = {value: 2, name: "black"};

/**
 * Sets the color of a token
 * @param e token
 * @param colorValue
 */
TokenHandler.setColor = function (e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
};

/**
 * Defines a token as selectable
 * @param e token
 * @param colorValue 1 white, 2 black
 */
TokenHandler.enableSelection = function (e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
    $(e).css("opacity", "0.25");
    $(e).data('selectable', 1);
};

/**
 * Define a token as non-selectable
 * @param e
 */
TokenHandler.disableSelection = function (e) {
    $(e).css("fill", TokenHandler.UNDEF.name);
    $(e).css("opacity", "1");
    $(e).data('selectable', 0);
};

/**
 * Resets a token for a new game
 * @param e token
 */
TokenHandler.resetToken = function (e) {
    $(e).data('selectable', 0);
    $(e).css({fill: TokenHandler.UNDEF.name, transition: "0s"});
    $(e).css("opacity", "1");
};

/**
 * Chechs if a token is selectable
 * @param e token to check
 * @return {boolean}
 */
TokenHandler.validate = function(e) {
    return $(e).data('selectable') === 1;
};

/**
 * Sets the
 * @param e token
 * @param colorValue 1 white, 2 black
 */
TokenHandler.placeToken = function(e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
};

/**
 * Changes the color of an token
 * @param e token
 * @param colorValue 1 white, 2 black
 */
TokenHandler.changeToken = function(e, colorValue) {
    $(e).css({fill: TokenHandler.getColorFromValue(colorValue), transition: "1s"});
};

TokenHandler.getTokenID = function(x, y) {
    return "#t" + x + y;
};

/**
 * Returns the color of an value
 * @param value 1 white, 2 black, otherwise undef
 * @return {*}
 */
TokenHandler.getColorFromValue = function (value) {
    switch (value) {
        case TokenHandler.WHITE.value:
            return TokenHandler.WHITE.name;
            break;
        case TokenHandler.BLACK.value:
            return TokenHandler.BLACK.name;
            break;
        default:
            return TokenHandler.UNDEF.name;
    }
};
