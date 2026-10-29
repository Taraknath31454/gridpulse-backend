package com.gridpulse.meterservice.meter;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeterCrudTests {

	private static final HttpClient HTTP = HttpClient.newHttpClient();
	private static final String ID = "CRUD-TEST";
	private static final String BODY = """
			{
			  "id": "CRUD-TEST",
			  "deviceName": "Review 2 Meter",
			  "facility": "Test Facility",
			  "zone": "Test Zone",
			  "status": "ONLINE",
			  "currentLoad": 12.5,
			  "voltage": 230.0,
			  "frequency": 50.0,
			  "health": "HEALTHY",
			  "lastCommunication": "2026-10-29T12:00:00Z"
			}
			""";

	@LocalServerPort
	private int port;

	@Autowired
	private MeterRepository repository;

	@AfterEach
	void removeTestMeter() {
		repository.findByIdIgnoreCase(ID).ifPresent(repository::delete);
	}

	@Test
	void instanceHeaderReportsActualPortAndCannotBeSpoofedByTheClient() throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/meters/MTR-1001"))
				.timeout(Duration.ofSeconds(10)).header("X-GridPulse-Instance", "spoofed").GET().build();
		HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.headers().firstValue("X-GridPulse-Instance")).contains(Integer.toString(port));
		assertThat(JsonPath.<String>read(response.body(), "$.id")).isEqualTo("MTR-1001");
	}

	@Test
	void instanceHeaderIsPresentOnNotFoundResponses() throws Exception {
		HttpResponse<String> response = request("GET", "/meters/MISSING-METER", null);
		assertThat(response.statusCode()).isEqualTo(404);
		assertThat(response.headers().firstValue("X-GridPulse-Instance")).contains(Integer.toString(port));
	}

	@Test
	void existingGetContractAndCaseInsensitiveLookupArePreserved() throws Exception {
		HttpResponse<String> response = request("GET", "/meters/mtr-1001", null);
		assertThat(response.statusCode()).isEqualTo(200);
		Map<String, Object> meter = JsonPath.read(response.body(), "$");
		assertThat(meter).containsOnlyKeys("id", "deviceName", "facility", "zone", "status",
				"currentLoad", "voltage", "frequency", "health", "lastCommunication");
		assertThat(meter).containsEntry("id", "MTR-1001")
				.containsEntry("deviceName", "North Feeder Meter")
				.containsEntry("currentLoad", 182.4);
		HttpResponse<String> all = request("GET", "/meters", null);
		assertThat(all.statusCode()).isEqualTo(200);
		List<String> ids = JsonPath.read(all.body(), "$[*].id");
		assertThat(ids).containsExactlyInAnyOrder("MTR-1001", "MTR-1002", "MTR-1003", "MTR-1004");
	}

	@Test
	void createReturns201WithLocationAndCommitsToRepository() throws Exception {
		HttpResponse<String> response = request("POST", "/meters", BODY);
		assertThat(response.statusCode()).isEqualTo(201);
		assertThat(response.headers().firstValue("Location")).contains("http://localhost:" + port + "/meters/" + ID);
		assertThat(JsonPath.<String>read(response.body(), "$.id")).isEqualTo(ID);
		Meter saved = repository.findById(ID).orElseThrow();
		assertThat(saved.getDeviceName()).isEqualTo("Review 2 Meter");
		assertThat(saved.getLastCommunication()).isEqualTo(Instant.parse("2026-10-29T12:00:00Z"));
		assertThat(request("GET", "/meters/" + ID, null).statusCode()).isEqualTo(200);
	}

	@ParameterizedTest
	@MethodSource("duplicateIds")
	void duplicateCreateReturns409WithoutOverwritingExistingMeter(String duplicateId) throws Exception {
		String body = JsonPath.parse(BODY).set("$.id", duplicateId).jsonString();
		assertThat(request("POST", "/meters", body).statusCode()).isEqualTo(409);
		assertThat(repository.findById("MTR-1001").orElseThrow().getDeviceName()).isEqualTo("North Feeder Meter");
		assertThat(repository.count()).isEqualTo(4);
	}

	static Stream<String> duplicateIds() {
		return Stream.of("MTR-1001", "mtr-1001");
	}

	@Test
	void updateReplacesAllMutableFieldsAndKeepsOriginalId() throws Exception {
		createFixture();
		String body = JsonPath.parse(BODY).set("$.id", "crud-test")
				.set("$.deviceName", "Updated Meter").set("$.facility", "Updated Facility")
				.set("$.zone", "Updated Zone").set("$.status", "WARNING")
				.set("$.currentLoad", 25.0).set("$.voltage", 228.0).set("$.frequency", 49.9)
				.set("$.health", "DEGRADED").set("$.lastCommunication", "2026-10-29T13:00:00Z").jsonString();
		HttpResponse<String> response = request("PUT", "/meters/crud-test", body);
		assertThat(response.statusCode()).isEqualTo(200);
		Meter updated = repository.findById(ID).orElseThrow();
		assertThat(updated.getId()).isEqualTo(ID);
		assertThat(updated.getDeviceName()).isEqualTo("Updated Meter");
		assertThat(updated.getFacility()).isEqualTo("Updated Facility");
		assertThat(updated.getZone()).isEqualTo("Updated Zone");
		assertThat(updated.getStatus()).isEqualTo(MeterStatus.WARNING);
		assertThat(updated.getCurrentLoad()).isEqualTo(25.0);
		assertThat(updated.getVoltage()).isEqualTo(228.0);
		assertThat(updated.getFrequency()).isEqualTo(49.9);
		assertThat(updated.getHealth()).isEqualTo(MeterHealth.DEGRADED);
		assertThat(updated.getLastCommunication()).isEqualTo(Instant.parse("2026-10-29T13:00:00Z"));
		assertThat(JsonPath.<String>read(response.body(), "$.id")).isEqualTo(ID);
	}

	@Test
	void updateCanUseUrlIdWhenBodyIdIsOmitted() throws Exception {
		createFixture();
		String body = JsonPath.parse(BODY).delete("$.id").set("$.deviceName", "Updated Meter").jsonString();
		assertThat(request("PUT", "/meters/" + ID, body).statusCode()).isEqualTo(200);
		assertThat(repository.findById(ID).orElseThrow().getDeviceName()).isEqualTo("Updated Meter");
	}

	@Test
	void updateCannotRenameAMeter() throws Exception {
		createFixture();
		String body = JsonPath.parse(BODY).set("$.id", "OTHER-ID").jsonString();
		assertThat(request("PUT", "/meters/" + ID, body).statusCode()).isEqualTo(400);
		assertThat(repository.existsById("OTHER-ID")).isFalse();
		assertThat(repository.findById(ID).orElseThrow().getDeviceName()).isEqualTo("Review 2 Meter");
	}

	@Test
	void deleteReturns204AndRemovesOnlyTheRequestedMeter() throws Exception {
		createFixture();
		HttpResponse<String> response = request("DELETE", "/meters/crud-test", null);
		assertThat(response.statusCode()).isEqualTo(204);
		assertThat(response.body()).isEmpty();
		assertThat(repository.existsById(ID)).isFalse();
		assertThat(repository.count()).isEqualTo(4);
		assertThat(request("GET", "/meters/" + ID, null).statusCode()).isEqualTo(404);
	}

	@Test
	void missingReadUpdateAndDeleteReturn404WithoutCreatingAMeter() throws Exception {
		assertThat(request("GET", "/meters/" + ID, null).statusCode()).isEqualTo(404);
		assertThat(request("PUT", "/meters/" + ID, BODY).statusCode()).isEqualTo(404);
		assertThat(request("DELETE", "/meters/" + ID, null).statusCode()).isEqualTo(404);
		assertThat(repository.existsById(ID)).isFalse();
	}

	@ParameterizedTest(name = "create rejects invalid {0}: {1}")
	@MethodSource("invalidValues")
	void invalidCreateReturns400WithoutSaving(String field, Object value) throws Exception {
		String body = JsonPath.parse(BODY).set("$." + field, value).jsonString();
		assertThat(request("POST", "/meters", body).statusCode()).isEqualTo(400);
		assertThat(repository.count()).isEqualTo(4);
	}

	static Stream<Arguments> invalidValues() {
		return Stream.of(
				Arguments.of("id", null), Arguments.of("id", " "), Arguments.of("id", "bad/id"),
				Arguments.of("id", "x".repeat(256)), Arguments.of("deviceName", ""),
				Arguments.of("deviceName", "x".repeat(256)), Arguments.of("facility", null),
				Arguments.of("zone", " "), Arguments.of("status", null), Arguments.of("status", "INVALID"),
				Arguments.of("health", null), Arguments.of("health", "INVALID"),
				Arguments.of("currentLoad", -1), Arguments.of("currentLoad", null),
				Arguments.of("voltage", -1), Arguments.of("voltage", null),
				Arguments.of("frequency", -1), Arguments.of("frequency", null),
				Arguments.of("lastCommunication", null), Arguments.of("lastCommunication", "invalid-time"));
	}

	@Test
	void missingNumericFieldIsRejectedInsteadOfDefaultingToZero() throws Exception {
		String body = JsonPath.parse(BODY).delete("$.voltage").jsonString();
		assertThat(request("POST", "/meters", body).statusCode()).isEqualTo(400);
		assertThat(repository.existsById(ID)).isFalse();
	}

	@Test
	void invalidUpdateDoesNotChangeExistingValues() throws Exception {
		createFixture();
		String body = JsonPath.parse(BODY).set("$.deviceName", "Changed").set("$.currentLoad", -1).jsonString();
		assertThat(request("PUT", "/meters/" + ID, body).statusCode()).isEqualTo(400);
		assertThat(repository.findById(ID).orElseThrow().getDeviceName()).isEqualTo("Review 2 Meter");
	}

	@Test
	void malformedAndMissingBodiesReturn400() throws Exception {
		assertThat(request("POST", "/meters", "{").statusCode()).isEqualTo(400);
		assertThat(request("POST", "/meters", null).statusCode()).isEqualTo(400);
		assertThat(request("POST", "/meters", "null").statusCode()).isEqualTo(400);
		assertThat(request("PUT", "/meters/" + ID, "{").statusCode()).isEqualTo(400);
	}

	@Test
	void offlineMetersMayHaveZeroMeasurements() throws Exception {
		String body = JsonPath.parse(BODY).set("$.status", "OFFLINE").set("$.currentLoad", 0)
				.set("$.voltage", 0).set("$.frequency", 0).jsonString();
		assertThat(request("POST", "/meters", body).statusCode()).isEqualTo(201);
		assertThat(repository.findById(ID).orElseThrow().getVoltage()).isZero();
	}

	private void createFixture() throws Exception {
		assertThat(request("POST", "/meters", BODY).statusCode()).isEqualTo(201);
	}

	private HttpResponse<String> request(String method, String path, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
				.timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
				.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
				.build();
		return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
