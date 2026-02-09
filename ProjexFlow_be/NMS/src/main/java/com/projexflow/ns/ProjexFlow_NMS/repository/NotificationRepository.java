package com.projexflow.ns.ProjexFlow_NMS.repository;

import com.projexflow.ns.ProjexFlow_NMS.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientIdAndRecipientRoleOrderByCreatedAtDesc(Long recipientId, String recipientRole);
}
