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
<!-- game board -->
<svg width="${width}" height="${height}" class="game">
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
                    class="token"
                    %{-- save token specific data --}%
                    data-selectable="0"
                    data-u="${u}"
                    data-v="${v}"
                    onclick="userAction.click(this)"></circle>
        </g:each>
    </g:each>
</svg>
<br>

<div class="all font">
    <!-- ingame token counter -->
    <div class="counter">
        <span id="white" class="counterlabel left white">0</span>
        <span id="black" class="counterlabel right black">0</span>
    </div>
    <br>
    <!-- new game user option -->
    <div id="newgame" align="center" class="box">
        <div>
            <label for="gamecolor" class="left">Select color</label>
            <select id="gamecolor" class="combobox font right">
            <option value="${COLOR_WHITE}" selected>white</option>
            <option value="${COLOR_BLACK}" class="black">black</option>
        </select>
        </div>
        <div>
            <label for="gametype" class="left">Select game type</label>
            <select id="gametype" class="combobox font right">
            <option value="${GAME_BOT}" selected>bot</option>
            <option value="${GAME_PVP}">pvp</option>
        </select>
        </div>
        <div>
            <button type="button" class="newgamebutton font" onclick="userAction.newGame()">new game</button>
        </div>
    </div>

    <!-- infobox tooltip for ingame information or error messages -->
    <div id="info" class="box">
        <div id="infoimagecell" class="infocell">
            <!-- save image path in <img> -->
            <img id="infoimage" src="" data-info="${asset.assetPath(src: 'info.png')}"
                 data-error="${asset.assetPath(src: 'error.png')}">
        </div>

        <div class="infocell">
            <div id="infotext"></div>
        </div>
    </div>
</div>

</body>
</html>
