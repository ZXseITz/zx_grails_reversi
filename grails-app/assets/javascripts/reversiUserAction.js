/**
 * Created by Claudio on 16.05.2017.
 */
function UserAction() {
    UserAction.mouseOver = function (e) {
        if (validate(e)) Effects.fadeIn(e, 'WHITE');
    };

    UserAction.mouseOut = function (e) {
        if (validate(e)) Effects.fadeOut(e);
    };

    UserAction.click = function (e) {
        if (validate(e)) connection.place($(e).data('u'), $(e).data('v'))
    };

    function validate(e) {
        return $(e).data('selectable') === 1;
    }
}