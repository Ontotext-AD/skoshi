$(window).scroll(function() {
    if ($(this).scrollTop() > 1){  
        $('header').addClass("sticky");
    }
    else{
        $('header').removeClass("sticky");
    }
});

var service, protocol, api, xhr, el, conceptDataCached;
var id = getUrlParameter('id');

function spinInit(el, color) {
    var opts = {
        lines: 11, // The number of lines to draw
        length: 5, // The length of each line
        width: 2, // The line thickness
        radius: 3, // The radius of the inner circle
        corners: 0.9, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#' + color, // #rgb or #rrggbb
        speed: 1.1, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: true, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: 'auto' // Left position relative to parent in px
    };
    target = el;
    var spinner = new Spinner(opts).spin(target);
}

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
					return '<ul class="nav nav-pills nav-stacked" data-ttipid="' + $(this).attr('data-id') + '"><li><a href="javascript:void(0)" id="synonyms">Add as synonym</a></li><li><a href="javascript:void(0)" id="related">Add as related</a></li><li><a href="javascript:void(0)" id="broader">Add as broader</a></li><li><a href="javascript:void(0)" id="narrower">Add as narrower</a></li><li><a href="javascript:void(0)" id="deleteConceptFromList">Delete</a></li></ul>';
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
					return '<ul class="nav nav-pills nav-stacked" data-ttipid="' + $(this).attr('data-id') + '"><li><a href="javascript:void(0)" id="synonyms">Add as synonym</a></li><li><a href="javascript:void(0)" id="related">Add as related</a></li><li><a href="javascript:void(0)" id="broader">Add as broader</a></li><li><a href="javascript:void(0)" id="narrower">Add as narrower</a></li><li><a href="javascript:void(0)" id="deleteConceptFromList">Delete</a></li></ul>';
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
	$('#concepts-loader').html('Loading...');
	var txt = '';
    xhr = $.ajax({
      url: url,
      cache: true
    }).done(function(result) {
    	$('#concepts-loader').html('');
      $.each(result, function(i, l) {
        var dataID = l.id;
        if (dataID.endsWith('=')) {
          dataID = dataID.slice(0, -1);
        }
        if (id && id != null && id == dataID) {
          txt += '<a href="javascript:void(0)" class="list-group-item active" data-id="' + l.id + '">' + l.label + ' <span class="glyphicon glyphicon-asterisk"></span></a>';
        } else {
          txt += '<a href="javascript:void(0)" class="list-group-item tt" data-id="' + l.id + '">' + l.label + '</a>';
        }
      });

      	document.getElementById('conceptsContainer').innerHTML = txt;
      	if (textValue.length > 1) {
	      $('#conceptsContainer').highlight(textValue);
	    }
      	
      	if (result.length > 0) {
      		tooltipAutoSuggest();
      	} else {
      		$('#conceptsContainer').append('<div style="font-size: 12px; margin-top: 5px;">No results found.</div>');
      	}     
    });
}
function autoSuggestService() {

	if (xhr) {
      xhr.abort();
    }
    
    var url;
    var textValue = $('#conceptsSearchBox').val();
    if (textValue.length >= 2) {
      url = service + "/concepts?prefix=" + textValue + "&limit=50";
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

function getConcepts(val, limit, offset) {
	if (limit == 0 && offset == 0) {
		$('#conceptsContainer').html('');	
	}

	$('#concepts-loader').html('Loading next 50 concepts...');

	var txt = '';
	var url = '';
	if (val.length > 1) {
		url = service + "/concepts?prefix=" + val + "&limit=" + limit + "&offset=" + offset;
	} else {
		url = service + "/concepts?limit=" + limit + "&offset=" + offset;
	}
	var xhr = $.ajax({
		url: url,
		type: "GET"
	}).done(function(result) {
		$.each(result, function(i, l) {
			var dataID = encodeURIComponent(l.id);

			if (window.location.href.indexOf("facets") > -1) {
		       txt += '<a href="javascript:void(0)" class="list-group-item facet" data-id="' + l.id + '">' + l.label + '</a>';
		    } else {
		    	if (id && id != null && id == dataID) {
					txt += '<a href="javascript:void(0)" class="list-group-item active c' + offset + '" data-id="' + l.id + '">' + l.label + '</a>';
				} else {
					txt += '<a href="javascript:void(0)" class="list-group-item tt c' + offset + '" data-id="' + l.id + '">' + l.label + '</a>';
				}
		    }
			
		});
		$('#conceptsContainer').append(txt);
		if (window.location.href.indexOf("facets") == -1) {
			tooltip('c' + offset);
		}
		if (val && val != null) {
			$('#conceptsContainer').highlight(val);
		}
		$('#concepts-loader').html('');
	});
	
}

function getConceptDetails(id) {
	var promise = $.ajax({
		url: service + "/concepts/" + id,
		type: "GET"
	}).done(function(result) {
		var pl = '';
		$.each(result, function(i, l) {
			if (i == 'label') {
				pl = l;
				$('#preflabel').val(l);
				$('#deleteConcept').css('display', 'inline-block');
				$('#activeUser').html(l);
				$('#stemming').css('display', 'inline-block');
			}
			if (i == 'alternativeLabels' || i == 'abbreviations') {
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
					$('#' + i + '-list').append('<a href="javascript:void(0)" class="list-group-item RSNB" id="' + synValue.id + '">' + synValue.label + '<span style="float: right" data-type="' + i + '" data-id="' + synValue.id + '" class="glyphicon glyphicon-remove deleteRSBA"></span></a>');
				});
				if (l.length <= 0) {
					$('#' + i + '-list').append('N/A');
				}
				$('.RSNB').qtip({
					content: {
						text: function(event, api) {
							var detailInfo = '';
							$.ajax({
								url: service + "/concepts/" + encodeURIComponent($(this).attr('id')),
								type: "GET",
								async: false
							}).done(function(result) {

								$.each(result, function(i, l) {
									if (i == 'label') {
										detailInfo += '<div><span style="color: #3498DB"><b>Main label:</b></span> ' + l + '</div>';
									}
									if (i == 'altLabels' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Alternative labels:</b></span> ' + l + '</div>';
									}
									if (i == 'abbreviations' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Abbreviations:</b></span> ' + l + '</div>';
									}
									if (i == 'definition' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Definition:</b></span> ' + l + '</div>';
									}
									if (i == 'note' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Note:</b></span> ' + l + '</div>';
									}
									if (i == 'related' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Related:</b></span> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.label;
											} else {
												detailInfo += synValue.label + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'synonyms' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Synonyms:</b></span> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.label;
											} else {
												detailInfo += synValue.label + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'broader' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Broader:</b></span> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.label;
											} else {
												detailInfo += synValue.label + ', ';
											}
										});
										detailInfo += '</div>';
									}
									if (i == 'narrower' && l != null && l.length > 0) {
										detailInfo += '<div><span style="color: #3498DB"><b>Narrower:</b></span> ';
										var len = $(l).length;
										$.each(l, function(synIndex, synValue) {
											if (synIndex == len - 1) {
												detailInfo += synValue.label;
											} else {
												detailInfo += synValue.label + ', ';
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