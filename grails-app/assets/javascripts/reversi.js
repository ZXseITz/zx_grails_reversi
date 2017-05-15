/**
 * Created by Claudio on 15.05.2017.
 */

/**
 * Fades e in
 * @param e element to effect
 * @param c css class for preview
 */
function fadein(e, c) {
    console.log("faded in");
    $(e).attr("class", c);
    $(e).fadeIn(250);
}

/**
 * Fades e out
 * @param e element to effect
 */
function fadeout(e) {
    console.log("faded out");
    $.when($(e).fadeOut(250)).done(function () {
        $(e).attr("class", "UNDEF");
    });
}

function send() {

}
