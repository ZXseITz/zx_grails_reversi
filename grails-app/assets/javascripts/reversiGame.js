/**
 * Created by Claudio on 21.05.2017.
 */

function Game() {
    let connection = new Connection(this);
    let playerColor;

    this.getConnection = function () {
        return connection;
    };

    this.getPlayerColor = function () {
        return playerColor
    };

    this.setUpUI = function () {
        TokenHandler.resetToken($(".TOKEN"));

        TokenHandler.setColor($('#t33'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t44'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t34'), TokenHandler.BLACK.value);
        TokenHandler.setColor($('#t43'), TokenHandler.BLACK.value);
    };

    this.setUp = function (color, selectables) {
        playerColor = color;
        this.setSelectable(selectables);
    };

    this.setSelectable = function(selectables) {
        selectables.forEach(function (item) {
            const id = TokenHandler.getTokenID(item['x'], item['y']);
            TokenHandler.setSelectable(id, 1);
        });
    }

    this.place = function(color, source, changes) {
        $.when(TokenHandler.placeToken(TokenHandler.getTokenID(source['x'], source['y']), color)).then(function () {
            changes.forEach(function (item) {
                const id = TokenHandler.getTokenID(item['x'], item['y']);
                TokenHandler.changeToken(id, color);
            });
        });
    };

    this.disableSelection = function () {
        TokenHandler.setSelectable($(".TOKEN"), 0);
    };

    this.pass = function () {

    };

    this.end = function () {

    };
}