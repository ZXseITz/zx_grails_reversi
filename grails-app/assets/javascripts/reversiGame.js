/**
 * Created by Claudio on 21.05.2017.
 */

function Game() {
    let connection = new Connection(this);
    let playerColor;

    /**
     * Gets the websocket connection
     * @return {Connection}
     */
    this.getConnection = function () {
        return connection;
    };

    this.getPlayerColor = function () {
        return playerColor
    };

    /**
     * Clears the UI, ready for the next game
     */
    this.setUpUI = function () {
        TokenHandler.resetToken($(".token"));
        $("#info").css("background", "white");
    };

    /**
     * Initializes a new game and inform the client
     * @param color color of this client
     * @param selectables selectable tokens
     */
    this.setUp = function (color, selectables) {
        playerColor = color;
        TokenHandler.setColor($('#t33'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t44'), TokenHandler.WHITE.value);
        TokenHandler.setColor($('#t34'), TokenHandler.BLACK.value);
        TokenHandler.setColor($('#t43'), TokenHandler.BLACK.value);
        this.enableSelection(selectables, 0);
        this.showInfo(`new game started as ${color}`, "info");
    };

    /**
     * Enable selection for defined tokens
     * @param selectables selectable tokens
     * @param pass enable auto passing
     */
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

    /**
     * Disable selection of all tokens
     */
    this.disableSelection = function () {
        TokenHandler.disableSelection($('.token').filter(function () {
            return TokenHandler.validate(this);
        }));
    };

    /**
     * Update the number of placed tokens
     * @param placed numbers {white, black}
     */
    this.updatePlacedTokens = function (placed) {
        $("#white").html(`${placed['white']}`);
        $("#black").html(`${placed['black']}`);
    };

    /**
     * Visualizes an incoming place action
     * @param color color of actor
     * @param source new placed token
     * @param changes token who's color has changed
     */
    this.place = function(color, source, changes) {
        $.when(TokenHandler.placeToken(TokenHandler.getTokenID(source['x'], source['y']), color)).then(function () {
            changes.forEach(function (item) {
                const id = TokenHandler.getTokenID(item['x'], item['y']);
                TokenHandler.changeToken(id, color);
            });
        });
    };

    /**
     * Visualizes an incoming
     * @param color
     */
    this.pass = function (color) {
        this.showInfo(`${color === 1 ? "White" : "Black"} has passed`, "info");
    };

    /**
     * Visualizes the end of a round
     * @param win 1 Victory, -1 Defeat, 0 Tie
     */
    this.end = function (win) {
        if (win > 0) {
            $("#info").css("background", "#ddddff");
            this.showInfo("Victory", "info");
        } else if(win < 0) {
            $("#info").css("background", "#ffdddd");
            this.showInfo("Defeat", "info");
        } else {
            this.showInfo("Tie", "info");
        }
    };

    /**
     * Shows an information for the client
     * @param text information text
     * @param type defines icon 'info' or 'error'
     */
    this.showInfo = function(text, type) {
        $("#infoimage").attr("src", `${$("#infoimage").data(type)}`);
        $("#infotext").html(text);
        const info = $("#info");
        info.fadeIn(500).css("display", "table");
        setTimeout(function() {
            info.fadeOut(500);
        }, 2000)
    };

    /**
     * Shows an error message for the client
     * @param code
     */
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
