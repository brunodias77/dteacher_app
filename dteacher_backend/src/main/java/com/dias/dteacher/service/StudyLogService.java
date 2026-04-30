package com.dias.dteacher.service;

import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.DailyStudyLog;
import com.dias.dteacher.model.DailyStudyLogId;
import com.dias.dteacher.model.StudyStreak;
import com.dias.dteacher.model.User;
import com.dias.dteacher.repository.DailyStudyLogRepository;
import com.dias.dteacher.repository.StudyStreakRepository;
import com.dias.dteacher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudyLogService {

    private final UserRepository          userRepository;
    private final DailyStudyLogRepository dailyStudyLogRepository;
    private final StudyStreakRepository   studyStreakRepository;

    @Transactional
    public void recordCardReview(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> NotFoundException.of("User", email));

        LocalDate today = LocalDate.now();

        incrementDailyLog(user, today);
        updateStreak(user, today);
    }

    private void incrementDailyLog(User user, LocalDate today) {
        DailyStudyLogId logId = new DailyStudyLogId(user.getId(), today);
        DailyStudyLog log = dailyStudyLogRepository.findById(logId)
                .orElseGet(() -> DailyStudyLog.builder()
                        .id(logId)
                        .user(user)
                        .cardsReviewed(0)
                        .wordsAdded(0)
                        .build());
        log.setCardsReviewed(log.getCardsReviewed() + 1);
        dailyStudyLogRepository.save(log);
    }

    private void updateStreak(User user, LocalDate today) {
        StudyStreak streak = studyStreakRepository.findById(user.getId())
                .orElseGet(() -> StudyStreak.builder()
                        .userId(user.getId())
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .totalDays(0)
                        .build());

        LocalDate lastDate = streak.getLastStudyDate();

        if (today.equals(lastDate)) {
            return; // already counted today
        }

        streak.setTotalDays(streak.getTotalDays() + 1);
        streak.setLastStudyDate(today);

        if (lastDate != null && lastDate.equals(today.minusDays(1))) {
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            streak.setCurrentStreak(1);
        }

        streak.setLongestStreak(Math.max(streak.getLongestStreak(), streak.getCurrentStreak()));
        studyStreakRepository.save(streak);
    }
}
