package com.gridpulse.authservice.auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final long expirationSeconds;

	public JwtService(
			JwtEncoder jwtEncoder,
			@Value("${gridpulse.jwt.expiration-seconds}") long expirationSeconds) {
		this.jwtEncoder = jwtEncoder;
		this.expirationSeconds = expirationSeconds;
	}

	public String createToken(String username, String role) {
		Instant issuedAt = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("gridpulse-auth-service")
				.subject(username)
				.issuedAt(issuedAt)
				.expiresAt(issuedAt.plusSeconds(expirationSeconds))
				.claim("role", role)
				.build();
		JwsHeader header = JwsHeader.with(() -> JwsAlgorithms.HS256)
				.type("JWT")
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
