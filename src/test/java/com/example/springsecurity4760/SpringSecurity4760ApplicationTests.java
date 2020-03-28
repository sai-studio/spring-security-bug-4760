package com.example.springsecurity4760;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static com.example.springsecurity4760.SpringSecurity4760ApplicationTests.ServletApiConfig;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@SpringBootTest(classes = {ServletApiConfig.class})
class SpringSecurity4760ApplicationTests {

    ///// Setting Up MockMvc and Spring Security. start
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    ///// Setting Up MockMvc and Spring Security. end


    @SpringBootApplication
    @RestController
    static class ServletApiConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

//            Try the following code and it will pass the test case
//            http.csrf().disable()
//                    .servletApi().disable()
//                    .servletApi();
        }

        @GetMapping("api-logout")
        String logut(HttpServletRequest request, Authentication auth) throws ServletException {

            // set a sessioin attribute
            // If logout is successful, then the session will be cleared.
            request.getSession().setAttribute("user","admin");

            request.logout();

            return "logout success!";
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void testServletApiLogout() throws Exception {

        mvc.perform(get("/api-logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("logout success!"))
                .andExpect(request().sessionAttributeDoesNotExist("user"))
        ;
    }
}
