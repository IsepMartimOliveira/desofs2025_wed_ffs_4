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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for JPA converters that handle automatic encryption/decryption of entity fields.
 */
@ExtendWith(MockitoExtension.class)
class EncryptionConvertersTest {

    @Mock
    private FieldEncryptionService encryptionService;

    private EncryptedStringConverter stringConverter;
    private EncryptedIntegerConverter integerConverter;

    @BeforeEach
    void setUp() {
        stringConverter = new EncryptedStringConverter();
        stringConverter.setEncryptionService(encryptionService);
        
        integerConverter = new EncryptedIntegerConverter();
        integerConverter.setEncryptionService(encryptionService);
    }

    @Test
    void stringConverter_shouldEncryptWhenConvertingToDatabaseColumn() {
        String plaintext = "test@example.com";
        String encrypted = "encrypted-data";
        
        when(encryptionService.encrypt(plaintext)).thenReturn(encrypted);
        
        String result = stringConverter.convertToDatabaseColumn(plaintext);
        
        assertEquals(encrypted, result);
        verify(encryptionService).encrypt(plaintext);
    }

    @Test
    void stringConverter_shouldDecryptWhenConvertingToEntityAttribute() {
        String encrypted = "encrypted-data";
        String plaintext = "test@example.com";
        
        when(encryptionService.decrypt(encrypted)).thenReturn(plaintext);
        
        String result = stringConverter.convertToEntityAttribute(encrypted);
        
        assertEquals(plaintext, result);
        verify(encryptionService).decrypt(encrypted);
    }

    @Test
    void stringConverter_shouldHandleNullValues() {
        when(encryptionService.encrypt(null)).thenReturn(null);
        when(encryptionService.decrypt(null)).thenReturn(null);
        
        assertNull(stringConverter.convertToDatabaseColumn(null));
        assertNull(stringConverter.convertToEntityAttribute(null));
    }

    @Test
    void integerConverter_shouldEncryptWhenConvertingToDatabaseColumn() {
        Integer phoneNumber = 123456789;
        String encrypted = "encrypted-phone";
        
        when(encryptionService.encrypt("123456789")).thenReturn(encrypted);
        
        String result = integerConverter.convertToDatabaseColumn(phoneNumber);
        
        assertEquals(encrypted, result);
        verify(encryptionService).encrypt("123456789");
    }

    @Test
    void integerConverter_shouldDecryptWhenConvertingToEntityAttribute() {
        String encrypted = "encrypted-phone";
        String decrypted = "123456789";
        
        when(encryptionService.decrypt(encrypted)).thenReturn(decrypted);
        
        Integer result = integerConverter.convertToEntityAttribute(encrypted);
        
        assertEquals(Integer.valueOf(123456789), result);
        verify(encryptionService).decrypt(encrypted);
    }

    @Test
    void integerConverter_shouldHandleNullValues() {
        assertNull(integerConverter.convertToDatabaseColumn(null));
        assertNull(integerConverter.convertToEntityAttribute(null));
        assertNull(integerConverter.convertToEntityAttribute(""));
    }

    @Test
    void integerConverter_shouldThrowExceptionForInvalidDecryptedData() {
        String encrypted = "encrypted-invalid";
        String invalidDecrypted = "not-a-number";
        
        when(encryptionService.decrypt(encrypted)).thenReturn(invalidDecrypted);
        
        assertThrows(RuntimeException.class, () -> {
            integerConverter.convertToEntityAttribute(encrypted);
        });
    }
}
