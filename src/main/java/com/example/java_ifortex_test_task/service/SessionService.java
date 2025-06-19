package com.example.java_ifortex_test_task.service;

import com.example.java_ifortex_test_task.dto.SessionResponseDTO;
import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.Session;
import com.example.java_ifortex_test_task.entity.User;
import com.example.java_ifortex_test_task.mapper.SessionMapper;
import com.example.java_ifortex_test_task.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    // Returns the first (earliest) desktop Session
    public SessionResponseDTO getFirstDesktopSession() {
        List<Object[]> rows = sessionRepository.getFirstDesktopSession(DeviceType.DESKTOP);
        Object[] row = rows.get(0);

        return getSessionResponseDTO(row);
    }

    private SessionResponseDTO getSessionResponseDTO(Object[] row) {
        Session session = new Session();
        session.setId(((Number) row[0]).longValue());
        session.setDeviceType(DeviceType.fromCode(((Number) row[1]).intValue()));
        session.setEndedAtUtc(row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null);
        session.setStartedAtUtc(((Timestamp) row[3]).toLocalDateTime());

        User user = new User();
        user.setId(((Number) row[4]).longValue());
        user.setFirstName((String) row[5]);
        user.setLastName((String) row[6]);
        session.setUser(user);

        return sessionMapper.toDto(session);
    }

    // Returns only Sessions from Active users that were ended before 2025
    public List<SessionResponseDTO> getSessionsFromActiveUsersEndedBefore2025() {
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<Object[]> rows = sessionRepository.getSessionsFromActiveUsersEndedBefore2025(endDate);

        return rows.stream()
                .map(row -> {
                    return getSessionResponseDTO(row);
                })
                .toList();
    }
}