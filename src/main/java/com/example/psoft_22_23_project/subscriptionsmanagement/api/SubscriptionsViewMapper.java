package com.example.psoft_22_23_project.subscriptionsmanagement.api;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class SubscriptionsViewMapper {

    @Mapping(source="plan.name.name", target= "planName")
    @Mapping(source="plan.description.description", target= "planDescription")
    @Mapping(source="user.username", target= "username")
    @Mapping(source="subscriptions.startDate.startDate", target= "startDate")
    @Mapping(source="subscriptions.endDate.endDate", target= "endDate")
    @Mapping(source="subscriptions.paymentType.paymentType", target= "paymentType")
    @Mapping(source="subscriptions.activeStatus.active", target= "activeStatus")
    public abstract SubscriptionsView toSubscriptionView(Subscriptions subscriptions);

    public abstract Iterable<SubscriptionsView> toSubscriptionsView(Iterable<Subscriptions> subscriptions);

    public Integer mapOptInt(final Optional<Integer> i) {
        return i != null ? i.orElse(null) : null;
    }

    public Long mapOptLong(final Optional<Long> i) {
        return i != null ? i.orElse(null) : null;
    }

    public String mapOptString(final Optional<String> i) {
        return i != null ? i.orElse(null) : null;
    }
}
