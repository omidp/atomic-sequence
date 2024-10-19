package org.atomic.sequence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
@Slf4j
public class JdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

	@Bean JdbcTemplate jdbcTemplate(DataSource ds){
		return new JdbcTemplate(ds);
	}

	@Bean CommandLineRunner cmd(DataSource dataSource){
		return args -> {
			try(var con = dataSource.getConnection()) {
				String sql = """
					CREATE SEQUENCE IF NOT EXISTS jdbc_seq
					    INCREMENT BY 1
					    START WITH 1
					    MINVALUE 1
					    MAXVALUE  9999999999
					    CACHE 1
					""";
				con.prepareStatement(sql).execute();
				//
				con.prepareStatement("DROP TABLE IF EXISTS jdbc_seq_tbl").execute();
				con.prepareStatement("CREATE TABLE IF NOT EXISTS jdbc_seq_tbl (seq_no bigint)").execute();
				var pr = con.prepareStatement("INSERT INTO jdbc_seq_tbl(seq_no) values(?)");
				pr.setLong(1, 1L);
				pr.execute();
				log.info("sequence is created.");
				con.prepareStatement("CREATE TABLE IF NOT EXISTS invoice (no bigint)").execute();
				log.info("invoice table is created.");
			}

		};
	}

}
