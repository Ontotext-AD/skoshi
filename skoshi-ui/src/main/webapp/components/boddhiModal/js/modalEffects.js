$(function() {

var ModalEffects = (function() {

	function init() {

		var overlay = document.querySelector( '.md-overlay' );

		[].slice.call( document.querySelectorAll( '.md-trigger' ) ).forEach( function( el, i ) {

			var modal = document.querySelector( '#' + el.getAttribute( 'data-modal' ) ),
				close = modal.querySelector( '.md-close' );

			function removeModal( hasPerspective ) {
				if ($('#newConceptInput').val()) {
					$(modal).css('visibility', 'hidden');
					$(modal).hide();
					classie.remove( modal, 'md-show' );
				}
				if( hasPerspective ) {
					classie.remove( document.documentElement, 'md-perspective' );
				}
			}

			function removeModal2( hasPerspective ) {
				$(modal).css('visibility', 'hidden');
				$(modal).hide();
				classie.remove( modal, 'md-show' );
				if( hasPerspective ) {
					classie.remove( document.documentElement, 'md-perspective' );
				}
			}

			function removeModalHandler() {
				removeModal( classie.has( el, 'md-setperspective' ) ); 
			}

			function removeModalHandler2() {
				removeModal2( classie.has( el, 'md-setperspective' ) ); 
			}

			el.addEventListener( 'keydown', function( ev ) {
				if (ev.keyCode == 27) {
			        removeModalHandler();
			    }
			});

			el.addEventListener( 'click', function( ev ) {
				$(modal).css('visibility', 'visible');
				$(modal).show();
				classie.add( modal, 'md-show' );
				overlay.removeEventListener( 'click', removeModalHandler );
				overlay.addEventListener( 'click', removeModalHandler2 );

				if( classie.has( el, 'md-setperspective' ) ) {
					setTimeout( function() {
						classie.add( document.documentElement, 'md-perspective' );
					}, 25 );
				}
				$('#newConceptInput').focus();
				$('#newFacetInput').focus();
			});

			close.addEventListener( 'click', function( ev ) {
				
				removeModalHandler();
			});

		});

	}

	init();

})();

});