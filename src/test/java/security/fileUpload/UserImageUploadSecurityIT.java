package security.fileUpload;

import com.example.psoft_22_23_project.filestoragemanagement.service.FileStorageService;
import com.example.psoft_22_23_project.usermanagement.api.UserController;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserImageRepository;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.example.psoft_22_23_project.usermanagement.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.example.psoft_22_23_project.Main.class)
@Transactional
class UserImageUploadSecurityIT {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserImageRepository userImageRepository;

    @TempDir
    Path tempDir;

    private User testUser;
    private String baseUrl;
    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() throws IOException {
        baseUrl = "http://localhost:" + port + "/api/user";
        testUser = new User("testuser@mail.com", "testuser", "testuser@mail.com", 123456789, 25);
        userRepository.save(testUser);
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        if (testUser != null) {
            try {
                Optional<User> currentUser = userRepository.findById(testUser.getId());
                if (currentUser.isPresent() && currentUser.get().getUserImage() != null) {
                    fileStorageService.deleteFile(currentUser.get().getUserImage().getFileName());
                }
            } catch (Exception e) {
                // Ignore cleanup errors in tests
            }
        }
    }

    // ============ EXISTING TESTS (keeping all your original tests) ============

    @Test
    @DisplayName("IT: Valid JPEG upload should succeed through service call")
    void uploadValidJpegImage_serviceCall_shouldSucceed() throws IOException {
        setupSecurityContext();

        try {
            byte[] jpegBytes = createRealImageBytes("jpg");
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-image.jpg",
                    "image/jpeg",
                    jpegBytes
            );

            User updatedUser = userService.upload(file);

            assertNotNull(updatedUser.getUserImage());
            assertEquals("image/jpeg", updatedUser.getUserImage().getContentType());
            assertTrue(updatedUser.getUserImage().getFileName().contains("testuser@mail.com"));

            Resource storedFile = fileStorageService.loadFileAsResource(updatedUser.getUserImage().getFileName());
            assertNotNull(storedFile);
            assertTrue(storedFile.exists());

            Optional<User> dbUser = userRepository.findById(updatedUser.getId());
            assertTrue(dbUser.isPresent());
            assertNotNull(dbUser.get().getUserImage());
        } finally {
            SecurityContextHolder.clearContext();
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    @DisplayName("IT: Invalid file extension should fail")
    void uploadInvalidExtension_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.exe",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: File with no extension should fail")
    void uploadFileWithNoExtension_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: File with multiple dots should fail")
    void uploadFileWithMultipleDots_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.image.jpg",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Invalid filename characters should fail")
    void uploadInvalidFilenameCharacters_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test@image!.jpg",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Extension too short should fail")
    void uploadExtensionTooShort_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jp",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Extension too long should fail")
    void uploadExtensionTooLong_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpegx",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Extension-MIME type mismatch should fail")
    void uploadExtensionMimeTypeMismatch_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] pngBytes = createRealImageBytes("png");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/png",
                pngBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Non-image MIME type should fail")
    void uploadNonImageMimeType_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] textBytes = "This is not an image".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.jpg",
                "text/plain",
                textBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Empty file should fail")
    void uploadEmptyFile_shouldFail() throws IOException {
        setupSecurityContext();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(Exception.class, () -> userController.upload(file));
    }

    @Test
    @DisplayName("IT: Null filename should fail")
    void uploadNullFilename_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Empty filename should fail")
    void uploadEmptyFilename_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Unsupported image format should fail")
    void uploadUnsupportedImageFormat_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] svgBytes = "<svg xmlns='http://www.w3.org/2000/svg'><rect width='100' height='100'/></svg>".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.svg",
                "image/svg+xml",
                svgBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("IT: Filename with spaces should fail")
    void uploadFilenameWithSpaces_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test image.jpg",
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    // ============ SANITIZED SECURITY TESTS ============

    @Test
    @DisplayName("SECURITY: Path traversal with relative paths should fail")
    void uploadPathTraversalRelative_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "..\\..\\..\\config\\data.jpg", // Generic path traversal
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("SECURITY: Path traversal with forward slashes should fail")
    void uploadPathTraversalForward_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../../config/data.jpg", // Generic path traversal
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("SECURITY: Absolute path attempts should fail")
    void uploadAbsolutePath_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] maliciousPaths = {
                "/config/data.jpg",     // Unix absolute path
                "C:\\data\\file.jpg"    // Windows absolute path (C: drive)
        };

        for (String path : maliciousPaths) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    path,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for path: " + path);
        }
    }

    @Test
    @DisplayName("SECURITY: URL-encoded traversal attempts should fail")
    void uploadUrlEncodedTraversal_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "%2e%2e%2f%2e%2e%2fconfig%2fdata.jpg", // URL encoded path
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("SECURITY: Null terminator injection should fail")
    void uploadNullTerminator_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test\0.script.jpg", // Null byte injection
                "image/jpeg",
                jpegBytes
        );

        assertThrows(Exception.class, () -> userService.upload(file));
    }

    @Test
    @DisplayName("SECURITY: Control characters should fail")
    void uploadControlCharacters_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] controlCharFiles = {
                "test\r\nimage.jpg",  // CRLF
                "test\timage.jpg",    // Tab
                "test\bimage.jpg"     // Backspace
        };

        for (String filename : controlCharFiles) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for control character filename");
        }
    }

    @Test
    @DisplayName("SECURITY: Command injection characters should fail")
    void uploadCommandChars_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] dangerousChars = {
                "test|image.jpg",     // Pipe
                "test&image.jpg",     // Ampersand
                "test;image.jpg",     // Semicolon
                "test`image.jpg",     // Backtick
                "test$image.jpg"      // Dollar sign
        };

        for (String filename : dangerousChars) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for dangerous character: " + filename);
        }
    }

    @Test
    @DisplayName("SECURITY: Wildcard characters should fail")
    void uploadWildcards_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] wildcardFiles = {
                "test*.jpg",    // Asterisk
                "test?.jpg"     // Question mark
        };

        for (String filename : wildcardFiles) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for wildcard: " + filename);
        }
    }

    @Test
    @DisplayName("SECURITY: Windows special characters should fail")
    void uploadWindowsSpecialChars_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] windowsChars = {
                "test:stream.jpg",    // Colon (ADS)
                "test<file.jpg",      // Less than
                "test>file.jpg",      // Greater than
                "test\"file.jpg"      // Quote
        };

        for (String filename : windowsChars) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for Windows special char: " + filename);
        }
    }

    @Test
    @DisplayName("SECURITY: Markup injection attempts should fail")
    void uploadMarkupInjection_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] markupFiles = {
                "test<tag>.jpg",         // HTML-like tag
                "test[bracket].jpg",     // Brackets
                "test{brace}.jpg"        // Braces
        };

        for (String filename : markupFiles) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for markup injection: " + filename);
        }
    }

    @Test
    @DisplayName("SECURITY: Filename length boundaries should be enforced")
    void uploadFilenameLengthBoundaries_shouldFailForInvalid() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");

        // Test too long filename
        String longFilename = "a".repeat(250) + ".jpg";
        MockMultipartFile longFile = new MockMultipartFile(
                "file",
                longFilename,
                "image/jpeg",
                jpegBytes
        );
        assertThrows(Exception.class, () -> userService.upload(longFile),
                "Should fail for filename too long");

        // Test too short filename
        MockMultipartFile shortFile = new MockMultipartFile(
                "file",
                "a.j",
                "image/jpeg",
                jpegBytes
        );
        assertThrows(Exception.class, () -> userService.upload(shortFile),
                "Should fail for filename too short");
    }

    @Test
    @DisplayName("SECURITY: Double extension attempts should fail")
    void uploadDoubleExtension_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] doubleExtensions = {
                "file.txt.jpg",      // Text file disguised
                "script.bat.jpg",    // Batch file disguised
                "code.js.jpg"        // JavaScript disguised
        };

        for (String filename : doubleExtensions) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for double extension: " + filename);
        }
    }

    @Test
    @DisplayName("SECURITY: Case sensitivity bypass attempts should fail")
    void uploadCaseSensitivityBypass_shouldFail() throws IOException {
        setupSecurityContext();

        byte[] jpegBytes = createRealImageBytes("jpg");
        String[] upperCaseExtensions = {
                "test-image.EXE",    // Executable
                "test-image.BAT",    // Batch
                "test-image.COM",    // Command
                "test-image.SCR"     // Screen saver
        };

        for (String filename : upperCaseExtensions) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            assertThrows(Exception.class, () -> userService.upload(file),
                    "Should fail for dangerous extension: " + filename);
        }
    }

    /*@Test
    @DisplayName("SECURITY: Valid edge case filenames should succeed")
    void uploadValidEdgeCaseFilenames_shouldSucceed() throws IOException {
        setupSecurityContext();

        String[] validFilenames = {
                "a1234.jpg",                    // Minimum valid length
                "test-image_123.png",           // Valid characters
                "12345.jpeg",                   // Numbers only
                "a".repeat(50) + ".jpg"         // Reasonable length
        };

        for (String filename : validFilenames) {
            byte[] jpegBytes = createRealImageBytes("jpg");
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    filename,
                    "image/jpeg",
                    jpegBytes
            );

            try {
                User result = userService.upload(file);
                assertNotNull(result, "Valid filename should succeed: " + filename);
                assertNotNull(result.getUserImage(), "User image should be created for: " + filename);

                fileStorageService.deleteFile(result.getUserImage().getFileName());
                userImageRepository.delete(result.getUserImage());
            } catch (Exception e) {
                fail("Valid filename should not fail: " + filename + " - Error: " + e.getMessage());
            }
        }
    }*/


    private void setupSecurityContext() {
        Long userId = testUser.getId();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_Subscriber"))
        );
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(port);
        request.setContextPath("");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private byte[] createRealImageBytes(String format) throws IOException {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
}