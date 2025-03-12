package cz.syntaxbro.erpsystem;

import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;

@SpringBootApplication(scanBasePackages = {"cz.syntaxbro"})
public class ErpSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpSystemApplication.class, args);
    }
    @Getter
    static Logger logger = LoggerFactory.getLogger(ErpSystemApplication.class.getName());

}