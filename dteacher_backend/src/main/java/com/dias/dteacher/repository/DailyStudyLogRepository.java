package com.dias.dteacher.repository;

import com.dias.dteacher.model.DailyStudyLog;
import com.dias.dteacher.model.DailyStudyLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStudyLogRepository extends JpaRepository<DailyStudyLog, DailyStudyLogId> {
}
