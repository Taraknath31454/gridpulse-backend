package com.gridpulse.meterservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class MeterServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}
