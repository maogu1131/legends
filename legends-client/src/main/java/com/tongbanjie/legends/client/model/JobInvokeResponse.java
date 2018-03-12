package com.tongbanjie.legends.client.model;

import java.io.Serializable;

/**
 * 封装了请求执行结果等相关参数的PO
 * <p>
 * 注：这个response是在server端调用invoke()方法的时候返回的结果
 * </p>
 * 
 * @author chen.jie
 * 
 */
public class JobInvokeResponse implements Serializable {

	private static final long serialVersionUID = -5873493554044978371L;

	/**
	 * 是否invoke成功，成功返回true，失败返回false
	 */
	private boolean isInvokedSucc;

	/**
	 * invoke失败返回的错误信息
	 */
	private String errorMsg;

	public boolean isInvokedSucc() {
		return isInvokedSucc;
	}

	public void setInvokedSucc(boolean isInvokedSucc) {
		this.isInvokedSucc = isInvokedSucc;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
