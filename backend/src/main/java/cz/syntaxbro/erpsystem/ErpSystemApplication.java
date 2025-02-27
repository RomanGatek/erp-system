package cz.syntaxbro.erpsystem;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class ErpSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpSystemApplication.class, args);
    }


    @Getter
    static Logger logger = Logger.getLogger(ErpSystemApplication.class.getName());

}