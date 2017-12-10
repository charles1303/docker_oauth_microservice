package hello;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.micro.models.RequestDetail;
import com.projects.micro.repositories.RequestDetailRepository;

//@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages={"com.projects.micro"})
@RestController
@EntityScan(basePackages = {"com.projects.micro.models"})
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory", 
basePackages = { "com.projects.micro.repositories" })
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class,args);
        RequestDetail detail = new RequestDetail();
        //detail.setProductId("ABCD1234");
        detail.setRequestName("Dan's Book of Writing");
        detail.setShortDescription("A book about writing books.");
        detail.setLongDescription("In this book about writing books, Dan will show you how to write a book.");
        RequestDetailRepository repository = ctx.getBean(RequestDetailRepository.class);
        repository.save(detail);
        for (RequestDetail requestDetail : repository.findAll()) {
            System.out.println(requestDetail.getRequestName());
        }
    }

    /*public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }*/

    
}
