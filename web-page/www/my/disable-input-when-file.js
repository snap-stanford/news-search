/**
 * Created by Niko on 15/09/14.
 */

$( document ).ready(function() {

    /**
     * Disable input fields when file is selected
     * */
    function disableFields(idPrefix){
        return function(e) {
            e.stopPropagation();
            $("#"+idPrefix+"1").prop('disabled', true).get(0).value = "";
            $("#"+idPrefix+"2").prop('disabled', true).get(0).value = "";
            $("#"+idPrefix+"3").prop('disabled', true).get(0).value = "";
            $("#"+idPrefix+"4").prop('disabled', true).get(0).value = "";
            $("#"+idPrefix+"5").prop('disabled', true).get(0).value = "";
        };
    }

    /**
     * Re-enable input fields when file is removed
     * */
    function enableFields(idPrefix){
        return function(e) {
            e.stopPropagation();
            $("#"+idPrefix+"1").prop('disabled', false);
            $("#"+idPrefix+"2").prop('disabled', false);
            $("#"+idPrefix+"3").prop('disabled', false);
            $("#"+idPrefix+"4").prop('disabled', false);
            $("#"+idPrefix+"5").prop('disabled', false);
        };
    }

    var list = ["lang", "url", "keyword", "title", "content", "quote"];
    for(var id in list) {
        var wl = list[id] + "W";
        var bl = list[id] + "B";
        $("#" + wl).on("change.bs.fileinput", disableFields(wl)).on("clear.bs.fileinput", enableFields(wl));
        $("#" + bl).on("change.bs.fileinput", disableFields(bl)).on("clear.bs.fileinput", enableFields(bl));
    }
});