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
package com.example.psoft_22_23_project.usermanagement.services;

import com.example.psoft_22_23_project.filestoragemanagement.service.FileStorageService;
import com.example.psoft_22_23_project.usermanagement.api.*;
import com.example.psoft_22_23_project.usermanagement.model.Role;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.model.UserImage;
import com.example.psoft_22_23_project.usermanagement.repositories.UserImageRepository;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsService;
import com.example.psoft_22_23_project.utils.Utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserImageRepository userImageRepository;
	private final FileStorageService fileStorageService;
	private final SubscriptionsRepository subscriptionsRepository;
	private final SubscriptionsService subscriptionsService; // Assuming you have a SubscriptionsService

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}

	public User upload(MultipartFile file) {
		User user = getCurrentAuthenticatedUser();

		UserImage userImage = null;
		if (file != null && !file.isEmpty()) {
			if (user.getUserImage() != null) {
				try {
					fileStorageService.deleteFile(user.getUserImage().getFileName());
					userImageRepository.delete(user.getUserImage());
				} catch (Exception e) {
					logger.error("Could not delete previous user image: {}", user.getUserImage().getFileName(), e);
				}
			}

			final String fileName = fileStorageService.storeFile(user.getUsername(), file);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/api/user/photo")
					.toUriString();

			userImage = new UserImage(Utils.transformSpaces(user.getUsername()), fileName, fileDownloadUri,
					file.getContentType(), file.getSize());
			user.setUserImage(userImage);
			userImageRepository.save(userImage);
			userRepository.save(user);
		}
		return user;
	}

	public Resource seeImage() {
		User user = getCurrentAuthenticatedUser();
		if (user.getUserImage() == null || user.getUserImage().getFileName() == null) {
			throw new UsernameNotFoundException("User does not have an image or image file name is missing.");
		}
		return fileStorageService.loadFileAsResource(user.getUserImage().getFileName());
	}

	public User createUser(CreateUserRequest request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		User user = new User(
				request.getUsername(),
				passwordEncoder.encode(request.getPassword()),
				request.getEmail(),
				request.getPhoneNumber(),
				request.getAge());

		if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
			user.setLocation(request.getLocation());
		} else if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
			String location = request.getCity();
			if (request.getCountry() != null && !request.getCountry().trim().isEmpty()) {
				location += ", " + request.getCountry();
			}
			user.setLocation(location);
		}

		user.addAuthority(new Role(Role.Subscriber));
		return userRepository.save(user);
	}

	@Transactional
	public PersonalDataExportDTO exportPersonalData() {
		User user = getCurrentAuthenticatedUser();

		PersonalDataExportDTO.UserDataDTO userDataDTO = new PersonalDataExportDTO.UserDataDTO();
		userDataDTO.setUsername(user.getUsername());
		userDataDTO.setEmail(user.getEmail());
		userDataDTO.setPhoneNumber(user.getPhoneNumber());
		userDataDTO.setAge(user.getAge());
		userDataDTO.setLocation(user.getLocation());

		if (user.getUserImage() != null) {
			PersonalDataExportDTO.UserImageDTO imageDTO = new PersonalDataExportDTO.UserImageDTO();
			imageDTO.setFileName(user.getUserImage().getFileName());
			imageDTO.setDownloadUrl(user.getUserImage().getFileDownloadUri());
			imageDTO.setContentType(user.getUserImage().getContentType());
			imageDTO.setFileSize(user.getUserImage().getFileSize());
			userDataDTO.setProfileImage(imageDTO);
		}

		PersonalDataExportDTO.ExportMetadataDTO metadataDTO = new PersonalDataExportDTO.ExportMetadataDTO();
		metadataDTO.setDataRetentionPolicy("Personal data retention as per privacy policy.");
		metadataDTO.setAuditNote("Subscription and device data preserved for business auditing.");

		return new PersonalDataExportDTO(userDataDTO, metadataDTO);
	}

	@Transactional
	public void deletePersonalData(PersonalDataDeletionRequest request) {
		User user = getCurrentAuthenticatedUser();

		if (!passwordEncoder.matches(request.getConfirmationPassword(), user.getPassword())) {
			throw new AccessDeniedException("Invalid password confirmation.");
		}

		// Anonymize or nullify user data fields
		user.setEmail("deleted_" + user.getId() + "@anonymous.local");
		user.setPhoneNumber(0); 
		user.setAge(0);
		user.setLocation(null);

		// Delete user image if it exists
		if (user.getUserImage() != null) {
			try {
				fileStorageService.deleteFile(user.getUserImage().getFileName());
				UserImage imageToDelete = user.getUserImage();
				user.setUserImage(null); // Break the association
				userImageRepository.delete(imageToDelete); // Delete the image record
				logger.info("User image {} deleted for user ID: {}", imageToDelete.getFileName(), user.getId());
			} catch (Exception e) {
				logger.error("Could not delete user image file: {} for user ID: {}. Error: {}",
						user.getUserImage() != null ? user.getUserImage().getFileName() : "unknown",
						user.getId(),
						e.getMessage());
			}
		}
		

		user.setPersonalDataDeleted(true);
		user.setPersonalDataDeletionDate(LocalDateTime.now());
		user.setEnabled(false);

		userRepository.save(user);

		Optional<Subscriptions> optionalSubscription = subscriptionsRepository.findByUser(user);
		if (optionalSubscription.isPresent()) {
			Subscriptions sub = optionalSubscription.get();
			if (sub.getActiveStatus().isActive()) {
				try {
					subscriptionsService.cancelSubscription(sub.getVersion());
					logger.info("Subscription {} for user {} has been deactivated.", sub.getId(), user.getId());
				} catch (Exception e) {
					logger.error("Failed to deactivate subscription {} for user {}", sub.getId(), user.getId(), e);
				}
			}
		}
		logger.info("Personal data deleted for user ID: {}", user.getId());
	}

	private User getCurrentAuthenticatedUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		int commaIndex = username.indexOf(",");

		String newString;
		if (commaIndex != -1) {
			newString = username.substring(0, commaIndex);
		} else {
			newString = username;
		}

		return userRepository.findById(Long.valueOf(newString))
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID " + newString));
	}
}
