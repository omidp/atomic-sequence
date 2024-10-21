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


	public long createSerialInvoice() {
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

	public long createInvoiceSeqTbl() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			long seqNumber = getSeqNumberTbl(con);
			log.info("seq num {}", seqNumber);
			PreparedStatement preparedStatement = con.prepareStatement("insert into invoice(no) values(?)");
			preparedStatement.setLong(1, seqNumber);
			preparedStatement.execute();
			con.prepareStatement("update jdbc_seq_tbl set seq_no = seq_no +1").execute();
			con.commit();
			return seqNumber;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public long createInvoiceDbSeq() {
		try (var con = getConnection()) {
			con.setAutoCommit(false);
			long seqNumber = getSeqNumber(con);
			log.info("seq num {}", seqNumber);
			PreparedStatement preparedStatement = con.prepareStatement("insert into invoice(no) values(?)");
			preparedStatement.setLong(1, seqNumber);
			preparedStatement.execute();
			con.commit();
			return seqNumber;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	private long getSeqNumberTbl(Connection connection) throws SQLException {
		ResultSet resultSet = connection.prepareStatement("SELECT seq_no from jdbc_seq_tbl for update").executeQuery();
		if (resultSet.next()) {
			return resultSet.getLong(1);
		}
		return -1;
	}

	private long getSeqNumber(Connection connection) throws SQLException {
		ResultSet resultSet = connection.prepareStatement("SELECT nextval('jdbc_seq')").executeQuery();
		if (resultSet.next()) {
			return resultSet.getLong(1);
		}
		return -1;
	}

	private Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public long createInvoiceUpdateSeqTbl() {
    try (var con = getConnection()) {
      con.setAutoCommit(false);
//			long seqNumber = getSeqNumberTbl(con);
      PreparedStatement ps = con.prepareStatement(
          "update jdbc_seq_tbl set seq_no = seq_no +1 RETURNING seq_no");
      ps.execute();
      var rs = ps.getResultSet();
      if (rs.next()) {
        long seqNumber = rs.getLong(1);
        log.info("seq num {}", seqNumber);
        PreparedStatement preparedStatement = con.prepareStatement(
            "insert into invoice(no) values(?)");
        preparedStatement.setLong(1, seqNumber);
        preparedStatement.execute();
        con.commit();
        return seqNumber;
      }
      return -1;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
