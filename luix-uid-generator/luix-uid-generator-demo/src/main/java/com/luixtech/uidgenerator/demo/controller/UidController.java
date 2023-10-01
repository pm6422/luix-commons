package com.luixtech.uidgenerator.demo.controller;

import com.luixtech.uidgenerator.core.uid.UidGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class UidController {
    private UidGenerator uidGenerator;

    @GetMapping("/api/uid")
    public long generateUid() {
        long uid = uidGenerator.generateUid();
        String str = uidGenerator.parseUid(uid);
        log.info("Parsed uid: {}", str);
        return uid;
    }
}
