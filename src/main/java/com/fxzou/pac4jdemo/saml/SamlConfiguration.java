package com.fxzou.pac4jdemo.saml;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.util.List;

@Configuration
public class SamlConfiguration {

    @Bean
    SAML2Configuration saml2Configuration() throws IOException {
        SAML2Configuration config = new SAML2Configuration(
                new ClassPathResource("samlKeystore.jks"),
                "pac4j-demo-passwd",
                "pac4j-demo-passwd",
                new UrlResource("https://idp.ssocircle.com/meta-idp.xml")
        );
        config.setForceAuth(true);
        config.setAuthnRequestBindingType(SAMLConstants.SAML2_POST_BINDING_URI);
        config.setAuthnRequestSigned(false);
        config.setWantsAssertionsSigned(false);
        config.setResponseBindingType(SAMLConstants.SAML2_POST_BINDING_URI);
        config.setUseNameQualifier(false);
        config.setNameIdPolicyFormat(NameID.EMAIL);
        config.setServiceProviderEntityId("urn:fxzou:pac4j:demo");
        config.setSignatureReferenceDigestMethods(List.of("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"));
        config.setSignMetadata(false);
        config.setPostLogoutURL("http://localhost:8090/sso/logout-success");
        config.setLogoutHandler(new LogoutHandler() {});
        return config;
    }

    @Bean
    SAML2Client saml2Client() throws IOException {
        SAML2Client saml2Client = new SAML2Client(saml2Configuration());
        saml2Client.setCallbackUrl("http://localhost:8090/sso");
        saml2Client.init();
        return saml2Client;
    }

}
