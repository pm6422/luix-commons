package com.luixtech.uidgenerator.usagedemo;

import com.luixtech.framework.EnableLuixWebFramework;
import com.luixtech.uidgenerator.springboot.config.EnableUidGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableUidGenerator
@EnableLuixWebFramework
public class LuixUidDemoApplication {

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        // Disable JOOQ's self-ad message, that message is located in the org.jooq.impl.DefaultRenderContext source file,
        // and it is using the org.jooq.Constants logger.
        System.setProperty("org.jooq.no-logo", "true");
        SpringApplication.run(LuixUidDemoApplication.class, args);
    }
}
