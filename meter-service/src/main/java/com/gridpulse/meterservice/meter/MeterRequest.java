package com.gridpulse.meterservice.meter;

import java.time.Instant;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record MeterRequest(
		@NotBlank(groups = Create.class)
		@Size(max = 255)
		@Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]*") String id,
		@NotBlank @Size(max = 255) String deviceName,
		@NotBlank @Size(max = 255) String facility,
		@NotBlank @Size(max = 255) String zone,
		@NotNull MeterStatus status,
		@NotNull @PositiveOrZero @DecimalMax("1.7976931348623157E308") Double currentLoad,
		@NotNull @PositiveOrZero @DecimalMax("1.7976931348623157E308") Double voltage,
		@NotNull @PositiveOrZero @DecimalMax("1.7976931348623157E308") Double frequency,
		@NotNull MeterHealth health,
		@NotNull Instant lastCommunication) {

	public interface Create extends Default {
	}
}
