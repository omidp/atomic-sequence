package org.atomic.sequence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hibernate_seq_tbl")
@Data
public class SeqEntity {

	@Column(name = "seq_no")
	@Id
	private long seqNo;

}
