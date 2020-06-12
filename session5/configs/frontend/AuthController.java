package com.course.servicemesh.frontend.courseservicemeshfrontend.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.course.servicemesh.frontend.courseservicemeshfrontend.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class AuthController {
  private final AuthenticationController controller;

  @Value(value = "${com.auth0.domain}")
  private String domain;

  @Value(value = "${com.auth0.clientId}")
  private String clientId;

  @Autowired
  public AuthController(AppConfig config) {
    JwkProvider jwkProvider = new JwkProviderBuilder(config.getDomain()).build();
    controller = AuthenticationController.newBuilder(config.getDomain(), config.getClientId(), config.getClientSecret())
        .withJwkProvider(jwkProvider)
        .build();
  }

  public Tokens handle(HttpServletRequest request, HttpServletResponse response) throws IdentityVerificationException {
    return controller.handle(request, response);
  }

  public String buildAuthorizeUrl(HttpServletRequest request, HttpServletResponse response, String redirectUri) {
    return controller.buildAuthorizeUrl(request, response, redirectUri)
        .build();
  }

  @GetMapping(value = "/login")
  protected String login(final HttpServletRequest req, final HttpServletResponse res) {
    String redirectUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/callback";
    String authorizeUrl = buildAuthorizeUrl(req, res, redirectUrl);
    return "redirect:" + authorizeUrl;
  }

  @GetMapping(value = "/callback")
  protected void getCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException,
                                                                                                 IOException
  {
    try {
      Tokens tokens = controller.handle(req, res);
      SessionUtils.set(req, "accessToken", tokens.getAccessToken());
      SessionUtils.set(req, "idToken", tokens.getIdToken());
      res.sendRedirect("/api/v1/dashboard");
    } catch (IdentityVerificationException e) {
      res.sendRedirect("/login");
    }
  }

  @GetMapping(value = "/logout")
  protected String logout(final HttpServletRequest req) {
    invalidateSession(req);

    String returnTo = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

    // Build logout URL like:
    // https://{YOUR-DOMAIN}/v2/logout?client_id={YOUR-CLIENT-ID}&returnTo=http://localhost:3000
    String logoutUrl = String.format("https://%s/v2/logout?client_id=%s&returnTo=%s", domain, clientId, returnTo);

    return "redirect:" + logoutUrl;
  }

  private void invalidateSession(HttpServletRequest request) {
    if (request.getSession() != null) {
      request.getSession().invalidate();
    }
  }
}
