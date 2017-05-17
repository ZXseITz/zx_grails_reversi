/**
 * Created by Claudio on 16.05.2017.
 */
function Connection(link) {
    const socket = new SockJS(link);
    const client = Stomp.over(socket);

    client.connect({}, function () {
        client.subscribe("/topic/messages", onMessage)
    });

    function onMessage(message) {
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
    }

    function sendJSON(type, data) {
        client.send(JSON.stringify({
            'type': type,
            'data': data
        }));
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