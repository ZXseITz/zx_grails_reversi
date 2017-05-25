<!doctype html>
<html>
<head>
    <title>Reversi</title>
    <asset:stylesheet src="reversi.css"/>
    <asset:javascript src="reversiConnection.js"/>
    <asset:javascript src="reversiCOM.js"/>
    <asset:javascript src="reversiUserAction.js"/>
    <asset:javascript src="TokenHandler.js"/>

    <asset:javascript src="application.js"/>
    <asset:javascript src="spring-websocket"/>
    <script type="text/javascript">
        // global use
        let game;
        const url = "${createLink(uri: '/reversi/room', absolute: true).replaceFirst(/http/, /ws/)}";
        window.onload = function () {
            game = new Game();
            game.getConnection().connect(url);
        };
    </script>
</head>

<body>
<%
    //needs to stay in body todo remove in production code
    double width = 400
    double height = 400
    double a = width / 8
    double b = height / 8
    double r = Math.min(a, b) * 3 / 8

    int COLOR_UNDEFINIED = 0
    int COLOR_WHITE = 1
    int COLOR_BLACK = 2

    int GAME_BOT = 0
    int GAME_PVP = 1
%>
<svg width="${width}" height="${height}" class="svg">
<!-- draw grid -->
    <g:each var="i" in="${0..8}">
        <line x1="0.0" y1="${i * b}" x2="${width}" y2="${i * b}" class="grid"></line>
        <line x1="${i * a}" y1="0.0" x2="${i * a}" y2="${height}" class="grid"></line>
    </g:each>

<!-- draw tokens -->
    <g:each var="v" in="${0..7}">
        <g:each var="u" in="${0..7}">
            <circle
                    id="t${u}${v}"
                    r="${r}"
                    cx="${u * a + a / 2}"
                    cy="${v * b + b / 2}"
                    class="TOKEN"
                    data-selectable="0"
                    data-u="${u}"
                    data-v="${v}"
                    onclick="userAction.click(this)"></circle>
        </g:each>
    </g:each>
</svg>
<br>

<div>
    <label>Withe Tokens:</label>
    <label id="whites">0</label>
</div>

<div>
    <label>Black Tokens:</label>
    <label id="blacks">0</label>
</div>

<div>
    <label>Select color</label>
    <select id="gamecolor">
        <option value="${COLOR_WHITE}" selected>white</option>
        <option value="${COLOR_BLACK}">black</option>
    </select>
    <br>
    <label>Select game type</label>
    <select id="gametype">
        <option value="${GAME_BOT}" selected>bot</option>
        <option value="${GAME_PVP}">pvp</option>
    </select>
    <br>
    <button type="button" onclick="userAction.newGame()">new game</button>
</div>
</body>
</html>
