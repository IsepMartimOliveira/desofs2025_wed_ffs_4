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
package com.example.psoft_22_23_project.plansmanagement.repositories;


import com.example.psoft_22_23_project.plansmanagement.model.Plans;

import org.springframework.cache.annotation.CacheConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
@Repository
@Configuration
@CacheConfig(cacheNames = "plans")
public interface PlansRepository extends CrudRepository<Plans, Long> {

	Optional<Plans> findByName_Name(@NotNull String name);

	Optional<Plans> findByActive_ActiveAndName_Name(@NotNull boolean active, @NotNull String name_name);
	Iterable<Plans> findByActive_Active(@NotNull boolean active);
	Optional<Plans> findByPromoted_Promoted(@NotNull boolean promoted);
	@Modifying
	@Query("UPDATE Plans p SET p.deleted = true WHERE p = :plan AND p.version = :desiredVersion")
	int ceaseByPlan(@Param("plan") Plans plan, @Param("desiredVersion") long desiredVersion);



}
