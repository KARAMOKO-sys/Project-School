package com.edueasy.user.service;

import com.edueasy.common.dto.TeacherCreatedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_TEACHER_CREATED = "teacher-created-topic";

    public void sendTeacherCreatedEvent(String teacherId, String teacherNumber, String firstName, String lastName, String email) {
        try {
            TeacherCreatedEvent event = TeacherCreatedEvent.builder().eventId(UUID.randomUUID().toString()).teacherId(teacherId).teacherNumber(teacherNumber).firstName(firstName).lastName(lastName).email(email).createdAt(LocalDateTime.now()).eventType("TEACHER_CREATED").build();
            log.info("\ud83d\udce4 Envoi de l'événement Kafka: {}", event);
            this.kafkaTemplate.send("teacher-created-topic", teacherId, event);
            log.info("✅ Événement envoyé avec succès au topic: {}", "teacher-created-topic");
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'événement Kafka: {}", e.getMessage());
        }

    }

    public KafkaProducerService(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}