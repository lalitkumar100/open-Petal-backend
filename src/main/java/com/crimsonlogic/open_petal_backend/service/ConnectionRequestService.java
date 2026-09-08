package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.connection.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;

/**
 * Service interface for connection request operations.
 */
public interface ConnectionRequestService {
    /**
     * Send Connection Request.
     */
    ConnectionRequest sendConnectionRequest(Long senderId, SendConnectionRequestDto dto);
    /**
     * Accept Connection Request.
     */
    ConnectionRequest acceptConnectionRequest(Long receiverId, Long requestId);
}
