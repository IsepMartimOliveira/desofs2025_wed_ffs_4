package com.example.psoft_22_23_project.subscriptionsmanagement.repositories;


import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {


    Optional<Subscriptions> findById(long id);


    Optional<Subscriptions> findByPlan(Plans plans);

    boolean existsByPlanAndUserNotNull(Plans plans);

    @Query("SELECT s FROM Subscriptions s WHERE s.id = :id")
    List<Subscriptions> searchById(@Param("id") Long id);

    Optional<Subscriptions> findByUser(User user);


    List<Subscriptions> findAllByPlanAndActiveStatus_Active(Plans plan, @NonNull boolean activeStatus_active);

    Optional<Subscriptions> findByActiveStatus_ActiveAndUser(@NotNull boolean active, @NotNull User user);

}



