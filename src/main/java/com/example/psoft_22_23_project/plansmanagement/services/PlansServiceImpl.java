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
package com.example.psoft_22_23_project.plansmanagement.services;

import com.example.psoft_22_23_project.exceptions.NotFoundException;
import com.example.psoft_22_23_project.plansmanagement.api.CreatePlanRequest;
import com.example.psoft_22_23_project.plansmanagement.api.EditPlanMoneyRequest;
import com.example.psoft_22_23_project.plansmanagement.api.EditPlansRequest;
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.plansmanagement.repositories.PlansRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class PlansServiceImpl implements PlansService {

	private final PlansRepository repository;

	private final SubscriptionsRepository subscriptionsRepository;

	private final CreatePlansMapper createPlansMapper;

	@Override
	public Iterable<Plans> findAtive() {
		return repository.findByActive_Active(true);
	}


	@Override
	public List<FeeRevision> history(final String name) {
		Optional<Plans> plans = repository.findByName_Name(name);

		if (plans.isPresent()){
			Plans plans1 = plans.get();
			List<FeeRevision> list = plans1.getFeeRevisions();
			return list;

		}else throw new IllegalArgumentException("Plan with name "+name+" does not exist");

	}

	@Override
	public Plans create( final CreatePlanRequest resource) {
		Optional<Plans> plans = repository.findByName_Name(resource.getName());
		if (plans.isPresent()) {
			throw new IllegalArgumentException("Plan with name "+resource.getName()+" already exists!");
		}

		Plans obj = createPlansMapper.create(resource);
		return repository.save(obj);

	}

	@Override
	public Plans partialUpdate(final String name, final EditPlansRequest resource, final long desiredVersion) {
		final var plans = repository.findByName_Name(name)
				.orElseThrow(() -> new IllegalArgumentException("Plan with name "+name+" doesn't exists!"));

		plans.updateData(desiredVersion, resource.getDescription(),
				resource.getMaximumNumberOfUsers(),resource.getNumberOfMinutes(),
				resource.getMusicCollection(),resource.getMusicSuggestion(),resource.getActive(),resource.getPromoted());

		return repository.save(plans);
	}

	@Override
	public Plans moneyUpdate(final String name, final EditPlanMoneyRequest resource, final long desiredVersion) {
		final var plans = repository.findByName_Name(name)
				.orElseThrow(() -> new IllegalArgumentException("Plan with name "+name+" doesn't exists!"));

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		int commaIndex = username.indexOf(",");

		String newString;
		if (commaIndex != -1) {
			newString = username.substring(commaIndex + 1); // Get the substring starting from the character after the comma
		} else {
			newString = username;
		}


		plans.moneyData(desiredVersion, resource.getAnnualFee(), resource.getMonthlyFee(),newString);

		return repository.save(plans);
	}

	@Override
	public Plans deactivate(final String name, final long desiredVersion) {
		final Plans plans = repository.findByName_Name(name)
				.orElseThrow(() -> new IllegalArgumentException("Plan with name "+name+" doesn't exists!"));

		if (!plans.getActive().getActive()){
			throw new IllegalArgumentException("Plan with name "+name+" is already deactivate");
		}
		plans.deactivate(desiredVersion);

		return repository.save(plans);
	}

	@Override
	public PromotionResult promote(final String name, final long desiredVersion) {
		// Find the plan
		final Plans plan = repository.findByName_Name(name)
				.orElseThrow(() -> new NotFoundException("Plan with name " + name + " doesn't exist!"));

		// Check if plan is inactive
		if (!plan.getActive().getActive()) {
			throw new IllegalArgumentException("Can't promote this plan, " + plan.getName().getName() + " plan must be active");
		}

		// Check if plan is already promoted
		if (plan.getPromoted().getPromoted()){
			throw new IllegalArgumentException("Can't promote this plan, " + plan.getName().getName() + " plan is already promoted");
		}

		PromotionResult result = new PromotionResult();

		// Check if another plan is already promoted
		final Optional<Plans> existingPlan = repository.findByPromoted_Promoted(true);
		existingPlan.ifPresent(existingPromotedPlan -> {
			existingPromotedPlan.getPromoted().setPromoted(false);
			result.setPreviousPromotedPlan(existingPromotedPlan);
		});

		// Update the plan and set it as promoted
		plan.promote(desiredVersion);
		result.setNewPromotedPlan(repository.save(plan));

		// Save the changes to the existing promoted plan if it exists
		existingPlan.ifPresent(repository::save);

		// returns object with two variables (new promoted plan, previous promoted plan)
		return result;
	}




	@Override
	@Transactional
	public int cease(String name, final long desiredVersion) {
		// Retrieve the plan by name
		Plans plans = repository.findByName_Name(name)
				.orElseThrow(() -> new EntityNotFoundException("Plan not found with name " + name));

		// Check if it's inactive and not promoted
		if (plans.getActive().getActive() || plans.getPromoted().getPromoted()) {
			throw new IllegalArgumentException("Can't delete this plan, it can't be active or promoted");
		}

		// Check if there are any subscriptions with non-null users for the specific plan
		if (subscriptionsRepository.existsByPlanAndUserNotNull(plans)) {
			throw new IllegalArgumentException("Can't delete this plan, it has active subscriptions");
		}

		// Perform the soft deletion if the version matches
		return repository.ceaseByPlan(plans, desiredVersion);
	}


}
