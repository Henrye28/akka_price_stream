package com.henryye.cluster.model;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Builder
@Data
@EqualsAndHashCode
public class PriceResult implements Message{
    String coin;
    Double priceResult;
}
