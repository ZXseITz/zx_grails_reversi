/**
 * Created by Claudio on 21.05.2017.
 */

function Game() {
    let connection = new Connection(this);
    let playerColor;
    let selectableTokens;

    this.getConnection = function () {
        return connection;
    };

    this.getPlayerColor = function () {
        return playerColor
    };

    this.setUpUI = function () {
        var all = $(".TOKEN");
        TokenHandler.setColor(all, TokenHandler.UNDEF);
        TokenHandler.setSelectable(all, 0);

        TokenHandler.setColor($('#t33'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t44'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t34'), TokenHandler.BLACK.value);
        TokenHandler.setColor($('#t43'), TokenHandler.BLACK.value);
    };

    this.setUp = function (color, selectables) {
        playerColor = color;
        console.log("Color: " + color);
        selectableTokens = [];
        selectables.forEach(function (item) {
            const id = TokenHandler.getTokenID(item['x'], item['y']);
            TokenHandler.setSelectable(id, 1);
            selectableTokens.push(id)
        });
    };

    this.place = function() {

    };

    this.pass = function () {

    };

    this.end = function () {

    };
}