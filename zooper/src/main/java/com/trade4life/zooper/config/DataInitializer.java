package com.trade4life.zooper.config;

import com.trade4life.zooper.model.ERole;
import com.trade4life.zooper.model.Role;
import com.trade4life.zooper.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        for(ERole roleEnum : ERole.values()){
            if(roleRepository.findByName(roleEnum).isEmpty()){
                roleRepository.save(new Role(roleEnum));
                logger.info("Created role: {}", roleEnum.name());
            }
        }
    }
}
