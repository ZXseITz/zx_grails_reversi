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
        TokenHandler.resetToken($(".token"));
    };

    this.setUp = function (color, selectables) {
        playerColor = color;
        TokenHandler.setColor($('#t33'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t44'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t34'), TokenHandler.BLACK.value);
        TokenHandler.setColor($('#t43'), TokenHandler.BLACK.value);
        this.enableSelection(selectables, 0);
        this.showInfo("new game started", "info");
    };

    this.enableSelection = function(selectables, pass) {
        if (selectables.length > 0) {
            selectables.forEach(function (item) {
                const id = TokenHandler.getTokenID(item['x'], item['y']);
                TokenHandler.enableSelection(id, playerColor);
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
        TokenHandler.disableSelection($('.token').filter(function () {
            return TokenHandler.validate(this);
        }));
    };

    this.updatePlacedTokens = function (placed) {
        $("#white").html(`${placed['white']}`);
        $("#black").html(`${placed['black']}`);
    };

    this.pass = function (color) {
        this.showInfo(`${color === 1 ? "White" : "Black"} has passed`, "info");
    };

    this.end = function (win) {
        this.showInfo(win > 0 ? "Victory" : (win < 0 ? "Defeat" : "Remis"), "info");
    };

    this.showInfo = function(text, type) {
        $("#infoimage").attr("src", `${$("#infoimage").data(type)}`);
        $("#infotext").html(text);
        const info = $("#info");
        info.fadeIn(500).css("display", "table");
        setTimeout(function() {
            info.fadeOut(500);
        }, 2000)
    };

    this.error = function (code) {
        TokenHandler.resetToken($(".token"));
        switch (code) {
            case COM.ERROR.GENERAL_ERROR:
                this.showInfo("General error", "error");
                break;
            case COM.ERROR.CONNECTION_ERROR:
                this.showInfo("Connection error", "error");
                break;
            case COM.ERROR.INVALID_GAME:
                this.showInfo("nvalid game", "error");
                break;
            case COM.ERROR.INVALID_ACTION:
                this.showInfo("Invalid action", "error");
                break;
            case  COM.ERROR.OPPONENT_DISCONNECTED:
                this.showInfo("Opponent has disconnected", "error");
                break;
        }
    }
}
