package com.henryye.cluster.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class Task implements Message{
    public String coinId;
    public double quantity;
    public double price;

    public enum Stop implements Message {
        INSTANCE
    }
}
