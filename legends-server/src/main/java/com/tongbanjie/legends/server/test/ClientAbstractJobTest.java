package com.tongbanjie.legends.server.test;

import com.tongbanjie.legends.client.core.AbstractJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 测试停止功能
 *
 * @author san.feng
 */
@Component
public class ClientAbstractJobTest extends AbstractJob {

	@Override
	public String execute(String param) {
		for (int i = 0; i < 100; i++) {
			if (!isTerminated()) {
				try {
					System.out.println("ClientAbstractJobTest executing .........[" + i + "]");
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				throw new RuntimeException("任务被终止！");
			}
		}
		return "success";
	}

}
