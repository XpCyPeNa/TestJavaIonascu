package com.boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;

public class HomeController {

	@RequestMapping("/")
	public String home(){
		return "Dos Boot, reporting for duty";
	}
	
}
