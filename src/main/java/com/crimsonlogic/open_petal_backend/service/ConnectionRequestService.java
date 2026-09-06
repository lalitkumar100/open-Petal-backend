package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;

public interface ConnectionRequestService {
    ConnectionRequest sendConnectionRequest(Long senderId, SendConnectionRequestDto dto);
    ConnectionRequest acceptConnectionRequest(Long receiverId, Long requestId);
}
