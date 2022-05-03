package ru.ValentinaKarnaukh.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

import java.text.DateFormat;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With
public class Bookingdates {

    @JsonProperty("checkin")
    private String checkin;
    @JsonProperty("checkout")
    private String checkout;


}
