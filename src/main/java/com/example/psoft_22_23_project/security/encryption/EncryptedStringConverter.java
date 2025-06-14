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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter for automatically encrypting/decrypting string fields.
 * 
 * This converter is applied to entity fields that contain PII and need to be
 * encrypted at rest to comply with GDPR and ASVS requirements.
 * 
 * The converter automatically:
 * - Encrypts data when storing to database
 * - Decrypts data when reading from database
 * - Handles null values gracefully
 */
@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static FieldEncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(FieldEncryptionService encryptionService) {
        EncryptedStringConverter.encryptionService = encryptionService;
    }

    /**
     * Converts a plaintext string to encrypted format for database storage.
     * 
     * @param attribute The plaintext value from the entity
     * @return Encrypted value to store in database, or null if input is null
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (encryptionService == null) {
            throw new IllegalStateException("FieldEncryptionService not initialized");
        }
        return encryptionService.encrypt(attribute);
    }

    /**
     * Converts an encrypted string from the database to plaintext.
     * 
     * @param dbData The encrypted value from the database
     * @return Decrypted plaintext value, or null if input is null
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (encryptionService == null) {
            throw new IllegalStateException("FieldEncryptionService not initialized");
        }
        return encryptionService.decrypt(dbData);
    }
}
