package com.example.demo.config;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.kafka.KafkaConsumer;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.service.PersonDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final KafkaConsumer kafkaConsumer;
    private final KafkaProducer kafkaProducer;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public JWTFilter(KafkaConsumer kafkaConsumer, KafkaProducer kafkaProducer, PersonDetailsService personDetailsService) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
        this.personDetailsService = personDetailsService;
    }

    private void producerRecord(String key, String value) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>("jwtValidate_request", key, value);
        kafkaProducer.sendStringMessage(producerRecord);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String jwt = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null && !jwt.isBlank()) {
            try {
                producerRecord("jwtValidate", jwt);
                String username = kafkaConsumer.queueValidate().value();

                if (username != null) {
                    UserDetails userDetails = personDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.sendRedirect("/auth/login?error=invalid_user");// ошибки пофиксить
                    return;
                }

            } catch (JWTVerificationException e) {
                response.sendRedirect("/auth/login?error=invalid_jwt");
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                response.sendRedirect("/auth/login?error=validation_error");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
