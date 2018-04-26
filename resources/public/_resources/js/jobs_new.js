$(function() {
	var temp_company_photo = "../assets/img/company-placeholder.png",
		main_company_photo = $('#job_preview_holder .photo img').attr("src");
	var updateData = function() {
		var value = $(this).val(),
			name = $(this).attr("id"),
			checked = $(this).is(":checked"),
			target = $('#j_' + name);
			
		// console.log("name: " + name + " || value: " + value + " || checked: " + checked);

		if(name == "is_secret") {
			if(checked){
				$('#j_company').text("Bir " + $('#company_industry').val() + " Şirketi");
				$('#job_preview_holder .photo img').attr("src",temp_company_photo);
			}else{
				$('#j_company').text($('#company').val());
				$('#job_preview_holder .photo img').attr("src",main_company_photo);
			};
		}
		if(value == "") { 
			value = $(this).attr("placeholder");
			target.addClass('disable');
		}else{
			target.removeClass('disable')
		}
		target.text(value);
	}

	$("form input, form textarea, form select").each(updateData).on("keyup change",updateData);

	CheckOutAddOne = function(){
		if($('#expire_date').val() == $(this).val() ) {
			CheckIn = $(this).val();
			CheckOut = moment(CheckIn, "DD/MM/YYYY").add(1, 'month').format("DD/MM/YYYY");
			$('#expire_date').datepicker('update',CheckOut);
		}
	}

	$('#datepicker').datepicker({
	    format: "dd/mm/yyyy",
	    todayBtn: "linked",
	    language: "tr",
	    autoclose: false,
	    todayHighlight: true
	});

	$('#created_date').on("change",CheckOutAddOne);

	// TypeAhead
	var tokenInputs = [];

	$('input.tokenfield').each(function(){
		var name = $(this).attr("id");
		tokenInputs[name] = new Bloodhound({
			// local: [{value: 'red'}, {value: 'blue'}, {value: 'green'} , {value: 'yellow'}, {value: 'violet'}, {value: 'brown'}, {value: 'purple'}, {value: 'black'}, {value: 'white'}],
			datumTokenizer: Bloodhound.tokenizers.whitespace,
			queryTokenizer: Bloodhound.tokenizers.whitespace,
			prefetch: {
				cache: false,
				url: '../data/' + name + '.json'
				// url: '../data/' + name + '.php'
			}
		});
		tokenInputs[name].initialize();

		$(this).tokenfield({
			showAutocompleteOnFocus : true,
			typeahead: [ { 	
					highlight: true,
					limit: 10, 
				}, { 
					source: tokenInputs[name].ttAdapter()
				}
			]
		});
	});


	// $( "form" ).submit(function( event ) {
	//   	console.log( $(this).serialize() );
	//   	event.preventDefault();
	// });




 });