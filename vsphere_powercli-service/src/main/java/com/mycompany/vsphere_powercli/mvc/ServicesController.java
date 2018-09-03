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
import org.springframework.web.bind.annotation.*;

import com.mycompany.vsphere_powercli.services.EchoService;


/**
 * A controller to serve HTTP JSON GET/POST requests to the endpoint "/services".
 * Its purpose is simply to redirect HTTP requests to the service APIs implemented in
 * separate components.
 */
@Controller
@RequestMapping(value = "/services")
public class ServicesController {
   private final static Log _logger = LogFactory.getLog(ServicesController.class);

   private final EchoService _echoService;

   @Autowired
   public ServicesController(
         @Qualifier("echoService") EchoService echoService) {
      _echoService = echoService;
   }

   // Empty controller to avoid compiler warnings in vsphere_powercli-ui's bundle-context.xml
   // where the bean is declared
   public ServicesController() {
      _echoService = null;
   }


   /**
    * Echo a message back to the client.
    */
   @RequestMapping(value = "/echo", method = RequestMethod.POST)
   @ResponseBody
   public String echo(@RequestParam(value = "message", required = true) String message)
         throws Exception {
      return _echoService.echo(message);
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

