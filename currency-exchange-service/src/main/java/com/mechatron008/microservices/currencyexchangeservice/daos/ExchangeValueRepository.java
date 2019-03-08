package com.mechatron008.microservices.currencyexchangeservice.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mechatron008.microservices.currencyexchangeservice.entityclasses.ExchangeValue;

public interface ExchangeValueRepository extends JpaRepository<ExchangeValue, Long>{

	ExchangeValue findByFromAndTo(String from, String to);
}
