package org.atomic.sequence;

import lombok.extern.slf4j.Slf4j;
import org.atomic.sequence.domain.SeqEntity;
import org.hibernate.SessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackageClasses = SeqEntity.class)
@Slf4j
public class HibernateApplication {

	public static void main(String[] args) {
		SpringApplication.run(HibernateApplication.class, args);
	}

	@Bean JdbcTemplate jdbcTemplate(DataSource ds){
		return new JdbcTemplate(ds);
	}

	@Bean CommandLineRunner cmd(DataSource dataSource, SessionFactory sessionFactory){
		return args -> {
			sessionFactory.inTransaction(session -> {
				SeqEntity seqEntity = new SeqEntity();
				seqEntity.setSeqNo(1);
				session.persist(seqEntity);
			});

		};
	}

}
