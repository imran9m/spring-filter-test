package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringFilterTest {

    @Test
    public void getHealthTest() throws Exception {
        standaloneSetup(new PersonController()).addFilter(new SkipFilter()).build().perform(get("/health")).andExpect(status().isOk());
    }

    @Test
    public void getPersonTest() throws Exception {
        standaloneSetup(new PersonController()).addFilter(new SkipFilter()).build().perform(get("/person")).andExpect(status().isAccepted());
    }


    private class SkipFilter extends OncePerRequestFilter {

        private Set<String> skipUrls = new HashSet<>(Arrays.asList("/health"));
        private AntPathMatcher pathMatcher = new AntPathMatcher();

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(request, response);
            response.setStatus(HttpStatus.ACCEPTED.value());
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            return skipUrls.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
        }
    }

    @RestController
    @RequestMapping(value = "/")
    private static class PersonController {

        @GetMapping("person")
        public void getPerson() {
        }

        @GetMapping("health")
        public void getHealth() {
        }
    }

}