package com.lavkatech.lottery.entity.enumeration;

import lombok.Getter;

@Getter
public enum Group {
    PARTNER(9),
    OTHER(6);

    private final int val;
    Group(int n){
        this.val = n;
    }
}
