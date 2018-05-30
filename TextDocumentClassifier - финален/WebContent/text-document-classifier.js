var categoriesCounter = 2;

$(document).ready(function () {
	$('button').hover(buttonHover, buttonUnhover);
	$('input[type="button"]').hover(buttonHover, buttonUnhover);
	
	addItemEventHandlers($('#categoryList li, #docList li'));
	
	$('input, button, textarea').focus(function() {
		$(this).addClass('focusedInput');
	});
	
	$("input, button, textarea").focusout(function() {
		$(this).removeClass('focusedInput');
	});
	
	$('#addCategoryButton').click(function() {
		var $categories = $('li.category input[type="text"]')
		var $newCategory = $('<li></li>').attr('class', 'category');
		var categoryElements = ' \
		<h4>Category:</h4> \
		<input id="cat' + (categoriesCounter + 1) + '" type="text" list="categorySuggestions"> \
		<h4>Keywords:</h4> \
		<textarea id="cat' + (categoriesCounter + 1) + 'Keywords" cols="20" rows="3" ></textarea>';
		$newCategory.html(categoryElements);
		addItemEventHandlers($newCategory);
		$('#categoryList').append($newCategory);
		$('li.category input[type="text"]').keyup(function() {
			suggestCategories($(this));
		});
		$('li.category input[type="text"]').on('input', function(event) {
			suggestKeywords($(event.target));
		});
		
		var $removeCategoryButton = $('#removeCategoryButton');
		if ($removeCategoryButton.prop('disabled')) {
			enableButton($removeCategoryButton);
			enableButton($('#classifyButton'));
		}
	});
	
	$('#removeCategoryButton').click(function() {
		$selectedCategories = $('#categoryList li.selectedItem');
		if ($selectedCategories.length) {
			if (confirm('Are you sure you want to remove the selected categories?')) {
				$selectedCategories.remove();
				if (!$('#categoryList li').length) {
					disableButton($(this));
					disableButton($('#classifyButton'))
				}
			}
		}
		else {
			alert('Please select categories to remove first!');
		}
	});
	
	$('#docChooserButton').click(function() {
		$('#docChooser').click();
	});
	
	$('#docChooser').change(function() {
		var formData = new FormData();
		$.each($(this)[0].files, function(i, file) {
            formData.append('file' + i, file);
        });
        $.ajax({
            url: '/TextDocumentClassifier/docs',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false
        });
		
		for (var i = 0; i < $(this)[0].files.length; i++) {
			var $newFile = $('<li></li>');
			$newFile.html($(this)[0].files[i].name);
			addItemEventHandlers($newFile);
			$('#docList').append($newFile);
		}
		
		$removeDocButton = $('#removeDocButton');
		if ($removeDocButton.prop('disabled')) {
			enableButton($removeDocButton);
		}
	});
	
	$('#removeDocButton').click(function() {
		$selectedDocs = $('#docList li.selectedItem');
		if ($selectedDocs.length) {
			if (confirm('Are you sure you want to remove the selected documents?')) {
				var docsToRemove = {};
				$.each($selectedDocs, function(i, doc) {
	                docsToRemove['file' + i] = $(doc).html();
	            });
		        $.ajax({
		            url: '/TextDocumentClassifier/docs' + '?' + $.param(docsToRemove),
		            type: 'DELETE',
		            success: function(response) {
		            	$selectedDocs.remove();
		            	
		            	if (!$('#docList li').length) {
							disableButton($(this));
						}
		            }
		        });
			}
		}
		else {
			alert('Please select documents to remove first!');
		}
	});
	
	$('#classifyButton').click(function() {
		var fd = new FormData();
		var categoryData = [];
		$categories = $('#categoryList input[type="text"]');
		for (var i = 0; i < $categories.length; i++) {
			categoryData.push($($categories[i]).val());
			fd.append('categories[]', $($categories[i]).val());
		}
		var keywordData = [];
		$keywords = $('#categoryList textarea');
		for (var i = 0; i < $keywords.length; i++) {
			keywordData.push($($keywords[i]).val());
			fd.append('keywords[]', $($keywords[i]).val());
		}
		params = {
            	categories: categoryData,
            	keywords: keywordData,
            	stopWords: $($('#stopWords')[0]).val(),
            	maxKeywordWeight: $($('#maxKeywordWeight')[0]).val()
            };
		fd.append('stopWords', $($('#stopWords')[0]).val());
		fd.append('maxKeywordWeight', $($('#maxKeywordWeight')[0]).val());
		  var req = new XMLHttpRequest();
		  req.open("POST", "/TextDocumentClassifier/classify", true);
		  //req.setRequestHeader('Content-type', 'application/zip');
		  req.responseType = "arraybuffer";

		  req.onload = function (event) {
		    var blob = new Blob([this.response], {type: req.getResponseHeader('Content-Type')});
		    console.log(blob.size);
		    /*
		    var link=document.createElement('a');
		    link.href=window.URL.createObjectURL(blob);
		    link.download="classified_docs.zip";
		    link.click();*/
		    var $downloadLink = $('#downloadLink');
	        var url = window.URL.createObjectURL(blob);
	        $downloadLink.attr('href', url);
	        $downloadLink.attr('download', 'classified_docs.zip');
	        //$downloadLink[0].click();
	        $downloadLink.removeClass('hidden');
	        //window.URL.revokeObjectURL(url);
		  };

		  req.send(fd);
		/*
		$.post('/TextDocumentClassifier/classify', params, function(response, status, xhr) {
			console.log(xhr.getResponseHeader('Content-Length'));
			xhr.responseType = "arraybuffer";
			//console.log(response);
			
			$.get('/TextDocumentClassifier/' + response, function(response) {
				console.log('SUccess!')
			});
			var type = xhr.getResponseHeader('Content-Type');
			// Create a new Blob object using the 
	        //response data of the onload object
	        var blob = new Blob([response], {type: type});
	        console.log(blob.size);
	        //Create a link element, hide it, direct 
	        //it towards the blob, and then 'click' it programatically
	        
	        var $downloadLink = $('#downloadLink');
	        //Create a DOMString representing the blob 
	        //and point the link element towards it
	        var url = window.URL.createObjectURL(blob);
	        $downloadLink.attr('href', url);
	        $downloadLink.attr('download', 'Koreni.zip');
	        //programatically click the link to trigger the download
	        $downloadLink[0].click();
	        //release the reference to the file by revoking the Object URL
	        window.URL.revokeObjectURL(url);
		})*/
//		$.post('/TextDocumentClassifier/help.zip');
//		$.ajax({
//            url: '/TextDocumentClassifier/classify',
//            type: 'POST',
//            data: params,
//            success: function(response) {
//            	var $downloadLink = $('#downloadLink');
//            	//$downloadLink.attr('href', response);
//            	$downloadLink.removeClass('hidden');
//            }
//        });
	});
	
	$('#helpIcon').hover(function() {
		$(this).addClass('pointer');
	}, function() {
		$(this).removeClass('pointer');
	});
	
	$('#helpIcon').click(function() {
		$('#helpInfo').removeClass('hidden');
	});
	
	$('#helpOKButton').click(function() {
		$('#helpInfo').addClass('hidden');
	});
	
	$('li.category input[type="text"]').keyup(function() {
		suggestCategories($(this));
	});
	
	$('li.category input[type="text"]').on('input', function() {
		suggestKeywords($(this));
	});
});

