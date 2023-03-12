package com.luixtech.uidgenerator.usagedemo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class UidController {
    @Resource
    private UidGenerator uidGenerator;

    @GetMapping("/api/uid")
    public long generateUid() {
        long uid = uidGenerator.generateUid();
        String str = uidGenerator.parseUid(uid);
        log.info("Parsed uid: {}", str);
        return uid;
    }
}
