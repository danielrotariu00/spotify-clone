package com.spotify.songcollection.business.service;

import com.spotify.idm.AuthorizeResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import com.spotify.idm.AuthorizeRequest;

import java.util.List;

public class IdmClient extends WebServiceGatewaySupport {
    private static final String URI = "http://localhost:8080/ws/users";
    private static final String AUTHORIZE_REQUEST = "http://spotify.com/idm/AuthorizeRequest";
    private static final Integer CONTENT_MANAGER_ROLE_ID = 3;
    private static final Integer ARTIST_ROLE_ID = 2;

    public boolean isContentManager(String token) {
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        return roleIds.contains(CONTENT_MANAGER_ROLE_ID);
    }

    public boolean isContentManagerOrOwner(String token, Integer artistId) {
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        return roleIds.contains(CONTENT_MANAGER_ROLE_ID) ||
                (roleIds.contains(ARTIST_ROLE_ID) && response.getUserId() == artistId);
    }

    private AuthorizeResponse authorize(AuthorizeRequest request) {
        return  (AuthorizeResponse) getWebServiceTemplate()
                .marshalSendAndReceive(URI, request, new SoapActionCallback(AUTHORIZE_REQUEST));
    }
}