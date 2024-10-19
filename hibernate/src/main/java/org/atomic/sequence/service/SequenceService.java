package org.atomic.sequence.service;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atomic.sequence.domain.InvoiceEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceService {

	private final SessionFactory sessionFactory;

	public long createInvoiceSeqTbl() {
		return sessionFactory.fromTransaction(session -> {
			Long seqNumber = session.createQuery("select se.seqNo from SeqEntity se", Number.class)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.setMaxResults(1).getSingleResult().longValue();
			session.persist(new InvoiceEntity(seqNumber));
			session.createQuery("update SeqEntity se set se.seqNo = se.seqNo+1").executeUpdate();
			return seqNumber;
		});
	}

}
