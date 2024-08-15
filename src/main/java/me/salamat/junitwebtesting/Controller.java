package me.salamat.junitwebtesting;

import me.salamat.junitwebtesting.junit.TestExecutor;
import me.salamat.junitwebtesting.junit.TestResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@org.springframework.stereotype.Controller
@EnableWebMvc
public class Controller {


    @GetMapping(value = {"", "/", "/home"})
    public ModelAndView home(){
        return new ModelAndView("index");
    }

    @PostMapping(value = "", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    //ResponseEntity so that Thymeleaf or MVC dont think its a View
    public ResponseEntity<String> testResult(@RequestBody String code){
        //Super ugly but it works
        String className = code.substring(code.indexOf("class "), code.indexOf(" {")).replaceAll("class ", "");
        return ResponseEntity.ok(new TestExecutor(className, code).runTest().getMessage());
    }
}
