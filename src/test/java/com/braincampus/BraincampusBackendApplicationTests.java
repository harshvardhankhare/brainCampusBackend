package com.braincampus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mock;

@SpringBootTest
@ActiveProfiles("test")
class BraincampusBackendApplicationTests {

	@Mock
	private JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}
