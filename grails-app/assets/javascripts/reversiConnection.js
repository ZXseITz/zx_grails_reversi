/**
 * Created by Claudio on 16.05.2017.
 */
function Connection(game) {
    let socket;

    this.connect = function (url) {
        socket = new WebSocket(url);

        socket.onopen = function () {
            console.log("connected to " + socket.url);
        };

        socket.onclose = function () {
            console.log("disconnected from " + socket.url);
        };

        socket.onerror = function (e) {
            console.log("error:");
            console.log(e);
        };

        socket.onmessage = function(message) {
            let json = JSON.parse(message.data);
            console.log("received:");
            console.log(json);
            switch (json.type) {
                case COM.SERVER_INIT:
                    game.setUp(json.data["color"], json.data["selection"]);
                    game.updatePlacedTokens(json.data["placed"]);
                    break;
                case COM.SERVER_ERROR:
                    game.error(json.data["error"]);
                    break;
                case COM.SERVER_PLACE_CLIENT:
                    game.place(json.data["color"], json.data["source"], json.data["changes"]);
                    game.updatePlacedTokens(json.data["placed"]);
                    break;
                case COM.SERVER_PLACE_OPPONENT:
                    game.place(json.data["color"], json.data["source"], json.data["changes"]);
                    game.updatePlacedTokens(json.data["placed"]);
                    game.enableSelection(json.data["selection"], json.data["pass"]);
                    game.end(json.data["win"]);
                    break;
                case COM.SERVER_PASS_CLIENT:
                    game.pass(json.data["color"]);
                    break;
                case COM.SERVER_PASS_OPPONENT:
                    game.pass(json.data["color"]);
                    game.enableSelection(json.data["selection"], json.data["pass"]);
                    game.end(json.data["win"]);
                    break;
                case COM.SERVER_END:
                    game.end(json.data["win"]);
                    break;
            }
        };
    };

    this.disconnect = function() {
        socket.close();
    };

    function sendJSON(type, data) {
        let json = JSON.stringify({
            'type': type,
            'data': data
        });
        console.log("send: " + json);
        socket.send(json);
    }

    this.newGame = function (color, gtype) {
        sendJSON(COM.CLIENT_NEW_GAME, {'color': color, 'gameType': gtype});
    };

    this.place = function (u, v) {
        sendJSON(COM.CLIENT_PLACE, {'x': u, 'y': v});
    };

    this.pass = function () {
        sendJSON(COM.CLIENT_PASS);
    };
}
