package apx.inc.iam_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IamServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServicesApplication.class, args);
    }

}
