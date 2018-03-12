package com.tongbanjie.legends.client.model;

import java.io.Serializable;

/**
 * 用于测试结果返回
 * 
 * @author chen.jie
 * 
 */
public class JobTestResponse implements Serializable {

	private static final long serialVersionUID = -1846413433523614639L;

	/**
	 * 成功true，失败false
	 */
	private boolean isSuccess;

	/**
	 * 成功、失败，返回的结果
	 */
	private String result;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
