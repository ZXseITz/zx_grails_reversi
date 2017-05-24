/**
 * Created by Claudio on 15.05.2017.
 */


function TokenHandler() {
}

TokenHandler.UNDEF = {value: 0, name: "forestgreen"};
TokenHandler.WHITE = {value: 1, name: "white"};
TokenHandler.BLACK = {value: 2, name: "black"};


TokenHandler.setColor = function (e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
};

TokenHandler.enableSelection = function (e, c) {
    $(e).css("fill", TokenHandler.getColorFromValue(c));
    $(e).css("opacity", "0.25");
    $(e).data('selectable', 1);
};

TokenHandler.disableSelection = function (e) {
    $(e).css("fill", TokenHandler.UNDEF.name);
    $(e).css("opacity", "1");
    $(e).data('selectable', 0);
};

TokenHandler.resetToken = function (e) {
    $(e).data('selectable', 0);
    $(e).css({fill: TokenHandler.UNDEF.name, transition: "0s"});
    $(e).css("opacity", "1");
};

TokenHandler.validate = function(e) {
    return $(e).data('selectable') === 1;
};

TokenHandler.placeToken = function(e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
};

TokenHandler.changeToken = function(e, colorValue) {
    $(e).css({fill: TokenHandler.getColorFromValue(colorValue), transition: "1s"});
};

TokenHandler.getTokenID = function(x, y) {
    return "#t" + x + y;
};

TokenHandler.getColorFromValue = function (value) {
    switch (value) {
        case TokenHandler.UNDEF.value:
            return TokenHandler.UNDEF.name;
            break;
        case TokenHandler.WHITE.value:
            return TokenHandler.WHITE.name;
            break;
        case TokenHandler.BLACK.value:
            return TokenHandler.BLACK.name;
            break;
        default:
            return null;
    }
};
