/**
 * Created by Claudio on 16.05.2017.
 */
function handleMouseOver(e) {
    if (validateToken(e)) fadeInToken(e, 'WHITE');
}

function handleMouseOut(e) {
    if (validateToken(e)) fadeOutToken(e);
}

function handleClick(e) {
    if (validateToken(e)) connection.place($(e).data('u'), $(e).data('v'))
}

function validateToken(e) {
    return $(e).data('selectable') === 1;
}