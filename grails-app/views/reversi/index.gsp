<!doctype html>
<html>
<head>
    <title>Reversi</title>
    <style>
    .svg {
        background: forestgreen;
    }

    .UNDEF {
        fill: forestgreen;
        border: none;
    }

    .WHITE {
        fill: white;
    }

    .BLACK {
        fill: black;
    }

    .grid {
        stroke: black;
        stroke-width: 1px;
    }
    </style>
</head>
<body>
<%
    //needs to stay in body todo remove in production code
    double width = 400
    double height = 400
    double a = width/8
    double b = height/8
    double r = Math.min(a, b) * 3/8
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
                        r="${r}"
                        cx="${token.u * a + a/2}"
                        cy="${token.v * b + b/2}"
                        class="${token.color.toString()}"
                ></circle>
            </g:each>
        </g:each>
    </svg>
</body>
</html>