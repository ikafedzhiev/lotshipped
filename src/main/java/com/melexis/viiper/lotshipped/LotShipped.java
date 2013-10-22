package com.melexis.viiper.lotshipped;


import com.melexis.foundation.util.IO;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.SimpleExpression;


public class LotShipped extends RouteBuilder{
	
	
	private Processor PrepareQueryFinalLotsShipped = new Processor()	{
		@Override
		public void process(Exchange exchange) throws Exception {
			final Message in = exchange.getIn();    		
			in.setBody(in.getHeader("LOTNAME", String.class));
			final String query = IO.resourceAsString(LotShipped.class, "sql/lotshipped.sql");
			final String evaluated = (String) new SimpleExpression(query).evaluate(exchange);
			in.setBody(evaluated);
		}
	};	
	
	@Override
	public void configure() throws Exception {

		from("activemqewaf:topic:VirtualTopic.viiper.customerdeliveries")
			.log("GOT DELIVERY FROM EWAF with body: ${body},  id: ${id}, exchangeId: ${exchangeId}")
			.process(PrepareQueryFinalLotsShipped)
			.to("jdbc:viiper-ds")
			.split().body()
			.log("Got RESULT from crosscheck: ${body}")
			.to("activemq:topic:VirtualTopic.viiper.customerdeliveries.finallotsshipped");

	}

}
