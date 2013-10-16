package com.melexis.viiper.lotshipped;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
//import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.spring.Main;


public class LotShipped extends RouteBuilder{
	
	private String query;  
	
	public LotShipped () throws IOException {
		final InputStream is = getClass().getClassLoader().getResourceAsStream("sql/lotshipped.sql");
		byte[] buf = new byte[is.available()];
		is.read(buf);
		query = new String (buf);
	} 
	
	
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		from("timer://kickoff?period=10s")
		.log("Viiper select")
		.setBody(simple(query))
		.to("jdbc://viiper-ds")
		.split().body()
		.log("Got body: ${body}")
		.to("activemq:topic:VirtualTopic.viiper_lotshipped");
		
		from("activemq:Consumer.Sofia.VirtualTopic.viiper_lotshipped")		
		.log("Got it.....SOFIA");
		
		from ("activemq:Consumer.Ieper.VirtualTopic.viiper_lotshipped")		
		.log("Got it.....IEPER");
	}


}
