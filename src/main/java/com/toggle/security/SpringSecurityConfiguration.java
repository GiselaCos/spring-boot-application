package com.toggle.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//Done by spring boot
//@EnableWebSecurity
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter{

    @Autowired
    private AuthenticationEntryPoint entryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //FIXME: put users in the DB and use encription
        auth.inMemoryAuthentication()
                .withUser("user").password("pass").roles("ADMIN");
        auth.inMemoryAuthentication()
                .withUser("Abc").password("app1").roles("APP");
        auth.inMemoryAuthentication()
                .withUser("Def").password("app2").roles("APP");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic()
            .authenticationEntryPoint(entryPoint);

        /*
        String[] pagesFree = {"/", "/home", "/template","/layout"};

        http
            .authorizeRequests()
                .antMatchers(pagesFree).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
         */

        /*
            auth
        .userDetailsService(new MyUserDetailsService(this.mongoTemplate))
        .passwordEncoder(new ShaPasswordEncoder(256));
         */
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }*/
}
