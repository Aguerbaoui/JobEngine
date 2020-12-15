package io.je.project.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.rulebuilder.models.OperandModel;

/*
 * Rule builder Rest Controller
 * */
@RestController
public class RuleController {

	/*
	 * Add a new operand from front
	 */
	@PostMapping(value = "/addOperand", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addOperand(@RequestBody OperandModel m) {
		
		return ResponseEntity.ok("");
	}
}