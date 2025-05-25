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
package com.example.psoft_22_23_project.usermanagement.api;

import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.UserService;
import com.example.psoft_22_23_project.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	private final UserViewMapper userViewMapper;



	@Operation(summary = "Upload Image")
	@PatchMapping("photo")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<UserView> upload(
			@RequestParam(name = "file", required = false) final MultipartFile file)
			throws URISyntaxException {

		User user = userService.upload(file);
		logger.info("Uploading profile image: filename={} for userId={}", Utils.sanitize(file.getOriginalFilename()), user.getId());
		return ResponseEntity.ok().body(userViewMapper.toUserView(user));

	}

	@Operation(summary = "Downloads a photo of a device")
	@GetMapping("photo")
	public ResponseEntity<Resource> downloadFile(final HttpServletRequest request) {


		Resource resource = userService.seeImage();

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (final IOException ex) {
			logger.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@Operation(summary = "Create a user account")
	@PostMapping("account")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<UserView> createUser(@Valid @RequestBody CreateUserRequest user) {

		User createdUser = userService.createUser(user);
		logger.info("Account created for userId={}, email={}", Utils.sanitize(createdUser.getId().toString()));
		return ResponseEntity.status(HttpStatus.CREATED).body(userViewMapper.toUserView(createdUser));
	}


}
