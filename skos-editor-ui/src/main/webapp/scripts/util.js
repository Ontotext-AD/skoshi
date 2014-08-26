var service = 'http://localhost:8080/skos';

var xhr, el, conceptDataCached, id = getUrlParameter('id');

var tooltip = function() {

	$('.tt').qtip({
		content: {
			text: function(event, api) {
				if (id && id != null) {
					return '<ul class="nav nav-pills nav-stacked" data-ttipid="' + $(this).attr('data-id') + '"><li><a href="javascript:void(0)" id="synonyms">Add to synonyms</a></li><li><a href="javascript:void(0)" id="related">Add to related</a></li><li><a href="javascript:void(0)" id="broader">Add to broader</a></li><li><a href="javascript:void(0)" id="narrower">Add to narrower</a></li><li><a href="javascript:void(0)" id="deleteConceptFromList">Delete</a></li></ul>';
				} else {
					return '<ul class="nav nav-pills nav-stacked" data-ttipid="' + $(this).attr('data-id') + '"><li><a href="javascript:void(0)" id="deleteConceptFromList">Delete</a></li></ul>';
				}
			}
		},
		position: {
			my: 'left center',
			at: 'right center'
		},
		hide: {
			fixed: true,
			delay: 150
		},
		show: {
			solo: true
		}
	});

	$('.active').qtip({
		content: {
			text: function(event, api) {
				return '<ul class="nav nav-pills nav-stacked" data-ttipid="' + $(this).attr('data-id') + '"><li><a href="javascript:void(0)" id="deleteConceptFromList">Delete</a></li></ul>';
			}
		},
		position: {
			my: 'left center',
			at: 'right center'
		},
		hide: {
			fixed: true,
			delay: 150
		},
		show: {
			solo: true
		}
	});

}


/* CORE FUNCTIONS */

function autoSuggestService() {
	setTimeout(function() {
	    console.log(conceptDataCached);
	  }, 500);
	
	if (xhr) {
      xhr.abort();
    }
    var textValue = $('#conceptsSearchBox').val();
    var url;
    if (textValue.length > 0) {
      url = service + "/concepts?prefix=" + textValue;
    } else {
      url = service + "/concepts";
    }
    $('#conceptsContainer').html('');
    xhr = $.ajax({
      url: url,
    }).done(function(result) {
      $.each(result, function(i, l) {
        var dataID = l.id;
        if (dataID.endsWith('=')) {
          dataID = dataID.slice(0, -1);
        }
        if (id && id != null && id == dataID) {
          $('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item active" data-id="' + l.id + '">' + l.prefLabel + ' <span class="glyphicon glyphicon-asterisk"></span></a>');
        } else {
          $('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item tt" data-id="' + l.id + '">' + l.prefLabel + '</a>');
        }
        if (textValue.length > 1) {
          $('#conceptsContainer').highlight(textValue);
        }
      });
      if (id && id != null) {
        tooltip();
      }
    });
}

function changeNotePrefDef(type, el) {
	el = el.replace(/(?:\r\n|\r|\n)/g, '%0A');
	$.ajax({
		url: service + "/concepts/" + id + "/" + type + "?value=" + el,
		type: "PUT"
	}).done(function(result) {
		alertify.success(result);
	}).fail(function(result) {
		alertify.error(result);
	});
}

function addRemoveRSNB(http, type, conceptID, itemID) {
	$.ajax({
		url: service + "/concepts/" + conceptID + "/" + type + "/" + itemID,
		type: http
	}).done(function(result) {
		alertify.success(result);
		getConceptDetails(conceptID);
	}).fail(function(result) {
		alertify.error(result);
	});
}

function getConcepts() {
	$('#conceptsContainer').html('');
	$.ajax({
		url: service + "/concepts",
		type: "GET"
	}).done(function(result) {
		$.each(result, function(i, l) {
			var dataID = encodeURIComponent(l.id);
			if (id && id != null && id == dataID) {
				$('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item active" data-id="' + l.id + '">' + l.prefLabel + '</a>');
			} else {
				$('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item tt" data-id="' + l.id + '">' + l.prefLabel + '</a>');
			}
		});
		if (result.length <= 0) {
			$('#conceptsContainer').html('No concepts.');
		}
		tooltip();
	});
}

