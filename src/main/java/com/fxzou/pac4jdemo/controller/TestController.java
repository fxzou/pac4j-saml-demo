package com.fxzou.pac4jdemo.controller;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/sso")
public class TestController {
    private final SAML2Client saml2Client;
    private SAML2Profile userProfile;

    public TestController(SAML2Client saml2Client) {
        this.saml2Client = saml2Client;
    }

    @GetMapping("/metadata")
    public String metadata() throws IOException {
        return saml2Client.getServiceProviderMetadataResolver().getMetadata();
    }

    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        JEEContext context = new JEEContext(request, response);
        Optional<RedirectionAction> redirect = saml2Client.redirect(context);
        redirect.ifPresent((action) -> JEEHttpActionAdapter.INSTANCE.adapt(action, context));
    }

    @RequestMapping
    public String sso(HttpServletRequest request, HttpServletResponse response) {
        JEEContext context = new JEEContext(request, response);
        try {
            Optional<SAML2Credentials> credentials = saml2Client.getCredentials(context);
            SAML2Credentials saml2Credentials = credentials.orElseThrow();
            userProfile = (SAML2Profile) saml2Credentials.getUserProfile();
            return "success: " + userProfile.getId();
        } catch (RedirectionAction action) {
            JEEHttpActionAdapter.INSTANCE.adapt(action, context);
            return null;
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        JEEContext context = new JEEContext(request, response);
        SAML2Profile saml2Profile = new SAML2Profile() {
            @Override
            public String getSessionIndex() {
                return userProfile.getSessionIndex();
            }

            @Override
            public String getId() {
                return userProfile.getId();
            }
        };
        Optional<RedirectionAction> redirect = saml2Client.getLogoutAction(context, saml2Profile, null);
        redirect.ifPresent((action) -> JEEHttpActionAdapter.INSTANCE.adapt(action, context));
    }

    @RequestMapping("/logout-success")
    public String logoutSuccess() {
        return "logout success";
    }

}
