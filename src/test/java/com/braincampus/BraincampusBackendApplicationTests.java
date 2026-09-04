package com.braincampus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BraincampusBackendApplicationTests {

	@MockitoBean
	private JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}
