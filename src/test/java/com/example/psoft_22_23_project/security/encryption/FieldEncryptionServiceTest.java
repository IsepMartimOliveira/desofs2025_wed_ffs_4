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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for the FieldEncryptionService to ensure proper PII encryption functionality.
 * 
 * These tests verify ASVS compliance for stored cryptography requirements:
 * - Encryption uses industry-standard algorithms (AES-256-GCM)
 * - Each encryption operation uses a unique IV
 * - Encryption provides authentication (GCM mode)
 * - Error handling doesn't leak sensitive information
 */
@ExtendWith(MockitoExtension.class)
class FieldEncryptionServiceTest {

    private FieldEncryptionService encryptionService;

    // Using a fixed, Base64-encoded 256-bit AES key for consistent testing
    private static final String TEST_ENCRYPTION_KEY = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8="; // Example key (bytes 0-31)
    private static final String TEST_ALGORITHM = "AES";
    private static final String TEST_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int TEST_IV_LENGTH = 12; // Default GCM IV length (96 bits)
    private static final int TEST_TAG_LENGTH = 128; // Default GCM tag length (128 bits)
    private static final int TEST_KEY_LENGTH = 256; // AES-256 key length

    @BeforeEach
    void setUp() {
        // Initialize FieldEncryptionService with fixed parameters for testing
        encryptionService = new FieldEncryptionService(
                TEST_ENCRYPTION_KEY,
                TEST_ALGORITHM,
                TEST_TRANSFORMATION,
                TEST_IV_LENGTH,
                TEST_TAG_LENGTH,
                TEST_KEY_LENGTH
        );
    }

    @Test
    void encrypt_shouldReturnNull_whenInputIsNull() {
        String result = encryptionService.encrypt(null);
        assertNull(result);
    }

    @Test
    void encrypt_shouldReturnEmpty_whenInputIsEmpty() {
        String result = encryptionService.encrypt("");
        assertEquals("", result);
    }

    @Test
    void encrypt_shouldReturnDifferentResults_forSameInput() {
        String plaintext = "test@example.com";
        
        String encrypted1 = encryptionService.encrypt(plaintext);
        String encrypted2 = encryptionService.encrypt(plaintext);
        
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2, "Each encryption should use a unique IV");
    }

    // Combined test for various plaintext inputs
    @ParameterizedTest
    @ValueSource(strings = {
            "sensitive-phone-number-123456789",
            "user@domain.com with spaces & symbols!@#$%",
            "João da Silva - português 🇵🇹",
            "Another test string with numbers 123 and symbols *&^",
            "Short"
    })
    void encryptDecrypt_shouldReturnOriginalText_forVariousInputs(String plaintext) {

        String encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(plaintext, decrypted, "Decrypted text should match original plaintext: " + plaintext);
    }

    @Test
    void decrypt_shouldReturnNull_whenInputIsNull() {
        String result = encryptionService.decrypt(null);
        assertNull(result);
    }

    @Test
    void decrypt_shouldReturnEmpty_whenInputIsEmpty() {
        String result = encryptionService.decrypt("");
        assertEquals("", result);
    }

    @Test
    void decrypt_shouldThrowException_forInvalidData() {
        String invalidEncrypted = "invalid-base64-data";
        
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt(invalidEncrypted);
        });
    }

    @Test
    void decrypt_shouldThrowException_forTamperedData() {
        String plaintext = "test@example.com";
        String encrypted = encryptionService.encrypt(plaintext);
        
        // Tamper with the encrypted data
        String tamperedEncrypted = encrypted.substring(0, encrypted.length() - 5) + "XXXXX";
        
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt(tamperedEncrypted);
        }, "GCM mode should detect tampering and throw an exception");
    }

    @Test
    void encryptedData_shouldBeLongerThanPlaintext() {
        String plaintext = "short";
        String encrypted = encryptionService.encrypt(plaintext);
        
        assertTrue(encrypted.length() > plaintext.length(), 
                  "Encrypted data should be longer due to IV and authentication tag");
    }

    @Test
    void encryptedData_shouldBeBase64Encoded() {
        String plaintext = "test@example.com";
        String encrypted = encryptionService.encrypt(plaintext);
        
        // Should be valid base64
        assertDoesNotThrow(() -> {
            Base64.getDecoder().decode(encrypted);
        });
    }
}
