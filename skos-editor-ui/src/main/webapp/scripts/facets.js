$(function() {

  var limit = 50;
  var offset = 0;
  var autoSuggestEnabled = false;
  var selected = false;
  var selectedCategory;

  var facetsAutosuggestRenderer = function (url, textValue) {
  
  $('#conceptsContainer').html('');
    xhr = $.ajax({
      url: url,
    }).done(function(result) {
      $.each(result, function(i, l) {
        var dataID = l.id;
        if (dataID.endsWith('=')) {
          dataID = dataID.slice(0, -1);
        }
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

  $('#concepts-loader').html('Loading...');

  var txt = '';
  var url = '';
  if (!selected) {
    $('#conceptsContainer').append('<div class="badge" style="margin: 20px auto !important;">Please select a facet to be able to add concepts.</div>');
    $('#concepts-loader').html('');
  } else {
    url = service + "/facets/" + $(selectedCategory).attr('data-id') + "/available";
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
        $("#right-content").append('<div class="form-group category"><div class="panel panel-default"><div class="panel-heading"><a class="panel-title categoryName" id=' + l.id + '>' + l.label + '</a><a href="javascript:void(0)" class="removeFacet" data-label="' + l.label + '" title="Remove facet" data-id="' + l.id + '"><span class="glyphicon glyphicon-remove" style="color: #000"></span></a></div><div class="panel-body category-content tokenfield" data-label="' + l.label + '" data-id="' + l.id + '"></div></div></div>');
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
                $('.category-content[data-id="' + facetid + '"]').append('<div class="token"><span class="token-label">' + y.label + '</span><a href="#" class="close" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                if (y.hasChildren) {
                  $.each(y.subTrees, function(x, y) {
                    $('.category-content[data-id="' + facetid + '"]').append('<div class="token level2"><span class="token-label">' + y.label + '</span><a href="#" class="close" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
                    if (y.hasChildren) {
                      $.each(y.subTrees, function(x, y) {
                        $('.category-content[data-id="' + facetid + '"]').append('<div class="token level3"><span class="token-label">' + y.label + '</span><a href="#" class="close" data-cid="' + y.id + '" data-fid="' + facetid + '" tabindex="-1">×</a></div><br />');
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
    facetsAutosuggestService(selectedCategory);
  });

  $(document).on('click', '.removeFacet', function() {
    $.ajax({
      url: service + "/facets/" + $(this).attr('data-id'),
      type: "DELETE"
    }).done(function(result) {
      getFacets();
    }).fail(function(result) {
      alertify.error(result);
    });
  });

  $(document).on('click', '.facet', function() {
    var cLabel = $(this).text();
    if (typeof selected != 'undefined' && selected) {
      var fId = $(selectedCategory).attr('data-id');
      var cId = $(this).attr('data-id');
      if ($(selectedCategory).find("[data-cid='" + cId + "']").size() == 0) {
        $.ajax({
          url: service + "/facets/" + fId + '/concepts/' + cId,
          type: "POST"
        }).done(function(result) {
          alertify.success(result);
          $(selectedCategory).append('<div class="token"><span class="token-label">' + cLabel + '</span><a href="#" class="close" data-cid="' + cId + '" data-fid="' + fId + '" tabindex="-1">×</a></div>');
        }).fail(function(result) {
          alertify.error(result);
        });
      } else {
        alertify.error('The concept was already added in this category.');
      }
      
    } else {
      alertify.error('Please select a facet.');
    }
  });

  $(document).on('click', '#newCategoryButton', function() {
    $("#right-content").append('<div class="form-group category"><div class="panel panel-default"><div class="panel-heading"><a class="panel-title categoryName">New category</a></div><div class="panel-body category-content"></div></div></div>');
    $('.categoryName').editable({
        type: 'text',
        title: 'Enter facet name',
    });
  });

  $(document).on('click', '.close', function() {
    var parent = $(this).parent();
    $.ajax({
      url: service + "/facets/" + $(this).attr('data-fid') + '/concepts/' + $(this).attr('data-cid'),
      type: "DELETE"
    }).done(function(result) {
      alertify.success(result);
      $(parent).remove();
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
    $(this).attr('data-selected', 'true');
    $('.panel-heading').css('background-color', '#f5f5f5').css('border-color', '#ddd').css('color', '#333');
    $(this).children().find('.panel-heading').css('background-color', '#3498DB').css('border-color', '#2980B9').css('color', '#fff');
  });

});