package com.roshik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgileResultsTrackerApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(AgileResultsTrackerApplication.class, args);
    }

}

