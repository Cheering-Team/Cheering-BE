package com.cheering.community;

public class CommunityRequest {
    public record ChangeOrderRequest (Long communityId, Integer communityOrder) { }
}
