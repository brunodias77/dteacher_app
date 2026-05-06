package com.dias.dteacher.repository;

import com.dias.dteacher.model.TextAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TextAnalysisRepository extends JpaRepository<TextAnalysis, UUID> {}
