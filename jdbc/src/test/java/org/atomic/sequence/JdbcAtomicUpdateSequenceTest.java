package org.atomic.sequence;

import org.atomic.sequence.service.SequenceService;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.in;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcAtomicUpdateSequenceTest {

	@ClassRule
	private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));


	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Autowired
	private SequenceService sequenceService;

	@BeforeAll
	static void startDb() {
		postgreSQLContainer.start();
	}

	@AfterAll
	static void stopDb() {
		postgreSQLContainer.stop();
	}

	@Test
	public void containerRunning() {
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@Test
	void testCreateInvoiceUpdateSeqTbl() throws InterruptedException, ExecutionException, TimeoutException {
		List<Long> expected = new ArrayList<>();
		List<Long> actual = Collections.synchronizedList(new ArrayList<>());
		int numberOfThreads = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		for (long i = 2; i <= numberOfThreads+1; i++) {
			expected.add(i);
			executorService.submit(() -> actual.add(sequenceService.createInvoiceUpdateSeqTbl()));
		}

		executorService.awaitTermination(8, TimeUnit.SECONDS);
		executorService.shutdown();
		assertThat(actual).isEqualTo(expected);
	}



}
