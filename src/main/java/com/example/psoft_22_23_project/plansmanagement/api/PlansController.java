/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.psoft_22_23_project.plansmanagement.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.plansmanagement.services.PlansService;
import com.example.psoft_22_23_project.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Plans", description = "Endpoints for managing plans")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlansController {

	private static final Logger logger = LoggerFactory.getLogger(PlansController.class);

	private final PlansService service;

	private final PlansViewMapper plansViewMapper;

	private final FeeRevisionViewMapper feeRevisionViewMapper;

	private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
		if (ifMatchHeader.startsWith("\"")) {
			return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
		}
		return Long.parseLong(ifMatchHeader);
	}

	@Operation(summary = "Gets all plans")
	@GetMapping
	public Iterable<PlansView> findActive() {
		return plansViewMapper.toPlansView(service.findAtive());
	}

	@Operation(summary = "Creates a new Plan")
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED) public ResponseEntity<PlansView>
	create(@Valid @RequestBody final CreatePlanRequest resource) {
		final Plans plan = service.create(resource);
		final var newPlanUri =
				ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(plan.getName().getName()).build() .toUri();
		logger.info("Creating plan: name={}, monthlyFee={}, annualFee={}",
				Utils.sanitize(Utils.sanitize(resource.getName())), resource.getMonthlyFee(), resource.getAnnualFee());
		return
				ResponseEntity.created(newPlanUri).eTag(Long.toString(plan.getVersion()))
						.body(plansViewMapper.toPlansView(plan)); }


	@Operation(summary = "Gets money history of plan")
	@GetMapping(value = "/history/{name}")
	public List<FeeRevisionView> history(@PathVariable("name") @Parameter(description = "The name of the plan to get history") final String name) {
		return feeRevisionViewMapper.toFeesView(service.history(name));
	}

	@Operation(summary = "Partially updates an existing plan")
	@PatchMapping(value = "/update/{name}")
	public ResponseEntity<PlansView> partialUpdate(final WebRequest request,
												 @PathVariable("name") @Parameter(description = "The name of the plan to update") final String name,
												 @Valid @RequestBody final EditPlansRequest resource) {

		final String ifMatchValue = request.getHeader("If-Match");
		if (ifMatchValue == null || ifMatchValue.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You must issue a conditional PATCH using 'if-match'");
		}



		final var plans = service.partialUpdate(name, resource, getVersionFromIfMatchHeader(ifMatchValue));
		logger.info("Partial update on plan: name={}, fields updated={}", Utils.sanitize(name),resource);
		return ResponseEntity.ok().eTag(Long.toString(plans.getVersion())).body(plansViewMapper.toPlansView(plans));
	}


	@Operation(summary = "Partially updates money of an existing plan")
	@PatchMapping(value = "/updateMoney/{name}")
	public ResponseEntity<PlansView> moneyUpdate(final WebRequest request,
												   @PathVariable("name") @Parameter(description = "The name of the plan to update") final String name,
												   @Valid @RequestBody final EditPlanMoneyRequest resource) {

		final String ifMatchValue = request.getHeader("If-Match");
		if (ifMatchValue == null || ifMatchValue.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You must issue a conditional PATCH using 'if-match'");
		}

		final Plans plans = service.moneyUpdate(name, resource, getVersionFromIfMatchHeader(ifMatchValue));
		logger.info("Updating pricing for plan: name={}, newMonthlyFee={}, newAnnualFee={}",
				Utils.sanitize(name), resource.getMonthlyFee(), resource.getAnnualFee());
		return ResponseEntity.ok().eTag(Long.toString(plans.getVersion())).body(plansViewMapper.toPlansView(plans));
	}



	@Operation(summary = "Deactivate a plan")
	@PatchMapping(value = "/deactivate/{name}")
	public ResponseEntity<PlansView> deactivate(final WebRequest request,
												@PathVariable("name") @Parameter(description = "The name of the plan to update") final String name) {
		final String ifMatchValue = request.getHeader("If-Match");
		if (ifMatchValue == null || ifMatchValue.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You must issue a conditional PATCH using 'if-match'");
		}

		final var plans = service.deactivate(name, getVersionFromIfMatchHeader(ifMatchValue));
		logger.warn("Deactivating plan: name={}", Utils.sanitize(name));
		return ResponseEntity.ok().eTag(Long.toString(plans.getVersion())).body(plansViewMapper.toPlansView(plans));
	}

	@Operation(summary = "Promote a plan")
	@PatchMapping(value = "/promote")
	public ResponseEntity<PromotionResultView> promote(final WebRequest request,
													   @RequestParam("name") @Parameter(description = "The name of the plan to promote") final String name) {
		final String ifMatchValue = request.getHeader("If-Match");
		logger.info("Promoting plan: name={}", Utils.sanitize(name));
		if (ifMatchValue == null || ifMatchValue.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You must issue a conditional PATCH using 'if-match'");
		}


		final var promotionResult = service.promote(name, getVersionFromIfMatchHeader(ifMatchValue));

		logger.info("Promotion result: newPromotedPlan={}, previousPromotedPlan={}",
				promotionResult.getNewPromotedPlan().getName(),
				promotionResult.getPreviousPromotedPlan() != null ? promotionResult.getPreviousPromotedPlan().getName() : "none");

		PromotionResultView promotionResultView = new PromotionResultView();
		promotionResultView.setNewPromotedPlan(plansViewMapper.toPlansView(promotionResult.getNewPromotedPlan()));
		promotionResultView.setPreviousPromotedPlan(plansViewMapper.toPlansView(promotionResult.getPreviousPromotedPlan()));

		return ResponseEntity.ok()
				.eTag(Long.toString(promotionResult.getNewPromotedPlan().getVersion()))
				.body(promotionResultView);
	}

	@Operation(summary = "Ceases an existing plan")
	@DeleteMapping
	public ResponseEntity<PlansView> cease(final WebRequest request,
											 @RequestParam("name")  final String name) {
		final String ifMatchValue = request.getHeader("If-Match");
		logger.warn("Ceasing plan: name={}", Utils.sanitize(name));
		if (ifMatchValue == null || ifMatchValue.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You must issue a conditional DELETE using 'if-match'");
		}
		final int count = service.cease(name, getVersionFromIfMatchHeader(ifMatchValue));

		if (count == 0) {
			logger.error("Ceasing plan '{}' failed, affected count={}", Utils.sanitize(name), count);
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} else if (count == 1) {
			logger.info("Plan '{}' successfully ceased", Utils.sanitize(name));
			return ResponseEntity.ok().build();
		} else {
			logger.error("Ceasing plan '{}' failed, affected count={}", Utils.sanitize(name), count);
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

}


