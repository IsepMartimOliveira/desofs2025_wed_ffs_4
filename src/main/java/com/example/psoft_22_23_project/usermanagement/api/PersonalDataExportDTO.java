package com.example.psoft_22_23_project.usermanagement.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class PersonalDataExportDTO {

    private LocalDateTime exportTimestamp;
    private UserDataDTO userData;
    private ExportMetadataDTO exportMetadata;

    public PersonalDataExportDTO(UserDataDTO userData, ExportMetadataDTO exportMetadata) {
        this.exportTimestamp = LocalDateTime.now();
        this.userData = userData;
        this.exportMetadata = exportMetadata;
    }

    @Getter
    @Setter
    public static class UserDataDTO {
        private String username;
        private String email;
        private Integer phoneNumber;
        private Integer age;
        private String location;
        private UserImageDTO profileImage;
    }

    @Getter
    @Setter
    public static class UserImageDTO {
        private String fileName;
        private String downloadUrl;
        private String contentType;
        private long fileSize;
    }

    @Getter
    @Setter
    public static class ExportMetadataDTO {
        private String dataRetentionPolicy;
        private String auditNote;
    }
}
