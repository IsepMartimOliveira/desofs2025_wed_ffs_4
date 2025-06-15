package com.example.psoft_22_23_project.subscriptionsmanagement.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsService;
import com.example.psoft_22_23_project.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Subscriptions", description = "Endpoints for managing subscriptions")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {


    private final SubscriptionsService service;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionsController.class);


    private final SubscriptionsViewMapper subscriptionsViewMapper;

    private final PlansDetailsViewMapper plansDetailsViewMapper;

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Gets all subscriptions")
    @GetMapping(value = "/list")
    public Iterable<SubscriptionsView> findAll() {
        return subscriptionsViewMapper.toSubscriptionsView(service.findAll());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubscriptionsView> create(@Valid @RequestBody final CreateSubscriptionsRequest resource) {

        logger.info("Request to create subscription for name={}, paymentType={}",
                Utils.sanitize(resource.getName()), Utils.sanitize(resource.getPaymentType()));

        final var subscriptions = service.create(resource);

        final var newSubscriptionUri =
                ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(subscriptions.getPlan().getName().getName()).build().toUri();

        logger.info("Subscription created with ID={} for userId={}",
                subscriptions.getId(), subscriptions.getUser().getId());
        return ResponseEntity.created(newSubscriptionUri)
                .eTag(Long.toString(subscriptions.getVersion()))
                .body(subscriptionsViewMapper.toSubscriptionView(subscriptions));
    }

    @Operation(summary = "Cancel a subscription")
    @PatchMapping
    public ResponseEntity<SubscriptionsView> cancelSubscription(final WebRequest request) {
        final String ifMatchValue = request.getHeader("If-Match");
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        Long version = getVersionFromIfMatchHeader(ifMatchValue);
        logger.info("Attempting to cancel subscription with version={}", version);



        final var subscriptions = service.cancelSubscription(getVersionFromIfMatchHeader(ifMatchValue));
        logger.info("Subscription with ID={} successfully cancelled", subscriptions.getId());
        return ResponseEntity.ok().eTag(Long.toString(subscriptions.getVersion())).body(subscriptionsViewMapper.toSubscriptionView(subscriptions));
    }


    @Operation(summary = "Give detailed information about a plan")
    @GetMapping
    public ResponseEntity<PlansDetailsView> planDetails() {

        final PlansDetails plan = service.planDetails();

        return ResponseEntity.ok(plansDetailsViewMapper.toPlansDetailsView(plan));
    }

    @Operation(summary = "Renew annual subscription")
    @PatchMapping(value = "/renew")
    public ResponseEntity<SubscriptionsView> renewAnualSubscription(final WebRequest request) {
        final String ifMatchValue = request.getHeader("If-Match");
        logger.info("Attempting to renew annual subscription with version={}", Utils.sanitize(ifMatchValue));
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }

        final var subscriptions = service.renewAnualSubscription(getVersionFromIfMatchHeader(ifMatchValue));
        logger.info("Annual subscription with ID={} successfully renewed", subscriptions.getId());
        return ResponseEntity.ok().eTag(Long.toString(subscriptions.getVersion())).body(subscriptionsViewMapper.toSubscriptionView(subscriptions));
    }


    @Operation(summary = "Change plan of my subscription")
    @PatchMapping(value = "/change/{name}")
    public ResponseEntity<SubscriptionsView> changePlan(final WebRequest request, @Valid @PathVariable final String name) {
        final String ifMatchValue = request.getHeader("If-Match");
        logger.info("Changing subscription plan to '{}' with version={}", Utils.sanitize(name), Utils.sanitize(ifMatchValue));
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }

        final var subscriptions = service.changePlan(getVersionFromIfMatchHeader(ifMatchValue), name);
        logger.info("Subscription ID={} successfully changed to plan '{}'", subscriptions.getId(), Utils.sanitize(name));
        return ResponseEntity.ok().eTag(Long.toString(subscriptions.getVersion())).body(subscriptionsViewMapper.toSubscriptionView(subscriptions));
    }

    @Operation(summary = "Change plan of my subscription")
    @PatchMapping(value = "/change/{actualPlan}/{newPlan}")
    public void migrateAllToPlan(final WebRequest request,@Valid @PathVariable final String actualPlan, @Valid @PathVariable final String newPlan) {
        final String ifMatchValue = request.getHeader("If-Match");
        logger.warn("Migration requested from plan '{}' to plan '{}', version={}", Utils.sanitize(actualPlan), Utils.sanitize(newPlan), Utils.sanitize(ifMatchValue));
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        logger.info("Migration from plan '{}' to '{}' completed", Utils.sanitize(actualPlan), Utils.sanitize(newPlan));
        service.migrateAllToPlan(getVersionFromIfMatchHeader(ifMatchValue), actualPlan, newPlan);
    }

}
