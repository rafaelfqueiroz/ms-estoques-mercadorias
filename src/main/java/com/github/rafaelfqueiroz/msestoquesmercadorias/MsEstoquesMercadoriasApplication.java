package com.github.rafaelfqueiroz.msestoquesmercadorias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {"com.github.rafaelfqueiroz.msestoquesmercadorias"}
)
public class MsEstoquesMercadoriasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsEstoquesMercadoriasApplication.class, args);
	}

}
