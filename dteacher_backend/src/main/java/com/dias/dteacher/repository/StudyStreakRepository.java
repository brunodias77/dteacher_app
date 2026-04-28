package com.dias.dteacher.repository;

import com.dias.dteacher.model.StudyStreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudyStreakRepository extends JpaRepository<StudyStreak, UUID> {
}
