/**
 * Created by Claudio on 16.05.2017.
 */
function userAction() {
}

/**
 * Handles the click action
 * @param e token
 */
userAction.click = function(e) {
    if (TokenHandler.validate(e)) {
        game.disableSelection();
        game.getConnection().place($(e).data('u'), $(e).data('v'))
    }
};

/**
 * Handles the new game action
 */
userAction.newGame = function() {
    const color = $("#gamecolor").val();
    const gType = $("#gametype").val();
    game.setUpUI();
    game.getConnection().newGame(color, gType);
};
