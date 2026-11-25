package com.crimsonlogic.open_petal_backend.connection.service;

import com.crimsonlogic.open_petal_backend.connection.dto.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.connection.entity.ConnectionRequest;

public interface ConnectionRequestService {
    ConnectionRequest sendConnectionRequest(Long senderId, SendConnectionRequestDto dto);
    ConnectionRequest acceptConnectionRequest(Long receiverId, Long requestId);
}
