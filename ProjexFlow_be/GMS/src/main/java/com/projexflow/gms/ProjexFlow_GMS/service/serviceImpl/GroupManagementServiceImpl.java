package com.projexflow.gms.ProjexFlow_GMS.service.serviceImpl;

import com.projexflow.gms.ProjexFlow_GMS.dto.*;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.NotificationCreateRequest;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;
import com.projexflow.gms.ProjexFlow_GMS.service.GroupManagementService;
import com.projexflow.gms.ProjexFlow_GMS.service.NotificationClient;
import com.projexflow.gms.ProjexFlow_GMS.service.UmsClient;
import lombok.RequiredArgsConstructor;
import com.projexflow.gms.ProjexFlow_GMS.entity.*;
import com.projexflow.gms.ProjexFlow_GMS.exception.*;
import com.projexflow.gms.ProjexFlow_GMS.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupManagementServiceImpl implements GroupManagementService {

        private final GroupJoinRequestRepo requestRepo;
        private final GroupRepo groupRepo;
        private final GroupMemberRepo memberRepo;
        private final UmsClient umsClient;
        private final NotificationClient notificationClient;

        public Long sendRequest(Long fromStudentId, SendRequestDto dto) {
                if (dto.getCourse() == null || dto.getCourse().isBlank())
                        throw new BadRequestException("Course is required to send a group request");
                if (dto.getToStudentId().equals(fromStudentId))
                        throw new BadRequestException("Cannot send request to self");

                // (Optional UX rule) block if sender already in a group
                // remove this if you want them to keep sending requests after group formed
                if (memberRepo.findActiveGroupId(dto.getBatchId(), fromStudentId).isPresent())
                        throw new BadRequestException("You are already in a group");

                boolean exists = requestRepo.existsByBatchIdAndCourseIgnoreCaseAndFromStudentIdAndToStudentIdAndStatus(
                                dto.getBatchId(), dto.getCourse(), fromStudentId, dto.getToStudentId(),
                                RequestStatus.PENDING);
                if (exists)
                        throw new BadRequestException("Request already pending");

                Instant now = Instant.now();
                GroupJoinRequest req = GroupJoinRequest.builder()
                                .batchId(dto.getBatchId())
                                .course(dto.getCourse())
                                .fromStudentId(fromStudentId)
                                .toStudentId(dto.getToStudentId())
                                .status(RequestStatus.PENDING)
                                .createdAt(now)
                                .build();

                Long id = requestRepo.save(req).getId();

                // ----------------- In-app notification (UI) -----------------
                // Notify recipient: "You received a group invite".
                try {
                        Map<String, Object> meta = new HashMap<>();
                        meta.put("batchId", dto.getBatchId());
                        meta.put("fromStudentId", fromStudentId);
                        meta.put("requestId", id);
                        notificationClient.create(new NotificationCreateRequest(
                                        dto.getToStudentId(),
                                        "STUDENT",
                                        "GROUP_INVITE",
                                        "Group invitation",
                                        "You have received a group invitation.",
                                        "GROUP_JOIN_REQUEST",
                                        id,
                                        meta));
                } catch (Exception ignored) {
                        // Notification failure must not break core workflow.
                }

                return id;
        }

        @Override
        public GroupDetailsResponse getGroupDetails(Long groupId) {
                Group group = groupRepo.findById(groupId)
                                .orElseThrow(() -> new NotFoundException("Group not found"));

                List<GroupMember> members = memberRepo.findAllByGroupIdAndActiveTrue(groupId);
                List<Long> studentIds = members.stream().map(GroupMember::getStudentId).toList();
                List<UmsStudentResponse> students = umsClient.getStudentsByIds(studentIds);

                Map<Long, UmsStudentResponse> byId = students.stream().collect(
                                java.util.stream.Collectors.toMap(UmsStudentResponse::getId, s -> s, (a, b) -> a));

                List<GroupDetailsResponse.MemberDto> memberDtos = members.stream()
                                .map(m -> new GroupDetailsResponse.MemberDto(
                                                m.getId(),
                                                m.getStudentId(),
                                                m.isActive(),
                                                m.getJoinedAt(),
                                                m.getLeftAt(),
                                                byId.get(m.getStudentId())))
                                .toList();

                return new GroupDetailsResponse(group.getId(), group.getBatchId(), group.getCourse(),
                                group.getCreatedAt(), memberDtos);
        }

        @Override
        public List<GroupDetailsResponse> getBatchGroups(Long batchId) {
                List<Group> groups = groupRepo.findAllByBatchId(batchId);
                if (groups.isEmpty())
                        return List.of();

                // Fetch all active members for the batch in ONE query (UI-friendly)
                List<GroupMember> members = memberRepo.findAllByBatchIdAndActiveTrue(batchId);

                // Batch fetch student profiles from UMS (UI-friendly)
                List<Long> studentIds = members.stream().map(GroupMember::getStudentId).distinct().toList();
                List<UmsStudentResponse> students = studentIds.isEmpty() ? List.of()
                                : umsClient.getStudentsByIds(studentIds);

                Map<Long, UmsStudentResponse> byStudentId = students.stream().collect(
                                java.util.stream.Collectors.toMap(UmsStudentResponse::getId, s -> s, (a, b) -> a));

                // groupId -> members
                Map<Long, List<GroupMember>> membersByGroup = members.stream().collect(
                                java.util.stream.Collectors.groupingBy(GroupMember::getGroupId));

                return groups.stream().map(g -> {
                        List<GroupMember> gm = membersByGroup.getOrDefault(g.getId(), List.of());
                        List<GroupDetailsResponse.MemberDto> dtos = gm.stream()
                                        .map(m -> new GroupDetailsResponse.MemberDto(
                                                        m.getId(),
                                                        m.getStudentId(),
                                                        m.isActive(),
                                                        m.getJoinedAt(),
                                                        m.getLeftAt(),
                                                        byStudentId.get(m.getStudentId())))
                                        .toList();
                        return new GroupDetailsResponse(g.getId(), g.getBatchId(), g.getCourse(), g.getCreatedAt(),
                                        dtos);
                }).toList();
        }

        @Override
        public List<GroupDetailsResponse> getBatchGroups(Long batchId, String course) {
                if (course == null || course.isBlank())
                        return getBatchGroups(batchId);

                List<Group> groups = groupRepo.findAllByBatchIdAndCourseIgnoreCase(batchId, course);
                if (groups.isEmpty())
                        return List.of();

                List<GroupMember> members = memberRepo.findAllByBatchIdAndCourseIgnoreCaseAndActiveTrue(batchId,
                                course);

                List<Long> studentIds = members.stream().map(GroupMember::getStudentId).distinct().toList();
                List<UmsStudentResponse> students = studentIds.isEmpty() ? List.of()
                                : umsClient.getStudentsByIds(studentIds);

                Map<Long, UmsStudentResponse> byStudentId = students.stream().collect(
                                java.util.stream.Collectors.toMap(UmsStudentResponse::getId, s -> s, (a, b) -> a));

                Map<Long, List<GroupMember>> membersByGroup = members.stream().collect(
                                java.util.stream.Collectors.groupingBy(GroupMember::getGroupId));

                return groups.stream().map(g -> {
                        List<GroupMember> gm = membersByGroup.getOrDefault(g.getId(), List.of());
                        List<GroupDetailsResponse.MemberDto> dtos = gm.stream()
                                        .map(m -> new GroupDetailsResponse.MemberDto(
                                                        m.getId(),
                                                        m.getStudentId(),
                                                        m.isActive(),
                                                        m.getJoinedAt(),
                                                        m.getLeftAt(),
                                                        byStudentId.get(m.getStudentId())))
                                        .toList();
                        return new GroupDetailsResponse(g.getId(), g.getBatchId(), g.getCourse(), g.getCreatedAt(),
                                        dtos);
                }).toList();
        }

        @Override
        public MyGroupResponse getMyGroup(Long batchId, Long studentId) {
                Long groupId = memberRepo.findActiveGroupId(batchId, studentId).orElse(null);
                if (groupId == null)
                        return new MyGroupResponse(null, null);
                return new MyGroupResponse(groupId, getGroupDetails(groupId));
        }

        @Override
        public long countMyGroupMembers(Long batchId, Long studentId) {
                Long groupId = memberRepo.findActiveGroupId(batchId, studentId).orElse(null);
                if (groupId == null)
                        return 0L;
                return memberRepo.countActiveMembers(groupId);
        }

        @Transactional
        public ActionResponse accept(Long requestId, Long currentStudentId) {
                Instant now = Instant.now();

                GroupJoinRequest req = requestRepo.findByIdForUpdate(requestId)
                                .orElseThrow(() -> new NotFoundException("Request not found"));

                if (req.getStatus() != RequestStatus.PENDING)
                        throw new BadRequestException("Request is not pending");

                if (!req.getToStudentId().equals(currentStudentId))
                        throw new ForbiddenException("Not your request to accept");

                Long batchId = req.getBatchId();
                String course = req.getCourse();
                Long fromId = req.getFromStudentId(); // requester
                Long toId = req.getToStudentId(); // acceptor

                // Defensive: ensure both students are still enrolled in the same course as the
                // request.
                // (Course may change in UMS; we treat that as invalid for grouping.)
                try {
                        List<UmsStudentResponse> check = umsClient.getStudentsByIds(List.of(fromId, toId));
                        Map<Long, UmsStudentResponse> m = check.stream().collect(
                                        java.util.stream.Collectors.toMap(UmsStudentResponse::getId, s -> s,
                                                        (a, b) -> a));
                        String fromCourse = m.get(fromId) == null ? null : m.get(fromId).getCourse();
                        String toCourse = m.get(toId) == null ? null : m.get(toId).getCourse();
                        if (course == null || fromCourse == null || toCourse == null ||
                                        !course.equalsIgnoreCase(fromCourse) || !course.equalsIgnoreCase(toCourse)) {
                                throw new CourseMismatchException(
                                                "Students must be enrolled in the same course to form/join a group");
                        }
                } catch (CourseMismatchException e) {
                        throw e;
                } catch (Exception ignored) {
                        // If UMS is temporarily unavailable, fall back to request-stored course only.
                }

                Long toGroupId = memberRepo.findActiveGroupId(batchId, toId).orElse(null);
                Long fromGroupId = memberRepo.findActiveGroupId(batchId, fromId).orElse(null);

                // If acceptor already has 2 members => Already full (your requirement)
                if (toGroupId != null) {
                        List<GroupMember> lockedMembers = memberRepo.findActiveMembersForUpdate(toGroupId);
                        if (lockedMembers.size() >= 2) {
                                return finishWith(req, now, RequestStatus.REJECTED,
                                                ActionResponse.builder().message("Already full").build());
                        }
                        // acceptor group has space, but requester must not already be in some other
                        // group
                        if (fromGroupId != null && !fromGroupId.equals(toGroupId))
                                throw new BadRequestException("Requester already in another group");

                        addMember(toGroupId, batchId, course, fromId, now);
                        return finishWith(req, now, RequestStatus.ACCEPTED,
                                        ActionResponse.builder().message("OK").groupId(toGroupId).build());
                }

                // acceptor has no group; if requester has a group with space -> join it
                if (fromGroupId != null) {
                        List<GroupMember> lockedMembers = memberRepo.findActiveMembersForUpdate(fromGroupId);
                        if (lockedMembers.size() >= 2) {
                                return finishWith(req, now, RequestStatus.REJECTED,
                                                ActionResponse.builder().message("Already full").build());
                        }
                        addMember(fromGroupId, batchId, course, toId, now);
                        return finishWith(req, now, RequestStatus.ACCEPTED,
                                        ActionResponse.builder().message("OK").groupId(fromGroupId).build());
                }

                // neither has a group -> create new group with both
                Group g = groupRepo.save(Group.builder()
                                .batchId(batchId)
                                .course(course)
                                .createdAt(now)
                                .build());

                addMember(g.getId(), batchId, course, fromId, now);
                addMember(g.getId(), batchId, course, toId, now);

                return finishWith(req, now, RequestStatus.ACCEPTED,
                                ActionResponse.builder().message("OK").groupId(g.getId()).build());
        }

        @Transactional
        public ActionResponse reject(Long requestId, Long currentStudentId) {
                GroupJoinRequest req = requestRepo.findByIdForUpdate(requestId)
                                .orElseThrow(() -> new NotFoundException("Request not found"));

                if (req.getStatus() != RequestStatus.PENDING)
                        throw new BadRequestException("Request is not pending");

                if (!req.getToStudentId().equals(currentStudentId))
                        throw new ForbiddenException("Not your request to reject");

                return finishWith(req, Instant.now(), RequestStatus.REJECTED,
                                ActionResponse.builder().message("REJECTED").build());
        }

        @Override
        public List<IncomingGroupRequestResponse> getIncomingRequests(Long batchId, String course, Long studentId) {
                // Only show PENDING in UI request section (recommended)
                List<GroupJoinRequest> requests = requestRepo
                                .findByBatchIdAndCourseIgnoreCaseAndToStudentIdOrderByCreatedAtDesc(batchId, course,
                                                studentId);

                if (requests.isEmpty())
                        return List.of();

                // Fetch sender details from UMS in one call (UI-friendly, avoids multiple API
                // calls)
                List<Long> senderIds = requests.stream().map(GroupJoinRequest::getFromStudentId).distinct().toList();

                List<UmsStudentResponse> senders = umsClient.getStudentsByIds(senderIds);

                Map<Long, UmsStudentResponse> senderMap = senders.stream()
                                .collect(Collectors.toMap(UmsStudentResponse::getId, s -> s));

                // Build UI-friendly response
                return requests.stream()
                                .map(r -> IncomingGroupRequestResponse.builder().requestId(r.getId())
                                                .batchId(r.getBatchId()).course(r.getCourse()).status(r.getStatus())
                                                .createdAt(r.getCreatedAt())
                                                .actionable(r.getStatus() == RequestStatus.PENDING)
                                                .sender(senderMap.get(r.getFromStudentId())).build())
                                .toList();
        }

        @Override
        public List<SentGroupRequestResponse> getSentRequests(Long batchId, String course, Long studentId) {
                List<GroupJoinRequest> requests = requestRepo
                                .findByBatchIdAndCourseIgnoreCaseAndFromStudentIdOrderByCreatedAtDesc(batchId, course,
                                                studentId);

                if (requests.isEmpty())
                        return List.of();

                // Fetch recipient details from UMS in one call
                List<Long> recipientIds = requests.stream().map(GroupJoinRequest::getToStudentId).distinct().toList();
                List<UmsStudentResponse> recipients = umsClient.getStudentsByIds(recipientIds);
                Map<Long, UmsStudentResponse> recipientMap = recipients.stream()
                                .collect(Collectors.toMap(UmsStudentResponse::getId, s -> s));

                // Build UI-friendly response
                return requests.stream()
                                .map(r -> {
                                        UmsStudentResponse recipient = recipientMap.get(r.getToStudentId());
                                        return SentGroupRequestResponse.builder()
                                                        .id(r.getId())
                                                        .toStudentId(r.getToStudentId())
                                                        .toStudentEmail(recipient != null ? recipient.getEmail() : null)
                                                        .toStudentName(recipient != null ? recipient.getFullName() : null)
                                                        .status(r.getStatus().name())
                                                        .createdAt(r.getCreatedAt())
                                                        .respondedAt(r.getRespondedAt())
                                                        .build();
                                })
                                .toList();
        }

        private void addMember(Long groupId, Long batchId, String course, Long studentId, Instant now) {
                // defensive: ensure group still has space (in case of races)
                long count = memberRepo.countActiveMembers(groupId);
                if (count >= 2)
                        throw new BadRequestException("Already full");

                memberRepo.save(GroupMember.builder().groupId(groupId).batchId(batchId).course(course)
                                .studentId(studentId).active(true).joinedAt(now).build());
        }

        private ActionResponse finishWith(GroupJoinRequest req, Instant now, RequestStatus status,
                        ActionResponse resp) {
                req.setStatus(status);
                req.setRespondedAt(now);
                requestRepo.save(req);

                // 🔔 Notify requester about the response
                try {
                        if (status == RequestStatus.ACCEPTED) {
                                Map<String, Object> meta = new HashMap<>();
                                meta.put("requestId", req.getId());
                                meta.put("groupId", resp.getGroupId());
                                meta.put("batchId", req.getBatchId());
                                meta.put("acceptedByStudentId", req.getToStudentId());

                                notificationClient.create(new NotificationCreateRequest(
                                                req.getFromStudentId(),
                                                "STUDENT",
                                                "GROUP_INVITE_ACCEPTED",
                                                "Invitation accepted",
                                                "Your group invitation was accepted!",
                                                "GROUP_JOIN_REQUEST",
                                                req.getId(),
                                                meta));
                        } else if (status == RequestStatus.REJECTED) {
                                Map<String, Object> meta = new HashMap<>();
                                meta.put("requestId", req.getId());
                                meta.put("batchId", req.getBatchId());
                                meta.put("rejectedByStudentId", req.getToStudentId());

                                notificationClient.create(new NotificationCreateRequest(
                                                req.getFromStudentId(),
                                                "STUDENT",
                                                "GROUP_INVITE_REJECTED",
                                                "Invitation rejected",
                                                "Your group invitation was rejected.",
                                                "GROUP_JOIN_REQUEST",
                                                req.getId(),
                                                meta));
                        }
                } catch (Exception ignored) {
                        // Notification failure must not break core workflow
                }

                return resp;
        }
}
