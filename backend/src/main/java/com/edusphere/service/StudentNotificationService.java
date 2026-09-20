package com.edusphere.service;

import com.edusphere.dto.NotificationDTO;
import com.edusphere.entity.Notification;
import com.edusphere.entity.StudentProfile;
import com.edusphere.repository.NotificationRepository;
import com.edusphere.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentNotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentProfileRepository studentProfileRepository;

    public List<NotificationDTO> getNotificationsByEmail(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId, String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return convertToDTO(saved);
    }

    @Transactional
    public void createNotification(StudentProfile student, String title, String message, String type) {
        Notification notification = Notification.builder()
                .student(student)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public long getUnreadCount(Long studentId) {
        return notificationRepository.countByStudentIdAndIsReadFalse(studentId);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
