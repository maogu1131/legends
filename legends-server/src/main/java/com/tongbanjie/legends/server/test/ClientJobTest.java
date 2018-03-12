package com.tongbanjie.legends.server.test;

import org.springframework.stereotype.Component;

import com.tongbanjie.legends.client.core.AbstractJob;

import java.util.concurrent.TimeUnit;

@Component
public class ClientJobTest extends AbstractJob {

	@Override
	public String execute(String param) {
		System.out.println("param: " + param);

		try {
			TimeUnit.SECONDS.sleep(10L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return "success";

	}

}
