package com.project.schoolmanagment.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  //we are overwriting the method for AuthenticationException cases.
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    LOGGER.error("Unauthorized error : {}" , authException.getMessage());
    //we are setting the response type for our custom error
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    final Map<String, Object> body = new HashMap<>();
    body.put("status",HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error","Unauthorized");
    body.put("message",authException.getMessage());
    body.put("path",request.getServletPath());
    
    //we are creating json object for RESPONSE
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writeValue(response.getOutputStream(),body);
    
  }
}
