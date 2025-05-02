package com.example.psoft_22_23_project.subscriptionsmanagement.services;

import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.plansmanagement.repositories.PlansRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;

import com.sun.jdi.request.DuplicateRequestException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final SubscriptionsRepository repository;

    private final PlansRepository plansRepository;

    private final DeviceRepository deviceRepository;

    private final UserRepository userRepository;
    private final CreateSubscriptionsMapper createSubscriptionsMapper;


    @Override
    public Iterable<Subscriptions> findAll() {
        return repository.findAll();
    }

    @Override
    public Subscriptions create(final CreateSubscriptionsRequest resource) {

        Plans plan = plansRepository.findByActive_ActiveAndName_Name(true, resource.getName())
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with name " + resource.getName()));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }


        User user = userRepository.findById(Long.valueOf(newString))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        Optional <Subscriptions> existingSubscription = repository.findByActiveStatus_ActiveAndUser(true, user);

        if (existingSubscription.isPresent()){
            if (existingSubscription.get().getActiveStatus().isActive()) {
                throw new IllegalArgumentException("You need to let your active subscription end in order to subscribe");
            }
/*
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(existingSubscription.get().getEndDate().getEndDate(), formatter);

            if (LocalDate.now().isBefore(dateTime)) {
                throw new IllegalArgumentException("You need to let your active subscription end in order to subscribe");
            }*/
        }



        /*
        if (existingSubscription.isPresent()) {
            throw new IllegalArgumentException("You need to cancel your active subscription in order to subscribe");
        }*/


        // construct a new object based on data received by the service
        Subscriptions obj = createSubscriptionsMapper.create(user, plan, resource);


        return repository.save(obj);
    }

    /*

    @Override
    public void delete(final Long id) {
        // Check if subscription exists
        Subscriptions subscription = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with ID " + id));

        // Check if the current user is authorized to delete the subscription
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }

        User user = subscription.getUser();
        boolean userAuthorized = Long.valueOf(newString).equals(user.getId());
        if (!userAuthorized) {
            throw new AccessDeniedException("User not allowed to delete the subscription");
        }

        // Delete the subscription
        repository.delete(subscription);
    }

*/

    @Override
    public Subscriptions cancelSubscription(final long desiredVersion){

        // Check if the current user is authorized to cancel the subscription
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }

        User user = userRepository.findById(Long.valueOf(newString)).orElseThrow(() -> new EntityNotFoundException("User not found with ID " + newString));

        // Check if subscription exists
        Subscriptions subscription = repository.findByActiveStatus_ActiveAndUser(true, user)
                .orElseThrow(() -> new EntityNotFoundException("No subscriptions associated with this user"));


        /*
        if (!subscription.getActiveStatus().isActive()){
            throw new EntityNotFoundException("Subscription already canceled");
        }

        boolean userAuthorized = Long.valueOf(newString).equals(user.getId());
        if (!userAuthorized) {
            throw new NotFoundException("User not allowed to cancel the subscription");
        }
*/

        subscription.deactivate(desiredVersion);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(subscription.getStartDate().getStartDate(), formatter);


        if(Objects.equals(subscription.getPaymentType().getPaymentType(), "monthly")){
            if (startDate.getMonthValue() == LocalDate.now().getMonthValue()){
                if (startDate.getYear() != LocalDate.now().getYear()) {
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths(1).plusYears(LocalDate.now().getYear() - startDate.getYear())));
                }else{
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths(1)));
                }

            }else if (startDate.getDayOfMonth() >= LocalDate.now().getDayOfMonth()){
                if (startDate.getYear() != LocalDate.now().getYear()){
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths(1).plusYears(LocalDate.now().getYear() - startDate.getYear())));
                }else {
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths(1)));
                }

            } else {
                if (startDate.getYear() != LocalDate.now().getYear()) {
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths((LocalDate.now().getMonthValue() - startDate.getMonthValue())+1).plusYears(LocalDate.now().getYear() - startDate.getYear())));
                } else {
                    subscription.getEndDate().setEndDate(String.valueOf(startDate.plusMonths((LocalDate.now().getMonthValue() - startDate.getMonthValue())+1)));
                }
            }
        } /*else {
            if (startDate.getYear() == LocalDate.now().getYear()) {
                subscription.getEndDate().setEndDate(String.valueOf(startDate.plusYears(1)));
            } else if (startDate.getMonthValue() == LocalDate.now().getMonthValue() && startDate.getDayOfMonth() >= LocalDate.now().getDayOfMonth()) {
                subscription.getEndDate().setEndDate(String.valueOf(startDate.plusYears(1)));
            } else {
                subscription.getEndDate().setEndDate(String.valueOf(startDate.plusYears((LocalDate.now().getYear() - startDate.getYear()) + 1)));
            }
        }*/
        return repository.save(subscription);
    }

    @Override
    public PlansDetails planDetails(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }

        User user = userRepository.findById(Long.valueOf(newString)).orElseThrow(() -> new EntityNotFoundException("You need to login in order to check the plan details"));


        Subscriptions subscription = repository.findByActiveStatus_ActiveAndUser(true, user)
                .orElseThrow(() -> new EntityNotFoundException("No subscriptions associated with this user"));

        /*
        boolean userAuthorized = Long.valueOf(newString).equals(user.getId());
        if (!userAuthorized) {
            throw new NotFoundException("User not allowed to view this plan details");
        }
        */


        return new PlansDetails(subscription.getPlan());
    }



    @Override
    public Subscriptions renewAnualSubscription(final long desiredVersion){

        // Check if the current user is authorized to cancel the subscription
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }

        User user = userRepository.findById(Long.valueOf(newString)).orElseThrow(() -> new EntityNotFoundException("User not found with ID " + newString));

        // Check if subscription exists
        Subscriptions subscription = repository.findByActiveStatus_ActiveAndUser(true, user)
                .orElseThrow(() -> new EntityNotFoundException("No subscriptions associated with this user"));


        /*
        if (!subscription.getActiveStatus().isActive()){
            throw new EntityNotFoundException("Subscription is canceled, not possible to renew");
        }


        boolean userAuthorized = Long.valueOf(newString).equals(user.getId());
        if (!userAuthorized) {
            throw new NotFoundException("User not allowed to cancel the subscription");
        }
*/
        if (Objects.equals(subscription.getPaymentType().getPaymentType(), "monthly")){

            throw new IllegalArgumentException("You can not renew a monthly subscription");
        } else {

            subscription.checkChange(desiredVersion);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate endDate = LocalDate.parse(subscription.getEndDate().getEndDate(), formatter);

            subscription.getEndDate().setEndDate(String.valueOf(endDate.plusYears(1)));
        }
        return repository.save(subscription);
    }



    @Override
    public Subscriptions changePlan(final long desiredVersion, final String name) {


        Plans plan = plansRepository.findByActive_ActiveAndName_Name(true, name)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with name " + name));

        int deviceLimit = plan.getMaximumNumberOfUsers().getMaximumNumberOfUsers();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int commaIndex = username.indexOf(",");

        String newString;
        if (commaIndex != -1) {
            newString = username.substring(0, commaIndex);
        } else {
            newString = username;
        }


        User user = userRepository.findById(Long.valueOf(newString))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        Subscriptions subscription = repository.findByActiveStatus_ActiveAndUser(true, user)
                 .orElseThrow(() -> new EntityNotFoundException("No subscriptions associated with this user"));

        int activeDevices= deviceRepository.countBySubscription(subscription);

        if(activeDevices > deviceLimit){
            throw new IllegalArgumentException("The plan you are trying to change has a number of devices lower than the actual number of active devices. Please remove them and try again");
        }


        if(subscription.getPlan().getName().getName().equals(name)){
            throw new IllegalArgumentException("You are already subscribed to this plan");
        }



        subscription.changePlan(desiredVersion, plan);

        return repository.save(subscription);
    }


    @Override
    public void migrateAllToPlan(final long desiredVersion, final String actualPlan, final String newPlan) {

        if (actualPlan.equals(newPlan)) throw new DuplicateRequestException("You are trying to migrate all the users to their actual plan");


        Plans plan_old = plansRepository.findByActive_ActiveAndName_Name(true, actualPlan)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with name " + actualPlan));

        Plans plan_new = plansRepository.findByActive_ActiveAndName_Name(true, newPlan)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with name " + newPlan));




        List<Subscriptions> subs = repository.findAllByPlanAndActiveStatus_Active(plan_old, true);

        if (subs.isEmpty()) throw new EntityNotFoundException("No subscriptions associated with plan " + actualPlan);

        for (Subscriptions sub : subs) {
            int deviceLimit = plan_new.getMaximumNumberOfUsers().getMaximumNumberOfUsers();
            int activeDevices = deviceRepository.countBySubscription(sub);

            if (activeDevices > deviceLimit) {
                throw new IllegalArgumentException("There are users with a number of active devices higher than the number of devices allowed in the subscription you are trying to move them. Ask the user to remove them and try again");
            }
        }

        for (Subscriptions sub : subs) {


            sub.changePlan(desiredVersion, plan_new);

            repository.save(sub);
        }
    }
}





