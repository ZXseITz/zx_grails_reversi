/**
 * Created by Claudio on 15.05.2017.
 */


function TokenHandler() {
}

TokenHandler.UNDEF = {value: 0, name: "forestgreen"};
TokenHandler.WHITE = {value: 1, name: "white"};
TokenHandler.BLACK = {value: 2, name: "black"};

/**
 * Fades e in
 * @param e element to effect
 * @param colorValue css class for preview
 */
TokenHandler.fadeInToken = function(e, colorValue) {
    // console.log("faded in");
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
    $(e).css("opacity", "0.5");
    $(e).css("display", "none");
    $(e).fadeIn(250);
};

/**
 * Fades e out
 * @param e element to effect
 */
TokenHandler.fadeOutToken = function (e) {
    // console.log("faded out");
    $.when($(e).fadeOut(250)).then(function () {
        $(e).css("fill", TokenHandler.UNDEF.name);
        $(e).css("opacity", "1");
        $(e).css("display", "inline");
    });
};

/**
 * Sets the color of source and changes the color of all targets
 * @param source
 * @param targets
 * @param c color
 */
TokenHandler.place = function(source, targets, c) {

};

TokenHandler.setColor = function (e, colorValue) {
    $(e).css("fill", TokenHandler.getColorFromValue(colorValue));
};

TokenHandler.setSelectable = function (e, s) {
    $(e).data('selectable', s);
};



TokenHandler.validate = function(e) {
    return $(e).data('selectable') === 1;
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
