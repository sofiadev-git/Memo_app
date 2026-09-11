package it.uniroma3.siw.memo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MemoApplication {
    /* MAIN PAGE*/
    public static void main(String[] args) {
        /*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("LA MIA PASSWORD CIFRATA È: " + encoder.encode("amministratore"));
        //serve solo per generare la password dell ADMIN*/
        SpringApplication.run(MemoApplication.class, args);

    }


}
