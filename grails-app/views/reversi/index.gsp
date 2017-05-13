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
    </style>
</head>
<body>
    <svg width="400" height="400" class="svg">
        <g:each var="tokenRow" in="${boardModel.tokens}">
            <g:each var="token" in="${tokenRow}">
                <circle
                        r="18.75"
                        cx="${token.u * 50 + 25}"
                        cy="${token.v * 50 + 25}"
                        class="${token.color.toString()}"
                ></circle>
            </g:each>
        </g:each>
    </svg>
</body>
</html>