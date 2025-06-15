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
 * JPA AttributeConverter for automatically encrypting/decrypting integer fields.
 * 
 * This converter is specifically designed for sensitive integer data like
 * phone numbers and age information that need to be encrypted at rest.
 */
@Converter
@Component
public class EncryptedIntegerConverter implements AttributeConverter<Integer, String> {

    private static FieldEncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(FieldEncryptionService encryptionService) {
        EncryptedIntegerConverter.encryptionService = encryptionService;
    }

    /**
     * Converts an integer to encrypted string format for database storage.
     * 
     * @param attribute The integer value from the entity
     * @return Encrypted string to store in database, or null if input is null
     */
    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        if (encryptionService == null) {
            throw new IllegalStateException("FieldEncryptionService not initialized");
        }
        if (attribute == null) {
            return null;
        }
        return encryptionService.encrypt(attribute.toString());
    }    /**
     * Converts an encrypted string from the database to integer.
     * 
     * @param dbData The encrypted value from the database
     * @return Decrypted integer value, or null if input is null
     */
    @Override
    public Integer convertToEntityAttribute(String dbData) {
        if (encryptionService == null) {
            throw new IllegalStateException("FieldEncryptionService not initialized");
        }
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        
        String decrypted = encryptionService.decrypt(dbData);
        try {
            return Integer.valueOf(decrypted);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to convert decrypted data to integer: " + e.getMessage(), e);
        }
    }
}
