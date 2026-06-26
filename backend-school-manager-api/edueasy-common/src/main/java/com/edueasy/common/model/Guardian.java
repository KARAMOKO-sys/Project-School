package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guardians")
@JsonTypeName("GUARDIAN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Guardian extends User {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String occupation;

    private String relationship;

    @ElementCollection
    @CollectionTable(
            name = "guardian_wards",
            joinColumns = @JoinColumn(name = "guardian_id")
    )
    @Column(name = "ward_id")
    @Builder.Default
    private List<String> wardIds = new ArrayList<>();

    @Column(name = "is_primary_contact")
    @Builder.Default
    private Boolean isPrimaryContact = false;

    @Override
    public UserRole getRole() {
        return UserRole.GUARDIAN;
    }
}