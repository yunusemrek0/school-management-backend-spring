package com.project.schoolmanagment.payload.messages;

public class ErrorMessages {

  private ErrorMessages() {
  }

  //user
  public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s";
  public static final String NOT_FOUND_USER_MESSAGE_USERNAME = "Error: User not found with username %s";
  public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have any permission to do this operation";
  
  //user roles
  public static final String ROLE_NOT_FOUND = "There is no role like that, check the database";
  public static final String NOT_FOUND_USER_USER_ROLE_MESSAGE = "Error: User not found with user-role %s";
  
  //unique properties
  public static final String ALREADY_REGISTER_MESSAGE_USERNAME = "Error: User with username %s is already registered";
  public static final String ALREADY_REGISTER_MESSAGE_SSN = "Error: User with ssn %s is already registered";
  public static final String ALREADY_REGISTER_MESSAGE_PHONE_NUMBER = "Error: User with phone number %s is already registered";
  public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "Error: User with email %s is already registered";
  
  
  
  
  
  
}
