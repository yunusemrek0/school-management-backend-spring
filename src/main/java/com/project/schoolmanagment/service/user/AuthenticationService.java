package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.request.authentication.LoginRequest;
import com.project.schoolmanagment.payload.request.authentication.UpdatePasswordRequest;
import com.project.schoolmanagment.payload.response.authentication.AuthResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.security.jwt.JwtUtils;
import com.project.schoolmanagment.security.service.UserDetailsImpl;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public AuthResponse authenticateUser(LoginRequest request) {
    
    String username = request.getUsername();
    String password = request.getPassword();

    //injection of spring security authentication in service layer
    Authentication authentication = 
        authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(username,password));

    //validated authentication info is uploaded to security context
    SecurityContextHolder.getContext().setAuthentication(authentication);
    
    //create new JWT for user
    String token = jwtUtils.generateJwtToken(authentication);

    //get all info for user
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    //get roles from user
    Set<String> roles = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    //user can have only one role
    //we get it from roles collection
    String userRole = roles.stream().findFirst().get();
    
    //different way of using builder design pattern
    AuthResponse.AuthResponseBuilder responseBuilder = AuthResponse.builder();
    responseBuilder.username(userDetails.getUsername());
    responseBuilder.token(token);
    responseBuilder.name(userDetails.getName());
    responseBuilder.ssn(userDetails.getSsn());
    responseBuilder.role(userRole);
    
    return responseBuilder.build();    
    
  }

  public void updatePassword(UpdatePasswordRequest request, HttpServletRequest httpServletRequest) {

    String username = (String) httpServletRequest.getAttribute("username");

    User user =userRepository.findByUsername(username);

    //validate if user is not built-in
    if (user.getBuiltIn()){
      throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
    }
    // this method checks without decoding
    if (passwordEncoder.matches(request.getNewPassword(), request.getOldPassword())){
        throw new BadRequestException(ErrorMessages.PASSWORD_SHOULD_NOT_MATCHED);
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

  }


  public UserResponse findByUsername(HttpServletRequest httpServletRequest) {
    String username = (String) httpServletRequest.getAttribute("username");

    User user = userRepository.findByUsername(username);

    return userMapper.mapUserToUserResponse(user);
  }
}
