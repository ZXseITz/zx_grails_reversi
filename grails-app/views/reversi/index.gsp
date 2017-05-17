<!doctype html>
<html>
<head>
    <title>Reversi</title>
    <asset:stylesheet src="reversi.css"/>
    <asset:javascript src="reversiConnection.js"/>
    <asset:javascript src="reversiCOM.js"/>
    <asset:javascript src="reversiUserAction.js"/>
    <asset:javascript src="reversiEffects.js"/>

    <asset:javascript src="application.js"/>
    <asset:javascript src="spring-websocket"/>
    <script type="text/javascript">
        // global use
        let connection;
        window.onload = function () {
            connection = new Connection("ws://localhost:8080/reversi/annotated");
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
%>
<svg width="${width}" height="${height}" class="svg">
<!-- draw grid -->
    <g:each var="i" in="${0..8}">
        <line x1="0.0" y1="${i * b}" x2="${width}" y2="${i * b}" class="grid"></line>
        <line x1="${i * a}" y1="0.0" x2="${i * a}" y2="${height}" class="grid"></line>
    </g:each>

<!-- draw tokens -->
    <g:each var="tokenRow" in="${boardModel.tokens}">
        <g:each var="token" in="${tokenRow}">
            <circle
                    id="t${token.getU()}${token.getV()}"
                    r="${r}"
                    cx="${token.u * a + a / 2}"
                    cy="${token.v * b + b / 2}"
                    class="${token.color.toString()}"
                    data-selectable="1"
                    data-u="${token.getU()}"
                    data-v="${token.getV()}"
                    onmouseover="handleMouseOver(this)"
                    onmouseout="handleMouseOut(this)"
                    onclick="handleClick(this)"
            ></circle>
        </g:each>
    </g:each>
</svg>
</body>
</html>
