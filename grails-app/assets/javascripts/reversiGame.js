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
        this.enableSelection(selectables, 0);
    };

    this.enableSelection = function(selectables, pass) {
        if (selectables.length > 0) {
            selectables.forEach(function (item) {
                const id = TokenHandler.getTokenID(item['x'], item['y']);
                TokenHandler.enableSelection(id, 1);
            });
        } else {
            if (pass === 1) connection.pass();
        }
    };

    this.place = function(color, source, changes) {
        $.when(TokenHandler.placeToken(TokenHandler.getTokenID(source['x'], source['y']), color)).then(function () {
            changes.forEach(function (item) {
                const id = TokenHandler.getTokenID(item['x'], item['y']);
                TokenHandler.changeToken(id, color);
            });
        });
    };

    this.disableSelection = function () {
        TokenHandler.disableSelection($('.TOKEN').filter(function () {
            return TokenHandler.validate(this);
        }));
    };

    this.pass = function (color) {
        alert(`${color === 1 ? "White" : "Black"} has passed`);
    };

    this.end = function (win) {
        if (win > -2) alert(win > 0 ? "Victory" : (win < 0 ? "Defeat" : "Remis"));
    };
}