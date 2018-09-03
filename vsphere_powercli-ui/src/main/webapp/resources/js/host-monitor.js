/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
// Use JQuery's $(document).ready to execute the script when the document is loaded.
// All variables and functions are also hidden from the global scope.

$(document).ready(function() {

   // Get current object and return if document is loaded before context is set
   var objectById = window.frameElement.htmlClientSdk.app.getContextObjects()[0];
   if (!objectById) {
      return;
   }

   // REST url to retrieve a list of properties
   var dataUrl = "/ui/vsphere_powercli/rest/data/properties/" + objectById.id + "?properties=name,overallStatus,hardware.systemInfo.model";

   // Refresh the view and register same function as GlobalRefresh handler so that
   // the view is also refreshed when the user hits the toolbar's Refresh button
   refreshData();
   window.frameElement.htmlClientSdk.event.onGlobalRefresh(refreshData);

   function refreshData() {
      // JQuery call to the DataAccessController in the Java plugin
      $.getJSON(dataUrl, function (data) {
         // data object contains the properties listed above
         var color = data.overallStatus;
         $("h3").text("Host Info for " + data.name);
         $("#hostInfo").html(
            "<p><b>Status: </b><span style='color:" + color + "'>" + data.overallStatus +
            "</span></p><p><b>Model: </b>" + data['hardware.systemInfo.model'] + "</p>");
      })
      .fail(function (jqXHR, status, error) {
         var response = jqXHR.responseJSON;
         alert("Error: " + response.message +
            "\nCause: " + response.cause);
      });
   }
});
