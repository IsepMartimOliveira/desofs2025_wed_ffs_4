package com.example.psoft_22_23_project.devicemanagement.services;

import com.example.psoft_22_23_project.devicemanagement.api.CreateDeviceRequest;
import com.example.psoft_22_23_project.devicemanagement.api.EditDeviceRequest;
import com.example.psoft_22_23_project.devicemanagement.model.Device;
import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceImageRepository;
import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.exceptions.NotFoundException;
import com.example.psoft_22_23_project.devicemanagement.model.DeviceImage;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.example.psoft_22_23_project.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final CreateDeviceMapper deviceMapper;
    private final DeviceRepository deviceRepository;
    private final DeviceImageRepository deviceImageRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UserRepository userRepository;


    @Override
    public List<Device> findAllDevicesByUser() {
        // Check if the user is authorized to access this list
        String userIdString = Utils.getAuthId();

        // Retrieve the user based on the extracted user ID
        User user = userRepository.findById(Long.valueOf(userIdString))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userIdString));

        // Get the subscription associated with the user
        Subscriptions subscription = subscriptionsRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found for user with id " + userIdString));

        // Retrieve all devices based on the subscription
        return deviceRepository.findAllBySubscription(subscription);
    }



    @Override
    public Device create(CreateDeviceRequest resource, DeviceImage imageResource) {
        // Check if the user is authorized to add a device to this subscription
        String userIdString = Utils.getAuthId();

        // Retrieve the user based on the extracted user ID
        User user = userRepository.findById(Long.valueOf(userIdString))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userIdString));

        // Get the subscription associated with the user
        Subscriptions subscription = subscriptionsRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found for user with id " + userIdString));

        // Check if the user has reached the device limit
        double deviceLimit = subscription.getPlan().getMaximumNumberOfUsers().getMaximumNumberOfUsers();
        int numDevices = deviceRepository.countBySubscription(subscription);
        if (numDevices >= deviceLimit) {
            throw new IllegalArgumentException("User has reached the device limit for this subscription");
        }

        // Check if the device with the given MAC address already exists
        if (deviceRepository.findByMacAddress_MacAddress(resource.getMacAddress()).isPresent()) {
            throw new IllegalArgumentException("Cannot create an object that already exists");
        }

        // Create the device using the device mapper and save it to the repository
        Device device = deviceMapper.create(subscription, resource);
        if (imageResource != null){
            device.setDeviceImage(imageResource);
            deviceImageRepository.save(imageResource);
        }
        return deviceRepository.save(device);
    }

    @Override
    public Device update(String macAddress, EditDeviceRequest resource, DeviceImage imageResource, long desiredVersion) {
        // first let's check if the object exists so we don't create a new object with
        // save
        final var device = deviceRepository.findByMacAddress_MacAddress(macAddress)
                .orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));

        // Check if the user is authorized to update this device
        String userIdString = Utils.getAuthId();
        if (!userIdString.equals(device.getSubscription().getUser().getId().toString())) {
            throw new AccessDeniedException("User not authorized to update this device");
        }

        // apply update
        device.update(desiredVersion, resource.getName(), resource.getDescription(), imageResource);

        if (imageResource != null){
            deviceImageRepository.save(imageResource);
        }
        return deviceRepository.save(device);

    }

    @Override
    @Transactional
    public int deleteDevice(String macAddress, final long desiredVersion) {

        // Retrieve the device by ID
        Device device = deviceRepository.findByMacAddress_MacAddress(macAddress)
                .orElseThrow(() -> new EntityNotFoundException("Device not found with macAddress " + macAddress));

        // Check if the user is authorized to delete this device
        String userIdString = Utils.getAuthId();
        if (!userIdString.equals(device.getSubscription().getUser().getId().toString())) {
            throw new AccessDeniedException("User not authorized to delete this device");
        }

        // Perform the deletion if the version matches
        return deviceRepository.deleteByIdIfMatches(device.getId(), desiredVersion);
    }


}
