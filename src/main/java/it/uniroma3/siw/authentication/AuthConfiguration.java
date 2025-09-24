package it.uniroma3.siw.authentication;

import it.uniroma3.siw.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.jdbcAuthentication()
	            .dataSource(dataSource)
	            // MODIFICA SOLO LA RIGA SEGUENTE
	            .authoritiesByUsernameQuery("SELECT email, CONCAT('ROLE_', role) from credentials WHERE email=?")
	            .usersByUsernameQuery("SELECT email, password, 1 as enabled FROM credentials WHERE email=?");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorizeRequests ->
						authorizeRequests
								// 1. Pagine e risorse pubbliche (accessibili a TUTTI)
								.requestMatchers("/", "/index", "/register", "/login", "/css/**", "/images/**", "/videos/**", "favicon.ico").permitAll()
								.requestMatchers("/products", "/product/**", "/type/**", "/types/**", "/search").permitAll()
								
								// 2. Pagine riservate solo agli ADMIN
								.requestMatchers("/admin/**").hasRole(Credentials.ADMIN_ROLE)
								
								// 3. Tutte le altre pagine richiedono l'autenticazione
								.anyRequest().authenticated()
				)
				.formLogin(formLogin ->
						formLogin
								.loginPage("/login")
								.permitAll()
								.defaultSuccessUrl("/profile", true)
								.failureUrl("/login?error=true")
				)
				.logout(logout ->
						logout
								.logoutUrl("/logout")
								.logoutSuccessUrl("/")
								.invalidateHttpSession(true)
								.deleteCookies("JSESSIONID")
								.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
								.clearAuthentication(true).permitAll()
				);

		return http.build();
	}
}