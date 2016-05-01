
var $ = jQuery.noConflict();


function submitBetaTestForm() {
    $.ajax({
        type: 'POST',
        url: $("#subscribeBetaTestForm").attr("action"),
        data: $("#subscribeBetaTestForm").serialize(), 
        success: function(response) {
            $("#subscribeBetaTestForm .alert-success").fadeIn('slow');
        },
        error: function(response) {
            $("#subscribeBetaTestForm .alert-danger").fadeIn('slow');
        }
    });
}

(function($) {
    $("#subscribeBetaTestForm .alert-success").hide();
    $("#subscribeBetaTestForm .alert-danger").hide();
})(jQuery);