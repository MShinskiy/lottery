package com.lavkatech.lottery.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DecodedUserQuery {

    @JsonProperty("Timestamp")
    public LocalDateTime timestamp;

    @JsonProperty("User")
    public UserQueryProperty user;
}

@Data
class UserQueryProperty {
    @JsonProperty("Contact")
    public ContactQueryProperty contact;
}

@Data
class ContactQueryProperty {
    @JsonProperty("DTE_Contact_Id__c")
    public String dtprf;
    @JsonProperty("mapID")
    public String group;
    @JsonProperty("levelSA")
    public String level;
}