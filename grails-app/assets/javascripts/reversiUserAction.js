/**
 * Created by Claudio on 16.05.2017.
 */
function userAction() {
}

userAction.mouseOver = function(e) {
    if (TokenHandler.validate(e)) TokenHandler.fadeInToken(e, game.getPlayerColor());
};

userAction.mouseOut = function(e) {
    if (TokenHandler.validate(e)) TokenHandler.fadeOutToken(e);
};

userAction.click = function(e) {
    if (TokenHandler.validate(e)) game.getConnection().place($(e).data('u'), $(e).data('v'))
};

userAction.newBotGame = function() {
    game.setUpUI();
    game.getConnection().botGame();
};
