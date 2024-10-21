package org.atomic.sequence.service;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atomic.sequence.domain.InvoiceEntity;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.AbstractReturningWork;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceService {

	private final SessionFactory sessionFactory;

	public long createInvoicePessimisticWrite() {
		return sessionFactory.fromTransaction(session -> {
			Long seqNumber = session.createQuery("select se.seqNo from SeqEntity se", Number.class)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.setMaxResults(1).getSingleResult().longValue();
			session.persist(new InvoiceEntity(seqNumber));
			session.createQuery("update SeqEntity se set se.seqNo = se.seqNo+1").executeUpdate();
			log.info("SeqNo {}", seqNumber);
			return seqNumber;
		});
	}


	public Long createInvoiceUpdateRowLocking() {
		return sessionFactory.fromTransaction(session -> {
			Long seqNumber =  session.doReturningWork(new AbstractReturningWork<Long>() {
				@Override
				public Long execute(Connection connection) throws SQLException {
					PreparedStatement preparedStatement = connection.prepareStatement("update hibernate_seq_tbl set seq_no = seq_no +1 RETURNING seq_no");
					preparedStatement.execute();
					ResultSet resultSet = preparedStatement.getResultSet();
					if (resultSet.next()) {
						return resultSet.getLong(1);
					}
					return null;
				}
			});
			session.persist(new InvoiceEntity(seqNumber));
			log.info("SeqNo {}", seqNumber);
			return seqNumber;
		});
	}

}
