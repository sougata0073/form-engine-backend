package com.sougata.form_data_service.dto.response.summary;

import com.sougata.form_data_service.constant.RatingIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RatingResponseSummaryDto extends ResponseSummaryDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Double averageRating;

    private RatingIcon ratingIcon;

    private Integer maxRatingNumber;

    private List<Response> responses;

    public record Response(

            Integer rating,

            @JsonSerialize(using = ToStringSerializer.class)
            Long responseCount
    ) {
    }

}
