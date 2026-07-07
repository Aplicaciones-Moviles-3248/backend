package com.upc.courtly.matches.domain.model.commands;

public record ApproveMatchJoinRequestCommand(Long joinRequestId, Long approverId) {
}
