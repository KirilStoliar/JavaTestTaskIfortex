package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u.* FROM users u " +
            "            JOIN (SELECT user_id, MAX(started_at_utc) as last_start" +
            "            FROM sessions " +
            "            WHERE device_type = :#{#deviceType.ordinal() + 1} " +
            "            GROUP BY user_id) " +
            "            s ON u.id = s.user_id " +
            "            ORDER BY s.last_start DESC", nativeQuery = true)
    List<User> getUsersWithAtLeastOneMobileSession(DeviceType deviceType);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN (SELECT user_id " +
            "        FROM sessions " +
            "        GROUP BY user_id " +
            "        ORDER BY COUNT(*) DESC " +
            "        LIMIT 1) s ON u.id = s.user_id", nativeQuery = true)
    User getUserWithMostSessions();
}
