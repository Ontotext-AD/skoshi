$(function() {

  var limit = 50;
  var offset = 0;
  var autoSuggestEnabled = false;
  var selected = false;
  var selectedCategory;

  var facetsAutosuggestRenderer = function (url, textValue) {
  
  $('#conceptsContainer').html('');
  $('#concepts-loader').html('Loading...');
    xhr = $.ajax({
      url: url,
      cache: true
    }).done(function(result) {
      $.each(result, function(i, l) {
        var dataID = l.id;
        if (dataID.endsWith('=')) {
          dataID = dataID.slice(0, -1);
        }
        $('#concepts-loader').html('');
        $('#conceptsContainer').append('<a href="javascript:void(0)" class="list-group-item facet" data-id="' + l.id + '">' + l.label + '</a>');
        if (textValue.length > 1) {
          $('#conceptsContainer').highlight(textValue);
        }
      });
    });
}
  var facetsAutosuggestService = function (id) {

  if (xhr) {
    xhr.abort();
  }
  
  var url;
  var textValue = $('#conceptsSearchBox').val();

  if (textValue.length >= 2) {
    url = service + "/facets/" + $(id).attr('data-id') + "/available?prefix=" + textValue + "&limit=50";
    facetsAutosuggestRenderer(url, textValue);
  } else if (textValue.length == 0) {
    url = service + "/facets/" + $(id).attr('data-id') + "/available?limit=50";
    facetsAutosuggestRenderer(url, textValue);
  }
    
}

  var getConceptsAvailable = function (val, limit, offset) {
  if (limit == 0 && offset == 0) {
    $('#conceptsContainer').html(''); 
  }

  $('#concepts-loader').html('Loading next 50 concepts...');

  var txt = '';
  var url = '';
  if (!selected) {
    $('#conceptsSearchBox').hide();
    $('#conceptsContainer').append('Please select a facet to be able to add concepts.');
    $('#concepts-loader').html('');
  } else {
    $('#conceptsSearchBox').show();
    url = service + "/facets/" + $(selectedCategory).attr('data-id') + "/available?limit=" + limit + "&offset=" + offset;
    var xhr = $.ajax({
      url: url,
      type: "GET"
    }).done(function(result) {
      $.each(result, function(i, l) {
        var dataID = encodeURIComponent(l.id);
        if (window.location.href.indexOf("facets") > -1) {
          txt += '<a href="javascript:void(0)" title="Add to ' + $(selectedCategory).attr('data-label') + '" class="list-group-item facet" data-id="' + l.id + '">' + l.label + '</a>';
        }
        
      });
      $('#conceptsContainer').append(txt);
      if (window.location.href.indexOf("facets") == -1) {
        tooltip('c' + offset);
      }
      $('#concepts-loader').html('');
    });
  }
}

  var getFacets = function() {
    $("#right-content").html('');
    $.ajax({
      url: service + "/facets/",
      type: "GET"
    }).done(function(result) {
      $.each(result, function(i, l) {
        var label = l.label;
        if (label.length > 30) {
            label = label.substr(0, 27);
            label = label + '...';
        }
        $("#right-content").append('<div class="form-group category item"><div class="panel panel-default"><div class="panel-heading"><a class="panel-title categoryName" title="' + l.label + '" id=' + l.id + '>' + label + '</a><a href="javascript:void(0)" class="remove" data-label="' + l.label + '" title="Remove facet" data-id="' + l.id + '"><span class="glyphicon glyphicon-remove" style="color: #000"></span></a></div><div class="panel-body category-content tokenfield" data-label="' + l.label + '" data-id="' + l.id + '"></div></div></div>');
        $('.categoryName').editable({
            type: 'text',
            title: 'Enter facet name',
        });
        var facetid = l.id;
        $.ajax({
          url: service + "/facets/" + l.id,
          type: "GET"
        }).done(function(result) {
          $.each(result, function(i, l) {
            if (i == 'subTrees') {
              $.each(l, function(x, y) {
                $('.category-content[data-id="' + facetid + '"]').append('<div class="token"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                if (y.hasChildren) {
                  $.each(y.subTrees, function(x, y) {
                    $('.category-content[data-id="' + facetid + '"]').append('<div class="token level2"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                    if (y.hasChildren) {
                      $.each(y.subTrees, function(x, y) {
                        $('.category-content[data-id="' + facetid + '"]').append('<div class="token level3"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                      });
                    }
                  });
                }
              });
            }
          });
        }).fail(function(result) {
          alertify.error(result);
        });     
      });
    }).fail(function(result) {
      alertify.error(result);
    });
  }

  var getFacetsAfterAdd = function() {
    $("#right-content").html('');
    $.ajax({
      url: service + "/facets/",
      type: "GET"
    }).done(function(result) {
      $.each(result, function(i, l) {
        $("#right-content").append('<div class="form-group category item data-id="' + l.id + '""><div class="panel panel-default"><div class="panel-heading"><a class="panel-title categoryName" id=' + l.id + '>' + l.label + '</a><a href="javascript:void(0)" class="remove" data-label="' + l.label + '" title="Remove facet" data-id="' + l.id + '"><span class="glyphicon glyphicon-remove" style="color: #000"></span></a></div><div class="panel-body category-content tokenfield" data-label="' + l.label + '" data-id="' + l.id + '"></div></div></div>');
        $('.categoryName').editable({
            type: 'text',
            title: 'Enter facet name',
        });
        var facetid = l.id;
        $.ajax({
          url: service + "/facets/" + l.id,
          type: "GET"
        }).done(function(result) {
          $.each(result, function(i, l) {
            if (i == 'subTrees') {
              $.each(l, function(x, y) {
                $('.category-content[data-id="' + facetid + '"]').append('<div class="token"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                if (y.hasChildren) {
                  $.each(y.subTrees, function(x, y) {
                    $('.category-content[data-id="' + facetid + '"]').append('<div class="token level2"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                    if (y.hasChildren) {
                      $.each(y.subTrees, function(x, y) {
                        $('.category-content[data-id="' + facetid + '"]').append('<div class="token level3"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                      });
                    }
                  });
                }
              });
            }
          });
        }).fail(function(result) {
          alertify.error(result);
        });     
      });
      var len = $('.category').length;
      $('.category').each(function(i, obj) {
        thisVal = $(this).val();
        var $this = $(this);
        if (parseInt(thisVal) != 0) {
            if (i == len - 1) {
                $(this).trigger('click');
            }
        }
      });

    }).fail(function(result) {
      alertify.error(result);
    });
  }

  var getFacetsAfterRemove = function() {
    $("#right-content").html('');
    $.ajax({
      url: service + "/facets/",
      type: "GET"
    }).done(function(result) {
      $.each(result, function(i, l) {
        $("#right-content").append('<div class="form-group category item" data-id="' + l.id + '"><div class="panel panel-default"><div class="panel-heading"><a class="panel-title categoryName" id=' + l.id + '>' + l.label + '</a><a href="javascript:void(0)" class="remove" data-label="' + l.label + '" title="Remove facet" data-id="' + l.id + '"><span class="glyphicon glyphicon-remove" style="color: #000"></span></a></div><div class="panel-body category-content tokenfield" data-label="' + l.label + '" data-id="' + l.id + '"></div></div></div>');
        $('.categoryName').editable({
            type: 'text',
            title: 'Enter facet name',
        });
        var facetid = l.id;
        $.ajax({
          url: service + "/facets/" + l.id,
          type: "GET"
        }).done(function(result) {
          $.each(result, function(i, l) {
            if (i == 'subTrees') {
              $.each(l, function(x, y) {
                $('.category-content[data-id="' + facetid + '"]').append('<div class="token"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                if (y.hasChildren) {
                  $.each(y.subTrees, function(x, y) {
                    $('.category-content[data-id="' + facetid + '"]').append('<div class="token level2"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                    if (y.hasChildren) {
                      $.each(y.subTrees, function(x, y) {
                        $('.category-content[data-id="' + facetid + '"]').append('<div class="token level3"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                      });
                    }
                  });
                }
              });
            }
          });
        }).fail(function(result) {
          alertify.error(result);
        });     
      });

      var len = $('.category').length;
      var found = false;
      $('.category').each(function(i, obj) {

        thisVal = $(this).val();
        var $this = $(this);
        
        if (typeof selectedCategory != 'undefined' && selectedCategory != null) {
          if ($(this).attr('data-id') == $(selectedCategory).attr('data-id')) {
            $(this).trigger('click');
            found = true;
          }
        }
      });

      if (!found) {
        selectedCategory = null;
        selected = false;
        $('.category').attr('data-selected', null);
      }

      $('#conceptsContainer').html('');
      //getConceptsAvailable();

    }).fail(function(result) {
      alertify.error(result);
    });
  }

  var getFacet = function(id, container) {
    $(container).html('');
    $.ajax({
      url: service + "/facets/" + id,
      type: "GET"
    }).done(function(result) {
      $.each(result, function(i, l) {
            if (i == 'subTrees') {
              $.each(l, function(x, y) {
                $(container).append('<div class="token"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + id + '" tabindex="-1">×</a></div><br />');
                if (y.hasChildren) {
                  $.each(y.subTrees, function(x, y) {
                    $(container).append('<div class="token level2"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + id + '" tabindex="-1">×</a></div><br />');
                    if (y.hasChildren) {
                      $.each(y.subTrees, function(x, y) {
                        $(container).append('<div class="token level3"><span class="token-label">' + y.label + '</span><a href="#" class="removeFacet" data-cid="' + y.id + '" data-fid="' + id + '" tabindex="-1">×</a></div><br />');
                      });
                    }
                  });
                }
              });
            }
          });
    }).fail(function(result) {
      alertify.error(result);
    });
  }

  if ($('#conceptsSearchBox').val().length > 0) {
    facetsAutosuggestService(selectedCategory);
  } else {
    getConceptsAvailable('', 50, 0);
  }
  $('#conceptsSearchBox').focusTextToEnd();
  
  $('#conceptsContainer').bind('scroll', function(){
     if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight){
        offset = offset + 50;
        var val = $('#conceptsSearchBox').val();
        getConceptsAvailable(val, limit, offset);
     }
  });

  getFacets();

  $('#conceptsSearchBox').keyup(function() {
    $('#conceptsSearchBox').keyup(function(e) {
      var keycode = (e.keyCode ? e.keyCode : e.which);
      if (keycode != '17' && keycode != '18' && keycode != '91' && keycode != '93' && keycode != '9' && keycode != '27' && keycode != '37' && keycode != '38' && keycode != '39' && keycode != '40'){
        facetsAutosuggestService(selectedCategory);
      }
    });
  });

  $('#newFacetInput').keyup(function(e) {
      var keycode = (e.keyCode ? e.keyCode : e.which);
      if (keycode == '13'){
        $('#saveNewFacet').trigger('click');
      }
      if (keycode == '27') {
        var overlay = document.querySelector( '.md-overlay' );

        [].slice.call( document.querySelectorAll( '.md-trigger' ) ).forEach( function( el, i ) {

          var modal = document.querySelector( '#' + el.getAttribute( 'data-modal' ) ),
            close = modal.querySelector( '.md-close' );
            classie.remove( modal, 'md-show' );

        });
      }
  });

  $(document).on('click', '.remove', function(e) {
    e.stopPropagation();
    $.ajax({
      url: service + "/facets/" + $(this).attr('data-id'),
      type: "DELETE"
    }).done(function(result) {
      getFacetsAfterRemove();
    }).fail(function(result) {
      alertify.error(result);
    });
  });

  $(document).on('click', '.facet', function() {
    if (typeof selected != 'undefined' && selected) {
      var fLabel = $(selectedCategory).attr('data-label');
      var cLabel = $(this).text();
      var fId = $(selectedCategory).attr('data-id');
      var cId = $(this).attr('data-id');
        $.ajax({
          url: service + "/facets/" + fId + '/concepts/' + cId,
          type: "POST"
        }).done(function(result) {
          alertify.success('Concept "' + cLabel + '" added successfuly to the facet "' + fLabel + '"', 6000);

          $('#conceptsContainer').html('');
          getFacet($(selectedCategory).attr('data-id'), selectedCategory);

          getConceptsAvailable('', 50, 0);
        }).fail(function(result) {
          alertify.error(result);
        });
    } else {
      alertify.error('Please select a facet.');
    }
  });

  $(document).on('click', '#saveNewFacet', function() {
    if ($('#newFacetInput').val()) {
      $.ajax({
        url: service + "/facets/",
        type: "POST",
        data: 'lbl=' + $('#newFacetInput').val()
      }).done(function(result) {
        $('#newFacetInput').val('');
        alertify.success(result);
        getFacetsAfterAdd();
      }).fail(function(result) {
        alertify.error(result);
      });
    } else {
      alertify.error('Please type a facet name.');
      return false;
    }
    
  });

  $('#newFacetButton').on('mouseover', function() {
    $('.md-modal').css('visibility', 'visible');
  });

  $('#newFacetButton').on('mouseout', function() {
    $('.md-modal').css('visibility', 'hidden');
  });

  $(document).on('click', '.removeFacet', function() {
    var parent = $(this).parent();
    var container = $(this).parents('.category-content');
    var facetId = $(this).attr('data-fid');
    $.ajax({
      url: service + "/facets/" + $(this).attr('data-fid') + '/concepts/' + $(this).attr('data-cid'),
      type: "DELETE"
    }).done(function(result) {
      alertify.success(result);
      $('#conceptsContainer').html('');
      getFacet(facetId, container);
      getConceptsAvailable('', 50, 0);
    }).fail(function(result) {
      alertify.error(result);
    });
  });

  $(document).on('click', '.category', function() {
    selected = true;
    selectedCategory = $(this).children().find('.category-content');

    if (typeof $(selectedCategory).attr('data-id') != 'undefined' && typeof $(this).attr('data-selected') == 'undefined') {
      var val = $('#conceptsSearchBox').val();
      $('#conceptsContainer').html('');
      getConceptsAvailable(val, limit, offset);
    }
    $('.category').attr('data-selected', null);
    $('.category').removeClass('selected');
    $(this).attr('data-selected', 'true');
    $(this).addClass('selected');
    $('.panel-heading').css('background-color', '#f5f5f5').css('border-color', '#ddd').css('color', '#333');
    $(this).children().find('.panel-heading').css('background-color', '#3498DB').css('border-color', '#2980B9').css('color', '#fff');
  });

});