package com.lavkatech.lottery.service;

import org.springframework.stereotype.Service;

@Service
public interface LotteryService {
    long getValue();
    void setValue(long value);
}
