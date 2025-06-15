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
package com.example.psoft_22_23_project.filestoragemanagement.service;

import com.example.psoft_22_23_project.exceptions.NotFoundException;
import com.example.psoft_22_23_project.filestoragemanagement.sanitize.SanitizeImage;
import com.example.psoft_22_23_project.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * <p>
 * code based on
 * https://github.com/callicoder/spring-boot-file-upload-download-rest-api-example
 *
 *
 */
@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	@Autowired
	public FileStorageService(final FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(fileStorageLocation);
		} catch (final Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(final String prefix, final MultipartFile file) {
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		}

		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
			throw new IllegalArgumentException("File name is missing");
		}

		try (InputStream inputStream = file.getInputStream()) {
			if (!SanitizeImage.isValidImage(file.getOriginalFilename(), inputStream)) {
				throw new FileStorageException("Invalid image file!");
			}
		} catch (IOException ex) {
			throw new FileStorageException("Error while validating the file", ex);
		}

		final String fileName = Utils.transformSpaces(prefix) + "_" + determineFileName(file);

		try {
			final Path targetLocation = fileStorageLocation.resolve(fileName);

			if (!targetLocation.normalize().startsWith(fileStorageLocation)) {
				throw new FileStorageException("Invalid file path detected");
			}

			Path tempFile = Files.createTempFile(fileStorageLocation, "upload_", ".tmp");
			try {
				Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
				Files.move(tempFile, targetLocation, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				// Clean up temp file if something goes wrong
				try {
					Files.deleteIfExists(tempFile);
				} catch (IOException cleanupEx) {
					logger.warn("Failed to clean up temp file: {}", tempFile, cleanupEx);
				}
				throw e;
			}

			logger.info("Successfully stored file: {}", fileName);
			return fileName;
		} catch (final IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	private String determineFileName(final MultipartFile file) {
		// Use the secure extension method from SanitizeImage
		String extension = SanitizeImage.getExtension(file.getOriginalFilename()).orElse("");

		if (extension.isEmpty()) {
			throw new FileStorageException("Could not determine file extension");
		}

		return UUID.randomUUID().toString() + "." + extension;
	}

	public Resource loadFileAsResource(final String fileName) {
		try {
			final Path filePath = fileStorageLocation.resolve(fileName).normalize();

			// Security check: ensure the resolved path is within our storage directory
			if (!filePath.startsWith(fileStorageLocation)) {
				throw new NotFoundException("Access denied for file: " + fileName);
			}

			final Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			}
			throw new NotFoundException("File not found " + fileName);
		} catch (final MalformedURLException ex) {
			throw new NotFoundException("File not found " + fileName, ex);
		}
	}

	public boolean deleteFile(final String fileName) {
		try {
			Path filePath = fileStorageLocation.resolve(fileName).normalize();

			if (!filePath.startsWith(fileStorageLocation)) {
				logger.warn("Attempted to delete file outside storage directory: {}", fileName);
				return false;
			}

			boolean deleted = Files.deleteIfExists(filePath);
			if (deleted) {
				logger.info("Successfully deleted file: {}", fileName);
			} else {
				logger.warn("File not found for deletion: {}", fileName);
			}
			return deleted;
		} catch (IOException ex) {
			logger.error("Could not delete file {}: {}", fileName, ex.getMessage());
			return false;
		}
	}
}