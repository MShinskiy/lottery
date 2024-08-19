package com.lavkatech.lottery.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import lombok.Data;

import java.time.LocalDateTime;

public class DecodedUserQuery {

    @JsonProperty("Timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    @JsonProperty("User")
    private UserQueryProperty user;

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getDtprf() {
        return this.user.getContact().getDtprf();
    }

    public Group getGroup() {
        return Group.valueOf(this.user.getContact().getGroup());
    }

    public Level getLevel() {
        return Level.valueOf(this.user.getContact().getLevel());
    }
}

@Data
class UserQueryProperty {
    @JsonProperty("Contact")
    private ContactQueryProperty contact;
}

@Data
class ContactQueryProperty {
    @JsonProperty("DTE_Contact_Id__c")
    private String dtprf;
    @JsonProperty("mapID")
    private String group;
    @JsonProperty("levelSA")
    private String level;
}