package com.cheering.community;

import java.util.List;

public class CommunityRequest {
    public record ChangeOrderRequest (Long communityId, Integer communityOrder) { }

    public record JoinCommunitiesRequest (List<Long> communityIds) { }
}
