package com.example.psoft_22_23_project.devicemanagement.model;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import lombok.Getter;
import org.hibernate.StaleObjectStateException;

import jakarta.persistence.*;


@Entity
@Getter
@Table(name = "Device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Embedded
    private MacAddress macAddress;

    @Embedded
    private Name name;

    @Embedded
    private Description description;

    @ManyToOne(fetch = FetchType.EAGER)
    private Subscriptions subscription;

    @OneToOne(fetch = FetchType.EAGER)
    private DeviceImage deviceImage;

    protected Device() {
    }

    public Device(MacAddress macAddress, Name name, Description description, Subscriptions subscription) {
        this.macAddress = macAddress;
        this.name = name;
        this.description = description;
        this.subscription = subscription;
    }

    public void setDeviceImage(DeviceImage deviceImage) {
        this.deviceImage = deviceImage;
    }

    public void update(final long desiredVersion, String newName, String newDescription, DeviceImage newImage) {

        // check current version
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.id);
        }

        if (newName != null) {
            this.name.setName(newName);
        }
        if (newDescription != null) {
            this.description.setDescription(newDescription);
        }
        if (newImage != null) {
            setDeviceImage(newImage);
        }
    }

}
