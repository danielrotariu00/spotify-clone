package com.spotify.idm.presentation.endpoint;

import com.spotify.idm.business.service.JwtProvider;
import com.spotify.idm.business.service.UserService;
import com.spotify.idm.AddRoleRequest;
import com.spotify.idm.AuthorizeRequest;
import com.spotify.idm.AuthorizeResponse;
import com.spotify.idm.CreateUserRequest;
import com.spotify.idm.DeleteRoleRequest;
import com.spotify.idm.DeleteUserRequest;
import com.spotify.idm.GetRolesRequest;
import com.spotify.idm.GetUserRequest;
import com.spotify.idm.GetUsersRequest;
import com.spotify.idm.LoginRequest;
import com.spotify.idm.LoginResponse;
import com.spotify.idm.LogoutRequest;
import com.spotify.idm.RolesResponse;
import com.spotify.idm.UpdatePasswordRequest;
import com.spotify.idm.User;
import com.spotify.idm.UserResponse;
import com.spotify.idm.UsersResponse;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class UserEndpoint {
	private static final String NAMESPACE_URI = "http://spotify.com/idm";

	private final UserService userService;
	private final JwtProvider jwtProvider;

	@Autowired
	public UserEndpoint(UserService userService, JwtProvider jwtProvider) {
		this.userService = userService;
		this.jwtProvider = jwtProvider;
	}


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createUserRequest")
	@ResponsePayload
	public UserResponse createUser(@RequestPayload CreateUserRequest request) {
		return userService.createUser(request);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updatePasswordRequest")
	@ResponsePayload
	public UserResponse updatePassword(@RequestPayload UpdatePasswordRequest request) throws MalformedClaimException {
		if (jwtProvider.isAuthorized(request.getToken(), request.getUserId())) {
			return userService.updatePassword(request);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addRoleRequest")
	@ResponsePayload
	public UserResponse addRole(@RequestPayload AddRoleRequest request) throws MalformedClaimException {
		if (jwtProvider.hasAdminRights(request.getToken())) {
			return userService.addRole(request);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteRoleRequest")
	@ResponsePayload
	public UserResponse deleteRole(@RequestPayload DeleteRoleRequest request) throws MalformedClaimException {
		if (jwtProvider.hasAdminRights(request.getToken())) {
			return userService.deleteRole(request);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
	@ResponsePayload
	public UserResponse getUser(@RequestPayload GetUserRequest request) throws MalformedClaimException {
		if (jwtProvider.isAuthorized(request.getToken(), request.getUserId())) {
			return userService.getUser(request);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUsersRequest")
	@ResponsePayload
	public UsersResponse getUsers(@RequestPayload GetUsersRequest request) throws MalformedClaimException {
		if (jwtProvider.hasAdminRights(request.getToken())) {
			return userService.getUsers();
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
	@ResponsePayload
	public UserResponse deleteUser(@RequestPayload DeleteUserRequest request) throws MalformedClaimException {
		if (jwtProvider.isAuthorized(request.getToken(), request.getUserId())) {
			return userService.deleteUser(request);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRolesRequest")
	@ResponsePayload
	public RolesResponse getRoles(@RequestPayload GetRolesRequest request) throws MalformedClaimException {
		if (jwtProvider.hasAdminRights(request.getToken())) {
			return userService.getRoles();
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "loginRequest")
	@ResponsePayload
	public LoginResponse login(@RequestPayload LoginRequest request) throws JoseException {
		User user = userService.login(request);
		String jwt = jwtProvider.generateJwt(user);
		LoginResponse response = new LoginResponse();

		response.setToken(jwt);

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "logoutRequest")
	@ResponsePayload
	public void logout(@RequestPayload LogoutRequest request) {
		jwtProvider.invalidate(request.getToken());
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "authorizeRequest")
	@ResponsePayload
	public AuthorizeResponse authorize(@RequestPayload AuthorizeRequest request) throws MalformedClaimException {
		JwtClaims claims = jwtProvider.validate(request.getToken());
		AuthorizeResponse response = new AuthorizeResponse();

		List<Integer> roleIds = claims.getStringListClaimValue("roleIds").stream()
				.map(Integer::valueOf)
				.collect(Collectors.toList());

		response.setUserId(Integer.parseInt(claims.getSubject()));
		response.getRoleIds().addAll(roleIds);

		return response;
	}
}
