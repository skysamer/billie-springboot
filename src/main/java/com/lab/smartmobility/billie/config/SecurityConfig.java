package com.lab.smartmobility.billie.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests((requests) ->
                requests.antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                        .antMatchers("/login", "/joinIn", "/findPassword", "/check-login", "/swagger-ui/index.html").permitAll()
                        .antMatchers("/meeting/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/equipment/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        //.antMatchers("/vehicle/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        //.antMatchers("/traffic-card/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        //.antMatchers("/vacation/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .and()
                        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class)
        );
    }

}
