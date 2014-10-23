$(function() {

  /* retrieve concepts and the selected concept if any */

  var limit = 50;
  var offset = 0;
  var autoSuggestEnabled = false;
  var selected = false;
  var selectedCategory;

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
    $("#importForm").attr("action", service + "/concepts/import");
  }());

  $('input').each(function(){
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


  /* EVENT HANDLERS */

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
      url: service + "/concepts/" + $('#newConceptInput').val(),
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
        $("#messageinfo").html("<span class='icon-spin icon-spinner'></span>");
      },
      dataType: 'json'
    };
    $("#importForm").ajaxForm(options);
    $('#import').modal('hide');
    getConcepts('', 50, 0);
  });

  $(document).on('click', '.tt', function() {
    var url = $(this).attr('data-id');
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