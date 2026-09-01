package com.braincampus.management.dto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class FeeCollectionResponse {

    private BigDecimal totalCollected;

    private Map<String, BigDecimal> collectionByPaymentMethod;

    private String startDate;

    private String endDate;
}