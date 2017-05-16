/**
 * Created by Claudio on 16.05.2017.
 */
function handleMouseOver(e) {
    if (validateToken(e)) Effects.fadeIn(e, 'WHITE');
}

function handleMouseOut(e) {
    if (validateToken(e)) Effects.fadeOut(e);
}

function handleClick(e) {
    if (validateToken(e)) connection.place($(e).data('u'), $(e).data('v'))
}

function validateToken(e) {
    return $(e).data('selectable') === 1;
}