function enableButton($button) {
	$button.prop('disabled', false);
	$button.removeClass('disabled');
}

function disableButton($button) {
	$button.prop('disabled', true);
	$button.addClass('disabled');
	$button.removeClass('hoveredButton');
}

function buttonHover() {
	$(this).removeClass('button').addClass('hoveredButton');
}

function buttonUnhover() {
	$(this).removeClass('hoveredButton').addClass('button');
}

function itemHover() {
	$(this).removeClass('category');
	$(this).addClass('hoveredItem');
	$(this).children('h4').addClass('hoveredHeading');
}

function itemUnhover() {
	$(this).removeClass('category');
	$(this).removeClass('hoveredItem');
	$(this).children('h4').removeClass('hoveredHeading');
}

function itemClick(e) {
	if (!$(e.target).is('input, textarea')) {
		var $liElement = $(e.target).closest('li');
		var classNames = $liElement.attr('class').split(' ');
		if (classNames.indexOf('selectedItem') > -1) {
			$liElement.removeClass('selectedItem');
		}
		else {
			$liElement.addClass('selectedItem');
		}
	}
}

function addItemEventHandlers($element) {
	$element.mouseenter(itemHover);
	$element.mouseleave(itemUnhover);
	$element.click(e => itemClick(e));
}

function suggestCategories($element) {
	$.ajax({
        url: '/TextDocumentClassifier/categories',
        type: 'GET',
        data: {categoryInput: $element.val()},
        success: function(response) {
        	var categories = response.split('\n').filter(c => c.length);
        	$('#categorySuggestions').empty();
        	for (var i = 0; i < categories.length; i++) {
        		var $newCategorySuggestion = $('<option>').attr('value', categories[i]);
        		$('#categorySuggestions').append($newCategorySuggestion);
        	}
        }
    });
}

function suggestKeywords($element) {
    var inputValue = $element.val();
    if ($('#categorySuggestions').find('option').filter(function() {
        return $(this).val().toUpperCase() === inputValue.toUpperCase();
    }).length) {
    	$.ajax({
            url: '/TextDocumentClassifier/keywords',
            type: 'GET',
            data: {category: inputValue},
            success: function(response) {
            	var keywordsElementID = $element.attr('id') + 'Keywords';
            	$('#' + keywordsElementID).val(response);
            }
        });
    }
}