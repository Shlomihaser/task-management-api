package com.example.taskmanagement;

import com.example.taskmanagement.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class TaskManagementApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
