package com.edueasy.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bloodType;

    private String allergies;

    private String medications;

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "doctor_phone")
    private String doctorPhone;

    @Column(name = "has_health_insurance")
    @Builder.Default
    private Boolean hasHealthInsurance = false;

    @Column(name = "health_insurance_number")
    private String healthInsuranceNumber;

    @Column(name = "special_instructions")
    private String specialInstructions;

    // ===== Méthodes métier =====

    public boolean hasAllergies() {
        return allergies != null && !allergies.isEmpty();
    }

    public boolean hasMedications() {
        return medications != null && !medications.isEmpty();
    }

    public List<String> getAllergyList() {
        if (allergies == null || allergies.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(allergies.split(","));
    }

    public boolean hasHealthInsurance() {
        return hasHealthInsurance != null && hasHealthInsurance;
    }

    public boolean hasEmergencyContact() {
        return emergencyContact != null && !emergencyContact.isEmpty();
    }

    public String getFullEmergencyContact() {
        if (emergencyContact != null && emergencyPhone != null) {
            return emergencyContact + " (" + emergencyPhone + ")";
        }
        return emergencyContact != null ? emergencyContact : emergencyPhone;
    }

    public boolean hasChronicDiseases() {
        return chronicDiseases != null && !chronicDiseases.isEmpty();
    }

    public List<String> getChronicDiseasesList() {
        if (chronicDiseases == null || chronicDiseases.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(chronicDiseases.split(","));
    }

    public boolean hasDoctorInfo() {
        return (
            (doctorName != null && !doctorName.isEmpty()) ||
            (doctorPhone != null && !doctorPhone.isEmpty())
        );
    }
}
