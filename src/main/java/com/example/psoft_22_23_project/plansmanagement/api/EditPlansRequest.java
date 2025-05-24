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

import lombok.*;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPlansRequest {
	@Size(min = 3, max = 25)
	@Pattern(regexp = "^[^<>'\"%;()&+/\\\\\\-|`{}\\[\\]$*!^=~#]*$")
	private String description;
	@Pattern(regexp = "(automatic|personalized)")
	private String musicSuggestion;
	@Size(min = 1, max = 10)
	@Pattern(regexp = "^[^<>'\"%;()&+/\\\\\\-|`{}\\[\\]$*!^=~#]*$")
	private String numberOfMinutes;
	@Min(0)
	private Integer maximumNumberOfUsers;

	@Min(0)
	private Integer musicCollection;
	@Nullable
	private Boolean active;
	@Nullable
	private Boolean promoted;



}
