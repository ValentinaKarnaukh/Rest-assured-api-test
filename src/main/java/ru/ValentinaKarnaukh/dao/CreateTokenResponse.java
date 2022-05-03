package ru.ValentinaKarnaukh.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.processing.Generated;

public class CreateTokenResponse {
    @Getter
    @JsonProperty("token")
    private String token;
}
