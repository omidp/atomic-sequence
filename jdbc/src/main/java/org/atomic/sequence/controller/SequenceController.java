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

	@PostMapping("/invoice")
	public void create(){
		sequenceService.createInvoiceSeqTbl();
	}

}
