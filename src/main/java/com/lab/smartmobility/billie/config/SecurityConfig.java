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
                        .antMatchers("/login", "/sign-up", "/findPassword", "/check-login", "/send-email-token", "/verify-email-token", "/swagger-ui/index.html").permitAll()
                        .antMatchers("/meeting/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/equipment/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                        /*------------------------------------------------------------------------------------차량-----------------------------------------------------------------------------------*/
                        .antMatchers( "/vehicle/discard/{vehicle-num}", "/vehicle/excel/{disposal-info}/{vehicle-num}/{base-date}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/vehicle/{vehicle-num}").hasRole("ADMIN")
                        .antMatchers( "/vehicle/modify", "/vehicle/register", "/vehicle/return-count/{disposal-info}/{vehicle-num}/{base-date}").hasRole("ADMIN")
                        .antMatchers( "/vehicle/return-image/{rent-num}", "/vehicle/return-list/{disposal-info}/{vehicle-num}/{base-date}", "/vehicle/return/{rent-num}").hasRole("ADMIN")

                        .antMatchers("/vehicle/apply-rental", "/vehicle/insert-return", "/vehicle/modify/{rent-num}", "/vehicle/my/{staff-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/vehicle/reservation-list/{startDate}/{endDate}", "/vehicle/reservation/{rent-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/vehicle/{vehicle-num}", "/vehicle/to-own").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/vehicle/admin/**").hasRole("ADMIN")

                        /*------------------------------------------------------------------------------------교통카드-----------------------------------------------------------------------------------*/
                        .antMatchers("/traffic-card/discard/{card-num}", "traffic-card/excel/{disposal-info}/{card-num}/{base-date}").hasRole("ADMIN")
                        .antMatchers("/traffic-card/modify", "/traffic-card/register", "/traffic-card/return-count/{disposal-info}/{card-num}/{base-date}").hasRole("ADMIN")
                        .antMatchers("/traffic-card/return/{reservation-num}", "/traffic-card/return-list/{disposal-info}/{card-num}/{base-date}").hasRole("ADMIN")

                        .antMatchers("/traffic-card/apply-rental", "/traffic-card/apply-return", "/traffic-card/card-list", "/traffic-card/card/{card-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/traffic-card/modify/{reservation-num}", "/traffic-card/my/{staff-num}", "/traffic-card/remove/{reservation-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/traffic-card/rental-list/{start-date}/{end-date}", "/traffic-card/reservation/{reservation-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/traffic-card/{card-num}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/traffic-card/admin/**").hasRole("ADMIN")

                        /*------------------------------------------------------------------------------------법인카드-----------------------------------------------------------------------------------*/
                       /* .antMatchers("/corporation-card/admin/expense-history/{base-year}/{page}/{size}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/admin/return-history/{disposal-info}/{card-name}/{base-year}/{page}/{size}", "/corporation-card/approve/admin").hasRole("ADMIN")
                        .antMatchers("/corporation-card/count/admin/expense-history/{base-year}", "/corporation-card/count/admin/return-history/{disposal-info}/{card-name}/{base-year}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/disposal/{card-id}", "/corporation-card/excel/expense-history/{base-year}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/remove/{card-id}", "/corporation-card/request-count-admin/{card-name}/{base-year}/{disposal-info}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/request-list-admin/{card-name}/{base-year}/{disposal-info}/{page}/{size}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/admin/remove/application/{application-id}").hasRole("ADMIN")
                        .antMatchers("/corporation-card/excel/return-history/{disposal-info}/{card-name}/{base-year}", "/corporation-card/insert", "/corporation-card/modify/{card-id}").hasRole("ADMIN")

                        .antMatchers("/corporation-card/approve/manager", "/corporation-card/count/manager/expense-history/{manager-num}/{base-year}").hasRole("MANAGER")
                        .antMatchers("/corporation-card/count/manager/return-history/{manager-num}/{disposal-info}/{card-name}/{base-year}").hasRole("MANAGER")
                        .antMatchers("/corporation-card/request-count-manager/{manager-num}/{card-name}/{base-year}/{disposal-info}").hasRole("MANAGER")
                        .antMatchers("/corporation-card/request-list-manager/{manager-num}/{card-name}/{base-year}/{disposal-info}/{page}/{size}").hasRole("MANAGER")
                        .antMatchers("/corporation-card/manager/expense-history/{manager-num}/{base-year}/{page}/{size}").hasRole("MANAGER")
                        .antMatchers("/corporation-card/manager/return-history/{manager-num}/{disposal-info}/{card-name}/{base-year}/{page}/{size}").hasRole("MANAGER")

                        .antMatchers("/corporation-card/companion/card-use").hasAnyRole("MANAGER", "ADMIN")

                        .antMatchers("/corporation-card/approved/get/{application-id}", "/corporation-card/approved/monthly/{start-date}/{end-date}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/approved/my/{staff-num}", "/corporation-card/count/my-expense-history/{staff-num}/{base-year}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/count/my-return-history/{staff-num}/{card-name}/{base-year}", "/corporation-card/expense-claim").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/modify/application/{application-id}", "/corporation-card/my-application-count/{staff-num}/{card-name}/{base-year}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/expense-history/{expense-id}", "/corporation-card/list/{disposal-info}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/my-application-list/{staff-num}/{card-name}/{base-year}/{page}/{size}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/my-application/{staff-num}/{application-id}", "/corporation-card/return-history/{return-id}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/my-expense-history/{staff-num}/{base-year}/{page}/{size}", "/corporation-card/{card-id}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/my-return-history/{staff-num}/{card-name}/{base-year}/{page}/{size}").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/corporation-card/remove/application/{application-id}", "/corporation-card/rent", "/corporation-card/return").hasAnyRole("USER", "MANAGER", "ADMIN")*/

                        /*------------------------------------------------------------------------------------마이 페이지-----------------------------------------------------------------------------------*/
                        .antMatchers("/my-page/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .antMatchers("/my-page/admin/**").hasRole("ADMIN")

                        .and()
                        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class)
        );
    }

}
