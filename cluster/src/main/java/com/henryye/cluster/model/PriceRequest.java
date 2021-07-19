package com.henryye.cluster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties
@Builder
public class PriceRequest {
    public String coin;
    public String quantity;
}
