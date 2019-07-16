package com.netcracker.pojoServices.pojoForSecondService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
public class HttpDtoToAcceptRepair {
    @JsonProperty("rowId")
    private Integer rowId;

    @JsonProperty("day")
    private Date day;

}
