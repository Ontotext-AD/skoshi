
var service, protocol, api, xhr, el, conceptDataCached;
var id = getUrlParameter('id');

function parseUri (str) {
	var	o   = parseUri.options,
		m   = o.parser[o.strictMode ? "strict" : "loose"].exec(str),
		uri = {},
		i   = 14;

	while (i--) uri[o.key[i]] = m[i] || "";

	uri[o.q.name] = {};
	uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
		if ($1) uri[o.q.name][$1] = $2;
	});

	return uri;
};

parseUri.options = {
	strictMode: false,
	key: ["source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","anchor"],
	q:   {
		name:   "queryKey",
		parser: /(?:^|&)([^&=]*)=?([^&]*)/g
	},
	parser: {
		strict: /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))(?:\?([^#]*))?(?:#(.*))?)/,
		loose:  /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
	}
};

if (location.protocol === 'https:') {
    protocol = 'https';
} else {
	protocol = 'http';
}
var dir = parseUri(document.location).directory;
dir = dir.replace(/\//g,'');
if (dir.length > 0) {
	api = dir + '-api';
} else {
	api = 'ROOT-api'
}

if (location.port && location.port.length > 0) {
	service = protocol + '://' + document.location.hostname + ':' + location.port + '/' + api;
} else {
	service = protocol + '://' + document.location.hostname + '/' + api;
}

var tooltip = function(param) {

	$('.' + param).qtip({
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

var tooltipAutoSuggest = function() {

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

function autoSuggestRenderer(url, textValue) {
	
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
        tooltipAutoSuggest();
    });
}
function autoSuggestService() {

	if (xhr) {
      xhr.abort();
    }
    
    var url;
    var textValue = $('#conceptsSearchBox').val();
    if (textValue.length >= 2) {
      url = service + "/concepts?prefix=" + textValue;
      autoSuggestRenderer(url, textValue);
    } else if (textValue.length == 0) {
      url = service + "/concepts?limit=50";
      autoSuggestRenderer(url, textValue);
    }
    
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

function getConcepts(limit, offset) {
	if (limit == 0 && offset == 0) {
		$('#conceptsContainer').html('');	
	}
	var txt = '';
	var xhr = $.ajax({
		url: service + "/concepts?limit=" + limit + "&offset=" + offset,
		type: "GET"
	}).done(function(result) {
		$.each(result, function(i, l) {
			var dataID = encodeURIComponent(l.id);
			if (id && id != null && id == dataID) {
				txt += '<a href="javascript:void(0)" class="list-group-item active c' + offset + '" data-id="' + l.id + '">' + l.prefLabel + '</a>';
				//$('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item active" data-id="' + l.id + '">' + l.prefLabel + '</a>');
			} else {
				txt += '<a href="javascript:void(0)" class="list-group-item tt c' + offset + '" data-id="' + l.id + '">' + l.prefLabel + '</a>';
				//$('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item tt" data-id="' + l.id + '">' + l.prefLabel + '</a>');
			}
		});
		$('#conceptsContainer').append(txt);
		if (result.length <= 0) {
			$('#conceptsContainer').html('No concepts.');
		}
		tooltip('c' + offset);
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