package com.facturacionAutonomo.api_gateway.controller;

import java.time.Duration;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class HelloWorlController {
	
	@GetMapping("/hola")
	public Flux<String> helloWorldHandler(){
		
		List<String> listaPalabras = List.of("Hola", "Mundo", "Tic");
	
			Flux<String> publisher = Flux.fromIterable(listaPalabras).delayElements(Duration.ofSeconds(2)).log()
					.onErrorResume(e-> Flux.just("error:" + e.getMessage()));
			
			return publisher;
		
	}

}
