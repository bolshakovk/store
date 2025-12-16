package com.store.store;

import org.hibernate.Hibernate;
import org.hibernate.Version;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
        System.out.printf( Version.getVersionString());
		SpringApplication.run(StoreApplication.class, args);
	}

}
