/**
 * Created by Claudio on 16.05.2017.
 */
function Connection(link) {
    const socket = new WebSocket(link);

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

    socket.onMessage = function(message) {
        let json = JSON.parse(message.data);
        console.log("JSON Data:");
        console.log(json);
        switch (json.type) {
            case COM.SERVER_INIT:
            case COM.SERVER_PLACE:
            case COM.SERVER_PASS:
            case COM.SERVER_VICTORY:
            case COM.SERVER_DEFEAT:
            case COM.SERVER_REMIS:
        }
    };

    function sendJSON(type, data) {
        var json = JSON.stringify({
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
