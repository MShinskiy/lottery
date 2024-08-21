package com.lavkatech.lottery.entity.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import lombok.Data;

import java.time.LocalDateTime;

public class DecodedUserQuery {

    @JsonCreator
    public DecodedUserQuery(@JsonProperty("Timestamp") LocalDateTime timestamp,
                            @JsonProperty("DTE_Contact_Id__c") String dtprf,
                            @JsonProperty("mapID") String group,
                            @JsonProperty("levelSA") String level) {
        this.timestamp = timestamp;
        this.user = new UserQueryProperty();
        this.user.setContact(new ContactQueryProperty());
        this.user.getContact().setDtprf(dtprf);
        this.user.getContact().setGroup(group);
        this.user.getContact().setLevel(level);
    }

    @JsonProperty("Timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    @JsonProperty("User")
    private UserQueryProperty user;

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String dtprf() {
        return this.user.getContact().getDtprf();
    }

    public Group group() {
        return Group.valueOf(this.user.getContact().getGroup());
    }

    public Level level() {
        return Level.valueOf(this.user.getContact().getLevel());
    }

    public DecodedUserQuery(String dtprf, Group group, Level level) {
        ContactQueryProperty cqp = new ContactQueryProperty();
        cqp.setDtprf(dtprf);
        cqp.setGroup(group.name());
        cqp.setLevel(level.name());
        UserQueryProperty uqp = new UserQueryProperty();
        uqp.setContact(cqp);
        this.user = uqp;
        this.timestamp = LocalDateTime.now();
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