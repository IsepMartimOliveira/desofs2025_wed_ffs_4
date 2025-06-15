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
package com.example.psoft_22_23_project.security.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * Service for encrypting and decrypting PII data fields at rest.
 * Uses AES-256-GCM with cryptographically secure random IVs.
 * 
 * This implementation follows ASVS requirements for:
 * - Industry-proven encryption algorithms (AES-256-GCM)
 * - Authenticated encryption (GCM mode provides authentication)
 * - Cryptographically secure random IV generation
 * - Proper key management separation
 */
@Service
public class FieldEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(FieldEncryptionService.class);

    private final String algorithm;
    private final String transformation;
    private final int ivLength;
    private final int tagLength;
    private final SecretKey encryptionKey;
    private final SecureRandom secureRandom;

    static {
        // Add BouncyCastle provider for additional cryptographic support
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public FieldEncryptionService(
            @Value("${app.encryption.key:}") String encryptionKeyString,
            @Value("${app.encryption.algorithm:AES}") String algorithm,
            @Value("${app.encryption.transformation:AES/GCM/NoPadding}") String transformation,
            @Value("${app.encryption.iv-length:12}") int ivLength,
            @Value("${app.encryption.tag-length:128}") int tagLength) {
        this.secureRandom = new SecureRandom();
        this.algorithm = algorithm;
        this.transformation = transformation;
        this.ivLength = ivLength;
        this.tagLength = tagLength;

        this.encryptionKey = new SecretKeySpec(
                Base64.getDecoder().decode(encryptionKeyString),
                this.algorithm);

        logger.info("FieldEncryptionService initialized with {} encryption and {} transformation", this.algorithm,
                this.transformation);
    }

    /**
     * Encrypts plaintext data using AES-256-GCM with a random IV.
     * 
     * @param plaintext The data to encrypt (null values are returned as null)
     * @return Base64-encoded encrypted data with prepended IV, or null if input is
     *         null
     * @throws RuntimeException if encryption fails
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            byte[] iv = new byte[ivLength];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(transformation);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, iv);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmParameterSpec);

            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(ivLength + encryptedData.length);
            buffer.put(iv);
            buffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    /**
     * Decrypts data that was encrypted with the encrypt method.
     * 
     * @param encryptedData Base64-encoded encrypted data with prepended IV
     * @return Decrypted plaintext, or null if input is null
     * @throws RuntimeException if decryption fails
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedData);

            if (encryptedWithIv.length < ivLength) {
                throw new IllegalArgumentException("Encrypted data is too short to contain a valid IV");
            }

            ByteBuffer buffer = ByteBuffer.wrap(encryptedWithIv);
            byte[] iv = new byte[ivLength];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher = Cipher.getInstance(transformation);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, iv);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, gcmParameterSpec);

            byte[] decryptedData = cipher.doFinal(encrypted);
            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }
}
