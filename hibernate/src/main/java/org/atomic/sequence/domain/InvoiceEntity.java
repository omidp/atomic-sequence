package org.atomic.sequence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.id.factory.internal.UUIDGenerationTypeStrategy;

import java.util.UUID;

@Entity
@Table(name = "hibernate_invoice")
@Data
@NoArgsConstructor
public class InvoiceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID uuid;

	@Column(name = "no")
	private long no;


	public InvoiceEntity(long no) {
		this.no = no;
	}
}
