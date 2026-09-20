package com.gridpulse.authservice.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.username(), request.password()));
			String role = authentication.getAuthorities().stream()
					.findFirst()
					.map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
					.orElse("USER");
			String token = jwtService.createToken(authentication.getName(), role);

			return ResponseEntity.ok(new LoginResponse(
					token, "Bearer", authentication.getName(), role));
		} catch (AuthenticationException exception) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
