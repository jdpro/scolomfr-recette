/*!
 * Start Bootstrap - SB Admin 2 v3.3.7+1 (http://startbootstrap.com/template-overviews/sb-admin-2)
 * Copyright 2013-2017 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */
var $execButton;
$(function() {
	$('#side-menu').metisMenu({
		toggle : true
	});

	$('table').on('draw.dt', function() {
		$(this).removeClass("hidden");

	});
	if (!Cookies.get('locale')) {
		Cookies.set('locale', "fr_FR", {
			path : '/'
		});
	}
	$('.dropdown-menu.dropdown-language li').on("click", function(e) {
		e.preventDefault();
		var newLocale = $(this).data("lang");
		Cookies.set('locale', newLocale, {
			path : '/'
		});
		location.reload();
	});
	$('table').DataTable({
		"pageLength" : 50,
		"columnDefs" : [ {
			"width" : "10%",
			"targets" : 0
		}, {
			"width" : "10%",
			"targets" : 1
		}, {
			"width" : "80%",
			"targets" : 2
		} ]
	});
	$('#side-menu').removeClass("hidden");
	$execButton = $("#testcase-exec-form").find("button");
	enableExecutionButton($execButton, true);
	$("#testcase-exec-form").on('submit', function(e) {

		e.preventDefault();
		enableExecutionButton($execButton, false);
		$.ajax({
			url : $(this).attr('action'),
			type : $(this).attr('method'),
			data : $(this).serialize(),
			dataType : "json",
			success : function(json) {

				displayResultArea(true);
				if ($infoMessageTemplate) {
					$("#errors-area").empty();
					$("#infos-area").empty();
				}
				if (json.uri) {
					executionTrackingUri = json.uri;
					handleAsyncResponse()
				} else {
					refreshMessages(json);
					enableExecutionButton($execButton, true);
				}
			}
		});

	});

});
var executionTrackingUri;
function handleAsyncResponse() {
	$.ajax({
		url : executionTrackingUri,
		dataType : "json",
		success : function(json) {
			refreshMessages(json);
			if (json.state != 'FINAL') {
				setTimeout(handleAsyncResponse, 500);
			} else {
				enableExecutionButton($execButton, true);
				if (extractErrorCount(json) == 0) {
					displayTestsModal($("#tests-modal-no-error-title").val(),
							$("#tests-modal-no-error-content").val());
				}

			}
		}
	});

}
var $infoMessageTemplate;
var $errorMessageTemplate;
function displayResultArea(bool) {
	bool ? $("section#result-area").removeClass("hidden") : $(
			"section#result-area").addClass("hidden");
}
function extractErrorCount(json) {
	var errorCount = (json.errorCount ? json.errorCount : 0);
	return errorCount;
}
function extractComplianceIndicator(json) {
	var complianceIndicator = (json.complianceIndicator ? json.complianceIndicator
			: -1);
	return complianceIndicator;
}
function refreshMessages(json) {
	if (!$infoMessageTemplate) {
		$infoMessageTemplate = $("#info-message-template").remove()
				.removeClass("hidden");
		$errorMessageTemplate = $("#error-message-template").remove()
				.removeClass("hidden");
	}

	displayErrorCount(extractErrorCount(json));
	var indicator = extractComplianceIndicator(json);

	displayComplianceIndicator(indicator == -1 ? "hide" : Math
			.round(indicator * 10000) / 100);
	var errors = new Array();
	var infos = new Array();
	$(json.messages).each(function(i, e) {
		if (e.type == 'INFO') {
			infos.push(e);
		} else {
			errors.push(e);
		}
	})
	displayMessages("errors-area", errors, $errorMessageTemplate)
	displayMessages("infos-area", infos, $infoMessageTemplate)
}
var $errorCountIndicator;
var previousErrorCount;
function displayErrorCount(errorCount) {
	if (!$errorCountIndicator) {
		$errorCountIndicator = $("#error-count")
	}
	$errorCountIndicator.text(errorCount);
	if (previousErrorCount != errorCount) {
		$errorCountIndicator.removeClass().addClass("label").addClass(
				errorCount == 0 ? "label-success" : "label-danger");
	}
	previousErrorCount = errorCount;
}
var $complianceIndicatorContainer, $complianceIndicator;
function displayComplianceIndicator(complianceIndicator) {
	if (!$complianceIndicatorContainer) {
		$complianceIndicatorContainer = $("#compliance-indicator-container");
		$complianceIndicator = $("#compliance-indicator");
	}
	if (complianceIndicator == "hide") {
		$complianceIndicatorContainer.addClass("hidden");
	} else {
		$complianceIndicatorContainer.removeClass("hidden");
		$complianceIndicator.text(complianceIndicator);
	}
}
function displayMessages(areaId, messages, $template) {
	$area = $("#" + areaId);
	var messageData, key, title, content, $messageBody;
	for ( var index in messages) {
		messageData = messages[index];
		if (!messageData.key) {
			continue;
		}
		key = messageData.key.replace(
				/([\!"#$%&'()*+,./:;<=>?@\[\\\]\^`\{|\}~])/g, "\\$1");

		title = messageData.title ? messageData.title : "<empty>";
		content = messageData.content ? messageData.content : messageData;
		$messageBody = $template.clone();
		$messageBody.prop("id", key);
		$messageBody.find("strong.title").text(title);
		$messageBody.find("span.content").html(content);
		$area.append($messageBody);
	}
}
var $testsModal;
function displayTestsModal(title, content) {
	if (!$testsModal) {
		$testsModal = $("#tests-modal");
	}
	$testsModal.find("h4").text(title);
	$testsModal.find(".modal-body").text(content);
	$testsModal.modal();
}

function enableExecutionButton($button, bool) {
	if (!bool) {
		$button.prop("disabled", "disabled").find("img").removeClass("hidden");
	} else {
		$button.removeAttr("disabled").find("img").addClass("hidden");
	}

}

// Loads the correct sidebar on window load,
// collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function() {
	$(window)
			.bind(
					"load resize",
					function() {
						var topOffset = 50;
						var width = (this.window.innerWidth > 0) ? this.window.innerWidth
								: this.screen.width;
						if (width < 768) {
							$('div.navbar-collapse').addClass('collapse');
							topOffset = 100; // 2-row-menu
						} else {
							$('div.navbar-collapse').removeClass('collapse');
						}

						var height = ((this.window.innerHeight > 0) ? this.window.innerHeight
								: this.screen.height) - 1;
						height = height - topOffset;
						if (height < 1)
							height = 1;
						if (height > topOffset) {
							$("#page-wrapper").css("min-height",
									(height) + "px");
						}
					});

	var url = window.location;
	// var element = $('ul.nav a').filter(function() {
	// return this.href == url;
	// }).addClass('active').parent().parent().addClass('in').parent();
	var element = $('ul.nav a').filter(function() {
		return this.href == url;
	}).addClass('active').parent();

	while (true) {
		if (element.is('li')) {
			element = element.parent().addClass('in').parent();
		} else {
			break;
		}
	}
});
