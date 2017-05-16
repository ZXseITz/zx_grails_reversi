/**
 * Created by Claudio on 15.05.2017.
 */

function Effects() {
    /**
     * Fades e in
     * @param e element to effect
     * @param c css class for preview
     */
    Effects.fadeIn = function (e, c) {
        // console.log("faded in");
        $(e).attr("class", c);
        $(e).css("opacity", "0.5");
        $(e).css("display", "none");
        $(e).fadeIn(250);
    };

    /**
     * Fades e out
     * @param e element to effect
     */
    Effects.fadeOut = function (e) {
        // console.log("faded out");
        $.when($(e).fadeOut(250)).then(function () {
            $(e).attr("class", "UNDEF");
            $(e).css("opacity", "1");
            $(e).css("display", "inline");
        });
    };

    /**
     * Sets the color of source and changes the color of all targets
     * @param source
     * @param targets
     * @param c color
     */
    Effects.place = function (source, targets, c) {
        //todo implement
    };
}

