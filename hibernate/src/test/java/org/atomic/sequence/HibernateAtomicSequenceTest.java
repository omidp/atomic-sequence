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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HibernateAtomicSequenceTest {

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
	void testCreateInvoicePessimisticWrite() throws InterruptedException {
		List<Long> expected = new ArrayList<>();
		List<Long> actual = Collections.synchronizedList(new ArrayList<>());
		int numberOfThreads = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		for (long i = 1; i <= numberOfThreads; i++) {
			expected.add(i);
			executorService.submit(()->{
				actual.add(sequenceService.createInvoicePessimisticWrite());
			});
		}
		executorService.awaitTermination(3, TimeUnit.SECONDS);
		executorService.shutdown();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testCreateInvoiceUpdateRowLocking() throws InterruptedException {
		List<Long> expected = new ArrayList<>();
		List<Long> actual = Collections.synchronizedList(new ArrayList<>());
		int numberOfThreads = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		for (long i = 1; i <= numberOfThreads; i++) {
			expected.add(i);
			executorService.submit(()->{
				actual.add(sequenceService.createInvoiceUpdateRowLocking()-1);
			});
		}
		executorService.awaitTermination(3, TimeUnit.SECONDS);
		executorService.shutdown();
		assertThat(actual).isEqualTo(expected);
	}

}