package com.piggymetrics.auth;

import com.piggymetrics.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@SpringBootApplication
@EnableResourceServer
//@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Configuration
	@EnableWebSecurity
	protected static class webSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private MongoUserDetailsService userDetailsService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.csrf().disable();
			// @formatter:on
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService)
					.passwordEncoder(new BCryptPasswordEncoder());
		}

		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	}

	@Bean
	public RedisConnectionFactory connectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	public TokenStore redisTokenStore() {
		RedisTokenStore redis = new RedisTokenStore(connectionFactory());
		return redis;
	}
//
//
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
        //DefaultTokenService保存token相关信息和用户相关信息
//		private TokenStore tokenStore = new InMemoryTokenStore();
		@Autowired
		@Qualifier("redisTokenStore")
		private TokenStore tokenStore;
        //OAuth2AuthenticationProcessingFilter调用AuthenticationManager做身份认证，默认是OAuth2AuthenticationManager
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
        //获取用户信息
		@Autowired
		private MongoUserDetailsService userDetailsService;

		@Autowired
		private Environment env;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

			// TODO persist clients details
            //下面这些配置信息，会存在于InMemoryClientDetailsService类中
			// @formatter:off
			clients.inMemory()
					.withClient("browser")
					.authorizedGrantTypes("refresh_token", "password")
					.scopes("ui")
			.and()
					.withClient("account-service")
					.secret(env.getProperty("ACCOUNT_SERVICE_PASSWORD"))
					.authorizedGrantTypes("client_credentials", "refresh_token")
					.scopes("server")
			.and()
					.withClient("statistics-service")
					.secret(env.getProperty("STATISTICS_SERVICE_PASSWORD"))
					.authorizedGrantTypes("client_credentials", "refresh_token")
					.scopes("server")
			.and()
					.withClient("notification-service")
					.secret(env.getProperty("NOTIFICATION_SERVICE_PASSWORD"))
					.authorizedGrantTypes("client_credentials", "refresh_token")
					.scopes("server");
			// @formatter:on
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints
					.tokenStore(tokenStore)
					.authenticationManager(authenticationManager)
					.userDetailsService(userDetailsService);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer
					.tokenKeyAccess("permitAll()")
					.checkTokenAccess("isAuthenticated()");
		}
	}
}
