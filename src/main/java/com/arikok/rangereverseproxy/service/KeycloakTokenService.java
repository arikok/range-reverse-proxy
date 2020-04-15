package com.arikok.rangereverseproxy.service;

import com.arikok.rangereverseproxy.config.properties.KeycloakProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class KeycloakTokenService implements ITokenService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KeycloakProperties keycloakProperties;

	private BearerToken token;

	@Getter @Setter
	private class BearerToken {

		private BearerToken(String accessToken,LocalDateTime expireDate){
			this.accessToken = accessToken;
			this.expireDate = expireDate;
		}
		private String accessToken;
		private LocalDateTime expireDate;
	}

	public String getToken(){
		if(token == null || LocalDateTime.now().compareTo(token.expireDate) > 0 ){
			log.info(token == null ? "Token is null, new token will be generated"
					: "Token expired at: " + token.getExpireDate().toString() + ". New token will be generated");
			this.token = createToken();
		}
		return this.token.accessToken;
	}

	private BearerToken createToken() {
		try {

			if(StringUtils.isEmpty(keycloakProperties.getRealm()) ||
					StringUtils.isEmpty(keycloakProperties.getClientId()) ||
					StringUtils.isEmpty(keycloakProperties.getAuthServerUrl()) ||
					StringUtils.isEmpty(keycloakProperties.getClientSecret())) {
				throw new AssertionError("keycloak.realm, keycloak.resource, keycloak.auth-server-url, keycloak.credentials.secret properties are required!");
			}

			String tokenUrl = keycloakProperties.getAuthServerUrl()+"/auth/realms/"+keycloakProperties.getRealm()+"/protocol/openid-connect/token";

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			httpHeaders.set("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("client_id", keycloakProperties.getClientId());
			map.add("client_secret", keycloakProperties.getClientSecret());
			map.add("grant_type", "client_credentials");

			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

			ResponseEntity<Map> response = restTemplate.exchange(
					tokenUrl,
					HttpMethod.POST,
					httpEntity,
					Map.class);

			if(response.getStatusCode().isError()) {
				log.error("Token get service not working properly! "+response.getStatusCode());
			} else {
				Map resultMap = response.getBody();
				String accessToken = resultMap.get("access_token").toString();
				int expiresIn = Integer.parseInt(resultMap.get("expires_in").toString());

				LocalDateTime expireDate = LocalDateTime.now().plusSeconds(expiresIn - keycloakProperties.getReissueTokenMargin());

				return new BearerToken(accessToken,expireDate);
			}
		} catch (Exception e) {
			log.error("Token get service not working properly!", e);
		}

		return null;
	}

}
