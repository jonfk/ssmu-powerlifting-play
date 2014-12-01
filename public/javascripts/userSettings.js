
// Styling for File input plugin
$("#image-input").fileinput({showUpload: false,
	maxFileSize: 1000,
	maxFileCount: 1,
	allowedFileTypes: ["image"],
	mainClass: "input-group"});

$(document).ready(function(){
	

// Sidebar js
$(function() {
    $('#sidebar a').on('click', function() {
        $('#'+$(this).data('id')).show().siblings('div').hide();
    });
});


// record edit js
$(function() {
    $('#records-table button').on('click', function() {
        $('#'+$(this).data('id')).show().siblings('div').hide();
    });
});

// record cancel button
$(function() {
    $('#records-edit-list').children().find('#cancel').on('click', function() {
        $('#'+$(this).data('id')).hide();
    	//alert("cancel button pressed");
    });
});

});