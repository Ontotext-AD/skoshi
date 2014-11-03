$(function() {

  var limit = 50;
  var offset = 0;
  var autoSuggestEnabled = false;
  var selected = false;
  var selectedCategory;

  $('#alternativelabels').tagsinput();

  key('a', function(){ 
    var overlay = document.querySelector( '.md-overlay' );

    [].slice.call( document.querySelectorAll( '.md-trigger' ) ).forEach( function( el, i ) {

      var modal = document.querySelector( '#' + el.getAttribute( 'data-modal' ) ),
        close = modal.querySelector( '.md-close' );

      function removeModal( hasPerspective ) {
        classie.remove( modal, 'md-show' );

        if( hasPerspective ) {
          classie.remove( document.documentElement, 'md-perspective' );
        }
      }

      function removeModalHandler() {
        removeModal( classie.has( el, 'md-setperspective' ) ); 
      }

      el.addEventListener( 'keydown', function( ev ) {
        if (ev.keyCode == 27) {
              removeModalHandler();
          }
      });

      key('esc', function(){
        removeModalHandler();
      });

        classie.add( modal, 'md-show' );
        $('#newFacetInput').focus();
        overlay.removeEventListener( 'click', removeModalHandler );
        overlay.addEventListener( 'click', removeModalHandler );

        if( classie.has( el, 'md-setperspective' ) ) {
          setTimeout( function() {
            classie.add( document.documentElement, 'md-perspective' );
          }, 25 );
        }
        $('#newFacetInput').focus();

      close.addEventListener( 'click', function( ev ) {
        removeModalHandler();
      });

    } );

  });

  (function() {
    if (id && id != null) {
      getConceptDetails(id);
    }
    if ($('#conceptsSearchBox').val().length > 0) {
      autoSuggestService();
    } else {
      getConcepts('', 50, 0);
    }
    $('#conceptsSearchBox').focusTextToEnd();
    $("#importForm, #importForm2, #importForm3").attr("action", service + "/concepts/import");
  }());

    if (id && id != null) {
      $.when($.get(service + "/concepts/" + id + "/stemming"))
      .then(function(value) {
          if (value) {
            $("#line-checkbox-1").prop("checked", true);
            $('#line-checkbox-1').iCheck('check');
          } else {
            $("#line-checkbox-1").prop("checked", false);
            $('#line-checkbox-1').iCheck('uncheck');
          }
          $('#line-checkbox-1').each(function(){
          var self = $(this),
            label = self.next(),
            label_text = label.text();

          label.remove();
          self.iCheck({
            checkboxClass: 'icheckbox_line',
            radioClass: 'iradio_line',
            insert: '<div class="icheck_line-icon"></div>' + label_text
          });
        });

        if ($('#line-checkbox-1').prop('checked')) {
          $('.icheckbox_line').css('background', '#27AE60');
        } else {
          $('.icheckbox_line').css('background', '#BDC3C7');
        }
      }, function(error) {
          alertify.error(error);
      });
    }

  /* EVENT HANDLERS */

  $('#line-checkbox-1').on('ifChecked', function(event){
    $('.icheckbox_line').css('background', '#27AE60');
    $.ajax({
      url: service + "/concepts/" + id + "/stemming/?v=true",
      type: "PUT"
    }).done(function(result) {
      alertify.success(result);
    }).fail(function(result) {
      alertify.error(result);
    });
  });

  $('#line-checkbox-1').on('ifUnchecked', function(event){
    $('.icheckbox_line').css('background', '#BDC3C7');
    $.ajax({
      url: service + "/concepts/" + id + "/stemming/?v=false",
      type: "PUT"
    }).done(function(result) {
      alertify.success(result);
    }).fail(function(result) {
      alertify.error(result);
    });
  });

  $('#conceptsContainer').bind('scroll', function(){
     if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight){
        offset = offset + 50;
        var val = $('#conceptsSearchBox').val();
        getConcepts(val, limit, offset);
     }
  });

  $('#conceptsSearchBox').keyup(function() {
    autoSuggestService();
  });

  $('#saveNewConcept').on('click', function() {
    $.ajax({
      url: service + "/concepts/?lbl=" + $('#newConceptInput').val(),
      type: "POST"
    }).done(function(result) {
      location.href = 'index.html?id=' + result;
    }).fail(function(result) {
      alertify.error('Error');
    });
  });

  $('#importButton').on('keypress', function() {
    $('#import').modal({
      keyboard: true
    });
  });

  $('#exportButton').on('click', function() {
    window.open(service + "/concepts/export");
  });

  $('#importButtonInside').on('click', function() {
    var options = {
      beforeSend: function() {
        spinInit($('#importButtonInside').prev().prev()[0], '333');
      },
      dataType: 'json',
      complete: function(xhr) {
        location.href = 'index.html';
      },
      error: function(xhr) {
        alertify.error(xhr);
      }
    };
    $("#importForm").ajaxForm(options);
  });

  $('#keyphrasesImportButtonInside').on('click', function() {
    var options = {
      beforeSend: function() {
        spinInit($('#keyphrasesImportButtonInside').prev().prev()[0], '333');
      },
      dataType: 'json',
      complete: function(xhr) {
        location.href = 'index.html';
      },
      error: function(xhr) {
        alertify.error(xhr);
      }
    };
    $("#importForm2").ajaxForm(options);
  });

  $('#multitestImportButton').on('click', function() {
    var options = {
      beforeSend: function() {
        spinInit($('#multitestImportButton').prev().prev()[0], '333');
      },
      dataType: 'json',
      complete: function(xhr) {
        location.href = 'index.html';
      },
      success: function(result) {
        alertify.success(xhr.responseText);
      },
      error: function(xhr) {
        alertify.error(xhr.responseText);
      }
    };
    $("#importForm3").ajaxForm(options);
  });

  $(document).on('click', '.tt', function() {
    var url = encodeURIComponent($(this).attr('data-id'));
    location.href = 'index.html?id=' + url;
  });

  $(document).ajaxComplete(function() {
    $('#conceptsContainer').scrollTo('.active');
  })

  $(document).on('click', '#synonyms, #related, #narrower, #broader', function() {
    var itemID = $(this).parent().parent().attr('data-ttipid');
    var type = $(this).attr('id');
    if (id && id != null) {
      addRemoveRSNB("POST", type, id, itemID);
    }
  });

  $(document).on('click', '#deleteConcept', function() {
    if (id && id != null) {
      $.ajax({
        url: service + "/concepts/" + id,
        type: "DELETE"
      }).done(function(result) {
        location.href = 'index.html';
      }).fail(function(result) {
        alertify.error('Error');
      });
    }
  });

  $(document).on('click', '#deleteConceptFromList', function() {
    var itemID = $(this).parent().parent().attr('data-ttipid');
    $.ajax({
      url: service + "/concepts/" + itemID,
      type: "DELETE"
    }).done(function(result) {
      if (id && id != null && id == itemID) {
        location.href = 'index.html';
      } else {
        alertify.success(result);
      }
      getConcepts('', 50, 0);
    }).fail(function(result) {
      alertify.error('Error');
    });
  });

  $(document).on('click', '.deleteRSBA', function() {
    var itemID = $(this).attr('data-id');
    var type = $(this).attr('data-type');
    if (id && id != null) {
      addRemoveRSNB("DELETE", type, id, itemID);
    }
  });

  var timer;
  $('#mainLabel, #definition, #note').on('keyup', function() {
    var el = $(this);
    clearTimeout(timer);
    var ms = 1000;
    timer = setTimeout(function() {
      if (id && id != null) {
        changeNotePrefDef(el.attr('id'), el.val());
      }
    }, ms);
  });

});