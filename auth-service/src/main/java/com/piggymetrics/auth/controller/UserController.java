package com.piggymetrics.auth.controller;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
    /**
	 * 其他系统会通过该接口获取用户信息，配置信息在Config项目share目录的application.yml配置文件中user-info-uri: http://localhost:5000/uaa/users/current
	 * 其他系统通过将OAuth2AuthenticationProcessingFilter加入SpringSecurity框架中，当请求进入系统后，该Filter会先请求用户信息，
	 * 然后将用户信息保存在SecurityContext中
	 * */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
	public Principal getUser(Principal principal) {
		return principal;
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(method = RequestMethod.POST)
	public void createUser(@Valid @RequestBody User user) {
		userService.create(user);
	}
}