function getConceptDetails(id) {
	var promise = $.ajax({
		url: service + "/concepts/" + id,
		type: "GET"
	}).done(function(result) {
		var pl = '';
		$.each(result, function(i, l) {
			if (i == 'prefLabel') {
				pl = l;
				$('#preflabel').val(l);
				$('#deleteConcept').css('display', 'inline-block');
				$('#activeUser').html(l);
				$('#deleteConcept').html('Delete ' + l);
			}
			if (i == 'altLabels' || i == 'abbreviations') {
				$.each(l, function(index, value) {
					$('#' + i.toLowerCase()).tagsinput('add', value);
				});
			}
			if (i == 'definition' || i == 'note') {
				$('#' + i).val(l);
			}
			if (i == 'related' || i == 'synonyms' || i == 'broader' || i == 'narrower') {
				$('#' + i + '-list').html('');
				$.each(l, function(synIndex, synValue) {
					$('#' + i + '-list').append('<a href="javascript:void(0)" class="list-group-item RSNB" id="' + synValue.id + '">' + synValue.prefLabel + '<span style="float: right" data-type="' + i + '" data-id="' + synValue.id + '" class="glyphicon glyphicon-remove deleteRSBA"></span></a>');
				});
				if (l.length <= 0) {
					$('#' + i + '-list').append('N/A for ' + pl);
				}
				$('.RSNB').qtip({
					content: {
						text: function(event, api) {
							var detailInfo = '';
							$.ajax({
								url: service + "/concepts/" + $(this).attr('id'),
								type: "GET",
								async: false
							}).done(function(result) {

								$.each(result, function(i, l) {
									if (i == 'prefLabel') {
										detailInfo += '<div><b>Main label:</b> ' + l + '</div>';
									}
									if (i == 'altLabels' && l != null && l.length > 0) {
										detailInfo += '<div><b>Alternative labels:</b> ' + l + '</div>';
									}
									if (i == 'abbreviations' && l != null && l.length > 0) {
										detailInfo += '<div><b>Abbreviations:</b> ' + l + '</div>';
									}
									if (i == 'definition' && l != null && l.length > 0) {
										detailInfo += '<div><b>Definition:</b> ' + l + '</div>';
									}
									if (i == 'note' && l != null && l.length > 0) {
										detailInfo += '<div><b>Note:</b> ' + l + '</div>';
									}
									if (i == 'related' && l != null && l.length > 0) {
										detailInfo += '<div><b>Related:</b> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.prefLabel;
											} else {
												detailInfo += synValue.prefLabel + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'synonyms' && l != null && l.length > 0) {
										detailInfo += '<div><b>Synonyms:</b> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.prefLabel;
											} else {
												detailInfo += synValue.prefLabel + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'broader' && l != null && l.length > 0) {
										detailInfo += '<div><b>Broader:</b> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.prefLabel;
											} else {
												detailInfo += synValue.prefLabel + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'narrower' && l != null && l.length > 0) {
										detailInfo += '<div><b>Narrower:</b> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.prefLabel;
											} else {
												detailInfo += synValue.prefLabel + ', ';
											}
										});
										detailInfo += '</div>';
									}
								});
							});
							return detailInfo;
						}
					},
					position: {
						my: 'left center',
						at: 'right center',
					},
					show: {
						solo: true
					}
				});
			}
		});
	});
	$.when(promise).done(function(xhr) {
		conceptDataCached = xhr;
	});
}


/* HELPER FUNCTIONS */

(function($){
    $.fn.focusTextToEnd = function(){
        this.focus();
        var $thisVal = this.val();
        this.val('').val($thisVal);
        return this;
    }
}(jQuery));

String.prototype.endsWith = function(suffix) {
	return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function getUrlParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
}