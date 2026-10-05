package com.thelawala.dto;

import jakarta.validation.constraints.NotNull;

/** Admin payload to add likes (delta may be negative) to a vendor's current count. */
public class LikeAdjustRequest {

    @NotNull(message = "delta is required")
    private Integer delta;

    public Integer getDelta() { return delta; }
    public void setDelta(Integer delta) { this.delta = delta; }
}
