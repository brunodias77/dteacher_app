package com.dias.dteacher.usecase.textanalysis.save;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SaveTextAnalysisResponse(UUID id, OffsetDateTime createdAt) {}
