package com.netcracker.pojoServices.pojoForSecondService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoToFinishRepair {
    @JsonProperty("price")
    private Integer price;

}
