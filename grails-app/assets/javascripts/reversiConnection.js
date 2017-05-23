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
            console.log("error: ");
            console.log(e);
        };

        socket.onmessage = function(message) {
            let json = JSON.parse(message.data);
            console.log("JSON Data:");
            console.log(json);
            switch (json.type) {
                case COM.SERVER_INIT:
                    game.setUp(json.data["color"], json.data["selection"]);
                    break;
                case COM.SERVER_PLACE_CLIENT:
                    game.place(json.data["color"], json.data["source"], json.data["changes"]);
                    break;
                case COM.SERVER_PLACE_OPPONENT:
                    game.place(json.data["color"], json.data["source"], json.data["changes"]);
                    game.setSelectable(json.data["selection"]);
                    game.end(json.data["win"]);
                    break;
                case COM.SERVER_PASS_CLIENT:
                    game.pass(json.data["color"]);
                    break;
                case COM.SERVER_PASS_OPPONENT:
                    game.pass(json.data["color"]);
                    game.setSelectable(json.data["selection"]);
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
        console.log("sended: " + json);
        socket.send(json);
    }

    this.botGame = function () {
        sendJSON(COM.CLIENT_NEW_BOT_GAME);
    };

    this.place = function (u, v) {
        sendJSON(COM.CLIENT_PLACE, {'x': u, 'y': v});
    };

    this.pass = function () {
        sendJSON(COM.CLIENT_PASS);
    };
}
