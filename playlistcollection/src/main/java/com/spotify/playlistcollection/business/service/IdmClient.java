package com.spotify.playlistcollection.business.service;

import com.spotify.idm.AuthorizeResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import com.spotify.idm.AuthorizeRequest;

import java.util.List;

public class IdmClient extends WebServiceGatewaySupport {
    private static final String URI = "http://localhost:8080/ws/users";
    private static final String AUTHORIZE_REQUEST = "http://spotify.com/idm/AuthorizeRequest";
    private static final Integer CLIENT_ROLE_ID = 1;

    public boolean isOwner(String token, Integer userId) {
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        return roleIds.contains(CLIENT_ROLE_ID) && response.getUserId() == userId;
    }

    private AuthorizeResponse authorize(AuthorizeRequest request) {
        return  (AuthorizeResponse) getWebServiceTemplate()
                .marshalSendAndReceive(URI, request, new SoapActionCallback(AUTHORIZE_REQUEST));
    }
}