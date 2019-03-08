package com.mechatron008.microservices.currencyconversionservice.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.mechatron008.microservices.currencyconversions.entityclasses.CurrencyConversionBean;
import com.mechatron008.microservices.currencyconversionservice.externalservices.CurrencyExchangeServiceProxy;

@RestController
@EnableFeignClients("com.mechatron008.microservices.currencyconversionservice.externalservices")
public class CurrencyConversionController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CurrencyExchangeServiceProxy currencyExchangeService;

	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		
		// We can use Feign here to avoid this lengthy service invocation
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate()
				.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
						CurrencyConversionBean.class, 
						uriVariables);
		
		CurrencyConversionBean response = responseEntity.getBody();
		
		return new CurrencyConversionBean(response.getId(), 
				from, 
				to, 
				response.getConversionRate(), 
				quantity, 
				quantity.multiply(response.getConversionRate()), 
				response.getPort());
	}
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean response = currencyExchangeService.retrieveExchangeValue(from, to);
		
		logger.info("{}", response);
		
		return new CurrencyConversionBean(response.getId(), 
				from, 
				to, 
				response.getConversionRate(), 
				quantity, 
				quantity.multiply(response.getConversionRate()), 
				response.getPort());
	}
	
}
