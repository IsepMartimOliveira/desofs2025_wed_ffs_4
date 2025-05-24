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
import lombok.Data;


import org.springframework.lang.Nullable;

import jakarta.validation.constraints.*;

@Data
public class CreatePlanRequest {
	@Size(min = 1)
	@NotNull
	@Pattern(regexp = "^[^<>'\"%;()&+/\\\\\\-|`{}\\[\\]$*!^=~#]*$")
	private String name;
	@Size(min = 1)
	@NotNull
	@Pattern(regexp = "^[^<>'\"%;()&+/\\\\\\-|`{}\\[\\]$*!^=~#]*$")
	private String description;
	@Size(min = 1)
	@NotNull
	@Pattern(regexp = "^[^<>'\"%;()&+/\\\\\\-|`{}\\[\\]$*!^=~#]*$")
	private String numberOfMinutes;
	@Pattern(regexp = "(automatic|personalized)")
	@NotNull
	private String musicSuggestion;
	@Min(0)
	@NotNull
	private Integer maximumNumberOfUsers;
	@Min(0)
	@NotNull
	private Integer musicCollection;
	@Min(0)
	@NotNull
	private double annualFee;
	@Min(0)
	@NotNull
	private double monthlyFee;
	@Nullable
	private boolean active;
	@Nullable
	private boolean promoted;

}
