package org.atomic.sequence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceService {

	private final DataSource dataSource;


	/**
	 * Using postgres serial column not thread safe for monotonically increasing, gapless counter(sequential numbers)
	 *
	 * @return
	 */
	public long createInvoiceWithSerialColumn() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			PreparedStatement preparedStatement = con.prepareStatement("insert into serial_invoice(description) values(?) RETURNING invoice_no");
			preparedStatement.setString(1, "txt");
			preparedStatement.execute();
			var rs = preparedStatement.getResultSet();
			if (rs.next()) {
				long seqNumber = rs.getLong(1);
				log.info("seq num {}", seqNumber);
				return seqNumber;
			}
			con.commit();
			return -1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * You can delete the select for update and just use update.
	 * update in postgres exclusively lock the record
	 *
	 * @return
	 */
	@Deprecated
	public Long createInvoiceSeqTbl() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			ResultSet resultSet = con.prepareStatement("SELECT seq_no from jdbc_seq_tbl for update").executeQuery();
			if (resultSet.next()) {
				Long seqNumber = resultSet.getLong(1);
				PreparedStatement preparedStatement = con.prepareStatement("insert into invoice(no) values(?)");
				preparedStatement.setLong(1, seqNumber);
				preparedStatement.execute();
				con.prepareStatement("update jdbc_seq_tbl set seq_no = seq_no +1").execute();
				con.commit();
				log.info("seq num {}", seqNumber);
				return seqNumber;
			}
			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Using postgres sequence is not thread safe for monotonically increasing, gapless counter(sequential numbers)
	 *
	 * @return
	 */
	public Long createInvoiceDbSeq() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			ResultSet resultSet = con.prepareStatement("SELECT nextval('jdbc_seq')").executeQuery();
			if (resultSet.next()) {
				Long seqNumber = resultSet.getLong(1);
				PreparedStatement preparedStatement = con.prepareStatement("insert into invoice(no) values(?)");
				preparedStatement.setLong(1, seqNumber);
				preparedStatement.execute();
				con.commit();
				log.info("seq num {}", seqNumber);
				return seqNumber;
			}
			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Long createInvoiceUpdateRowLocking() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(
				"update jdbc_seq_tbl set seq_no = seq_no +1 RETURNING seq_no");
			ps.execute();
			var rs = ps.getResultSet();
			if (rs.next()) {
				long seqNumber = rs.getLong(1);
				PreparedStatement preparedStatement = con.prepareStatement(
					"insert into invoice(no) values(?)");
				preparedStatement.setLong(1, seqNumber);
				preparedStatement.execute();
				con.commit();
				log.info("seq num {}", seqNumber);
				return seqNumber;
			}
			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
