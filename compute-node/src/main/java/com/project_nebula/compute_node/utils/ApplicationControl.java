package com.project_nebula.compute_node.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationControl {

    private static ApplicationContext context;

    public static void exit(int status) {
        SpringApplication.exit(context, () -> 1);
        System.exit(status);
    }

}
