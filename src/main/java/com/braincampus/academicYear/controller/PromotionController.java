package com.braincampus.academicYear.controller;
import com.braincampus.academicYear.dto.PromotionRequest;
import com.braincampus.academicYear.dto.PromotionResponse;
import com.braincampus.academicYear.service.PromotionService;
import com.braincampus.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasAuthority('PROMOTE_STUDENTS')")
    public ResponseEntity<ApiResponse<PromotionResponse>> promote(
            @Valid @RequestBody PromotionRequest request
    ) {

        PromotionResponse response =
                promotionService.promote(request);

        return ResponseEntity.ok(
                ApiResponse.<PromotionResponse>builder()
                        .success(true)
                        .message("Students promoted successfully")
                        .data(response)
                        .build()
        );
    }
}