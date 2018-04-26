$(function () {
  $(".select input[type='checkbox']").on("change",function(){
  	var on = $(this).prop("checked"),
  		t = $(this).parent().parent(),
  		sa = $(".select-all input[type='checkbox']");
  	if(on) {
  		t.addClass("selected");
  	}else{
  		t.removeClass("selected");
  		if(sa.prop("checked")) {
  			sa.prop('checked', false)
  		}
  	}
  });

  $(".select-all input[type='checkbox']").on("change",function(){ 
	var on = $(this).prop("checked"),
		t = $(".select input[type='checkbox']");
	if(on) {
		t.prop('checked', true).change();
	}else{
		t.prop('checked', false).change();
	}

  })





})