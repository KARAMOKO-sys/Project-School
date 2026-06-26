package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "parents")
@JsonTypeName("PARENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Parent extends User {

    private String relationship;

    private String occupation;

    @Builder.Default
    private Boolean receiveNotifications = true;

    @Builder.Default
    private String preferredLanguage = "fr";

    @Builder.Default
    private Boolean isPrimaryContact = false;

    @ElementCollection
    @CollectionTable(
        name = "parent_children",
        joinColumns = @JoinColumn(name = "parent_id")
    )
    @Column(name = "child_id")
    @Builder.Default
    private List<String> childIds = new ArrayList<>();

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.PARENT;
    }

    public List<String> getChildren() {
        return childIds != null ? childIds : new ArrayList<>();
    }

    public boolean canAccessChild(String childId) {
        return childIds != null && childIds.contains(childId);
    }

    public void addChild(String childId) {
        if (childIds == null) {
            childIds = new ArrayList<>();
        }
        if (!childIds.contains(childId)) {
            childIds.add(childId);
        }
    }

    public void removeChild(String childId) {
        if (childIds != null) {
            childIds.remove(childId);
        }
    }

    public boolean hasMultipleChildren() {
        return childIds != null && childIds.size() > 1;
    }

    public boolean isPrimaryContact() {
        return isPrimaryContact != null && isPrimaryContact;
    }

    public boolean hasChildren() {
        return childIds != null && !childIds.isEmpty();
    }

    public int getChildrenCount() {
        return childIds != null ? childIds.size() : 0;
    }

    public boolean shouldReceiveNotifications() {
        return receiveNotifications != null && receiveNotifications;
    }
}
