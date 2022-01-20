package guru.sfg.brewery.config;

import guru.sfg.brewery.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Google2faFilter google2faFilter;

//    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
//        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(
//                new AntPathRequestMatcher("/api/**"));
//        filter.setAuthenticationManager(authenticationManager);
//
//        return filter;
//    }

    //Needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension(){
        return new SecurityEvaluationContextExtension();
    }

    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
//                UsernamePasswordAuthenticationFilter.class);
        http.csrf().ignoringAntMatchers("/h2-console/**", "/api/**");

        http.addFilterBefore(google2faFilter, SessionManagementFilter.class);

//        http.cors().and()
//            .authorizeRequests( authorize ->
//                authorize.antMatchers("/h2-console/**").permitAll()
//                .antMatchers("/", "/webjars/**", "/login", "/resources/**")
//            );

        http.cors().and().authorizeRequests(authorize ->
                    authorize
                        .antMatchers("/h2-console/**")
                            .permitAll()
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**")
                            .permitAll()
//                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**")
//                            .permitAll()
//                        .mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**")
//                            .hasRole("ADMIN")
//                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}")
//                            .permitAll()

//                        .mvcMatchers("/brewery/breweries")
//                            .hasAnyRole("ADMIN", "CUSTOMER")
//                        .mvcMatchers("/brewery/api/v1/breweries")
//                            .hasAnyRole("ADMIN", "CUSTOMER")

//                        .antMatchers("/beers/find", "/beers/{beerId}")
//                            .hasAnyRole("ADMIN", "CUSTOMER", "USER")
                )
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .formLogin( loginConfigurer ->
                    loginConfigurer.loginProcessingUrl("/login")
                            .loginPage("/").permitAll()
                            .successForwardUrl("/")
                            .defaultSuccessUrl("/")
                            .failureUrl("/?error"))
                .logout( logoutConfigurer ->
                    logoutConfigurer.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                            .logoutSuccessUrl("/?logout")
                            .permitAll())
                .httpBasic()
            .and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository)
                    .userDetailsService(userDetailsService);
//                .rememberMe()
//                    .key("sfg-key")
//                    .userDetailsService(userDetailsService);

        //h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.inMemoryAuthentication()
////                .withUser("simi")
////                .password("{bcrypt}$2a$10$dUeONGX5uTnblsOPugpoAu6UWMOmebZmUhi0NDxbAwnyTczMolMIS")
////                .roles("ADMIN")
////                .and()
////                .withUser("user")
////                .password("{sha256}afc39e1799f1db778eb292195b871079b5c2a999a9b996f5fdeb25d62eb988bbfe78579fe5796d9c")
////                .roles("USER");
//
//
//    }
}
