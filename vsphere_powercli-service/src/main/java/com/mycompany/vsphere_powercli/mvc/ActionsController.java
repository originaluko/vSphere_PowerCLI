/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
package com.mycompany.vsphere_powercli.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.vsphere_powercli.services.SampleActionService;

import com.vmware.vise.data.query.ObjectReferenceService;


/**
 * A controller to serve HTTP JSON GET/POST requests to the endpoint "/actions".
 */
@Controller
@RequestMapping(value = "/actions")
public class ActionsController {
   private final static Log _logger = LogFactory.getLog(ActionsController.class);

   // UI plugin resource bundle for localized messages
   private final String RESOURCE_BUNDLE = "__bundleName__";

   private final SampleActionService _actionService;
   private final ObjectReferenceService _objectReferenceService;

   @Autowired
   public ActionsController(
         SampleActionService actionService,
         @Qualifier("objectReferenceService") ObjectReferenceService objectReferenceService) {
      _actionService = actionService;
      _objectReferenceService = objectReferenceService;
      QueryUtil.setObjectReferenceService(objectReferenceService);
   }

   // Empty controller to avoid warnings in vsphere_powercli-ui's bundle-context.xml
   // where the bean is declared
   public ActionsController() {
      _actionService = null;
      _objectReferenceService = null;
   }


   /**
    * Generic method to invoke an action on a given object or a global action.
    *
    * @param actionUid  the action Uid as defined in plugin.xml
    *
    * @param targets  null for a global action, comma-separated list of object ids
    *    for an action on 1 or more objects
    *
    * @param json additional data in JSON format, or null for the delete action.
    *
    * @return
    *    Returns a map with key values.
    */
   @RequestMapping(method = RequestMethod.POST)
   @ResponseBody
   public Map<String, Object> invoke(
            @RequestParam(value = "actionUid", required = true) String actionUid,
            @RequestParam(value = "targets", required = false) String targets,
            @RequestParam(value = "json", required = false) String json)
            throws Exception {
      // Parameters validation
      Object objectRef = null;
      if (targets != null) {
         String[] objectIds = targets.split(",");
         if (objectIds.length > 1) {
            // Our actions only support 1 target object for now
            _logger.warn("Action " + actionUid + " called with " + objectIds.length
                  + " target objects, will use only the first one");
         }
         String objectId = ObjectIdUtil.decodeParameter(objectIds[0]);
         objectRef = _objectReferenceService.getReference(objectId);
         if (objectRef == null) {
            String errorMsg = "Error in action " + actionUid +
                  ", object not found with id: " + objectId;
            _logger.error(errorMsg);
            throw new Exception(errorMsg);
         }
      }

      ActionResult actionResult = new ActionResult(actionUid, RESOURCE_BUNDLE);

      if (actionUid.equals("com.mycompany.vsphere_powercli.sampleAction1")) {
          _actionService.sampleAction1(objectRef);
          // Display a test error message
          actionResult.setErrorLocalizedMessage("Testing error message for action1");

      } else if (actionUid.equals("com.mycompany.vsphere_powercli.sampleAction2")) {
          boolean result = _actionService.sampleAction2(objectRef);
          actionResult.setResult(result, null);

      } else {
         String warning = "Action not implemented yet! "+ actionUid;
         _logger.warn(warning);
         actionResult.setErrorLocalizedMessage(warning);
      }
      return actionResult.getJsonMap();
   }

   /**
    * Generic handling of internal exceptions.
    * Sends a 500 server error response along with a json body with messages
    *
    * @param ex The exception that was thrown.
    * @param response
    * @return a map containing the exception message, the cause, and a stackTrace
    */
   @ExceptionHandler(Exception.class)
   @ResponseBody
   public Map<String, String> handleException(Exception ex, HttpServletResponse response) {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

      Map<String,String> errorMap = new HashMap<String,String>();
      errorMap.put("message", ex.getMessage());
      if(ex.getCause() != null) {
         errorMap.put("cause", ex.getCause().getMessage());
      }
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      errorMap.put("stackTrace", sw.toString());

      return errorMap;
   }
}

