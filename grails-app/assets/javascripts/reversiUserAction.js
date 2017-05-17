/**
 * Created by Claudio on 16.05.2017.
 */
function userAction() {
}

userAction.mouseOver = function(e) {
    if (validateToken(e)) tokenEffects.fadeInToken(e, 'WHITE');
};

userAction.mouseOut = function(e) {
    if (validateToken(e)) tokenEffects.fadeOutToken(e);
};

userAction.click = function(e) {
    if (validateToken(e)) connection.place($(e).data('u'), $(e).data('v'))
};

function validateToken(e) {
    return $(e).data('selectable') === 1;
}
