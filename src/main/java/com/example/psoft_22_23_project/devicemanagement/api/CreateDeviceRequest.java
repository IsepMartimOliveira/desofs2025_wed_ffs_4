package com.example.psoft_22_23_project.devicemanagement.api;

import com.example.psoft_22_23_project.devicemanagement.model.Description;
import com.example.psoft_22_23_project.devicemanagement.model.MacAddress;
import com.example.psoft_22_23_project.devicemanagement.model.Name;
import lombok.*;

@Data
@RequiredArgsConstructor
@Getter
@Setter
public class CreateDeviceRequest {

    private String macAddress;

    private String name;

    private String description;

    public CreateDeviceRequest(String testMacAddress, String newDevice, String description) {
        this.macAddress = testMacAddress;
        this.name = newDevice;
        this.description = description;
    }
}
