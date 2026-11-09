package com.replate.service.request;

import com.replate.dto.request.CreateFoodRequestDTO;
import com.replate.dto.response.FoodRequestResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FoodRequestService {

    FoodRequestResponseDTO createRequest(CreateFoodRequestDTO request, Long ngoUserId);

    FoodRequestResponseDTO getRequestById(Long requestId, Long currentUserId, String currentUserRole);

    Page<FoodRequestResponseDTO> getMyRequests(Long ngoUserId, Pageable pageable);

    Page<FoodRequestResponseDTO> getReceivedRequests(Long donorUserId, Pageable pageable);

    FoodRequestResponseDTO approveRequest(Long requestId, Long donorUserId, String remarks);

    FoodRequestResponseDTO rejectRequest(Long requestId, Long donorUserId, String rejectionReason);

    FoodRequestResponseDTO withdrawRequest(Long requestId, Long ngoUserId);
}
