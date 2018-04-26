$(function () {
  $( window ).resize(function() {

    var userlist = $("#messageusers"),
        form = $(".form-message-sender"),
        conversation = $("#conversation");


        userlist.height($(this).height() - userlist.offset().top);
        form.outerWidth(conversation.width());
        conversation.height($(this).height() - conversation.offset().top - form.outerHeight());

  });

  $( window ).resize();

});