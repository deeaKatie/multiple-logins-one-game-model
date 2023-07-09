package rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"repository", "rest"})
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        SpringApplication.run(Main.class, args); // how he know?
    }

//    @Bean(name="props")
//    @Primary
//    public Properties getBdProperties(){
//        Properties props = new Properties();
//        try {
//            System.out.println("Searching bd.config in directory "+((new File(".")).getAbsolutePath()));
//            props.load(new FileReader("db.config"));
//        } catch (IOException e) {
//            System.err.println("Configuration file bd.cong not found" + e);
//
//        }
//        return props;
//    }
}