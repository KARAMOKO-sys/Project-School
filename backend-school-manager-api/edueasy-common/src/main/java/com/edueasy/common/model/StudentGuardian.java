package com.edueasy.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGuardian {

    @Column(name = "guardian_first_name", nullable = false)
    private String firstName;

    @Column(name = "guardian_last_name", nullable = false)
    private String lastName;

    @Column(name = "guardian_email")
    private String email;

    @Column(name = "guardian_phone")
    private String phone;

    @Column(name = "guardian_relation")
    private String relation;

    @Column(name = "is_primary_guardian")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "guardian_address")
    private String address;

    @Column(name = "guardian_occupation")
    private String occupation;

    @Column(name = "can_pickup_student")
    @Builder.Default
    private Boolean canPickupStudent = true;

    @Column(name = "authorized_to_access")
    @Builder.Default
    private Boolean authorizedToAccess = true;

    public boolean isPrimary() {
        return isPrimary != null && isPrimary;
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }

    public boolean hasPhone() {
        return phone != null && !phone.isEmpty();
    }

    public boolean canPickup() {
        return canPickupStudent != null && canPickupStudent;
    }

    public boolean isAuthorized() {
        return authorizedToAccess != null && authorizedToAccess;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isComplete() {
        return firstName != null && !firstName.isEmpty()
                && lastName != null && !lastName.isEmpty()
                && (hasEmail() || hasPhone());
    }

    public boolean isParent() {
        return relation != null && (relation.equalsIgnoreCase("Père") || relation.equalsIgnoreCase("Mère"));
    }

    public boolean isLegalGuardian() {
        return relation != null && (relation.equalsIgnoreCase("Tuteur légal") || relation.equalsIgnoreCase("Tuteur"));
    }
}