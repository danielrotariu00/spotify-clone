//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.12.16 at 09:02:01 PM EST 
//


package com.spotify.idm;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.spotify.idm package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.spotify.idm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateUserRequest }
     * 
     */
    public CreateUserRequest createCreateUserRequest() {
        return new CreateUserRequest();
    }

    /**
     * Create an instance of {@link UpdatePasswordRequest }
     * 
     */
    public UpdatePasswordRequest createUpdatePasswordRequest() {
        return new UpdatePasswordRequest();
    }

    /**
     * Create an instance of {@link AddRoleRequest }
     * 
     */
    public AddRoleRequest createAddRoleRequest() {
        return new AddRoleRequest();
    }

    /**
     * Create an instance of {@link DeleteRoleRequest }
     * 
     */
    public DeleteRoleRequest createDeleteRoleRequest() {
        return new DeleteRoleRequest();
    }

    /**
     * Create an instance of {@link GetUserRequest }
     * 
     */
    public GetUserRequest createGetUserRequest() {
        return new GetUserRequest();
    }

    /**
     * Create an instance of {@link GetUsersRequest }
     * 
     */
    public GetUsersRequest createGetUsersRequest() {
        return new GetUsersRequest();
    }

    /**
     * Create an instance of {@link DeleteUserRequest }
     * 
     */
    public DeleteUserRequest createDeleteUserRequest() {
        return new DeleteUserRequest();
    }

    /**
     * Create an instance of {@link GetRolesRequest }
     * 
     */
    public GetRolesRequest createGetRolesRequest() {
        return new GetRolesRequest();
    }

    /**
     * Create an instance of {@link LoginRequest }
     * 
     */
    public LoginRequest createLoginRequest() {
        return new LoginRequest();
    }

    /**
     * Create an instance of {@link LogoutRequest }
     * 
     */
    public LogoutRequest createLogoutRequest() {
        return new LogoutRequest();
    }

    /**
     * Create an instance of {@link AuthorizeRequest }
     * 
     */
    public AuthorizeRequest createAuthorizeRequest() {
        return new AuthorizeRequest();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link AuthorizeResponse }
     * 
     */
    public AuthorizeResponse createAuthorizeResponse() {
        return new AuthorizeResponse();
    }

    /**
     * Create an instance of {@link UserResponse }
     * 
     */
    public UserResponse createUserResponse() {
        return new UserResponse();
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link UsersResponse }
     * 
     */
    public UsersResponse createUsersResponse() {
        return new UsersResponse();
    }

    /**
     * Create an instance of {@link RolesResponse }
     * 
     */
    public RolesResponse createRolesResponse() {
        return new RolesResponse();
    }

    /**
     * Create an instance of {@link Role }
     * 
     */
    public Role createRole() {
        return new Role();
    }

}
