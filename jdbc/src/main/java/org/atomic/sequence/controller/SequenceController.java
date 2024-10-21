package org.atomic.sequence.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atomic.sequence.service.SequenceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SequenceController {

	private final SequenceService sequenceService;

	@PostMapping("/createInvoiceSeqTbl")
	public Long createInvoiceSeqTbl(){
		return sequenceService.createInvoiceSeqTbl();
	}

	@PostMapping("/createInvoiceWithSerialColumn")
	public Long createInvoiceWithSerialColumn(){
		return sequenceService.createInvoiceWithSerialColumn();
	}

	@PostMapping("/createInvoiceUpdateRowLocking")
	public Long createInvoiceUpdateRowLocking(){
		return sequenceService.createInvoiceUpdateRowLocking();
	}

	@PostMapping("/createInvoiceDbSeq")
	public Long createInvoiceDbSeq(){
		return sequenceService.createInvoiceDbSeq();
	}

}
