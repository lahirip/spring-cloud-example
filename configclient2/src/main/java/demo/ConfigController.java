package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class ConfigController {


    @Autowired
    Environment env;

    @Value("${bar:World!}")
    String bar;

    @RequestMapping("/")
    public String home() {
        return "Hello " + bar + "!";
    }

    @RequestMapping("/property")
    public String property(@RequestParam(value = "name", defaultValue = "XXX") String name) {
        System.out.println();
        String propertyValue = env.getProperty(name);
        System.out.println(propertyValue);
        return propertyValue;
    }
}
