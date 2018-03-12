package com.tongbanjie.legends.client.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 服务端调用STOP请求返回的结果 </p>
 * 
 * @author san.feng
 * 
 * 可以分布式任务调度, 通过资源组, 负载均衡来做, 没时间啊
 * 
 */
public class JobStopResponse implements Serializable {

	private static final long serialVersionUID = -314032714717934075L;

	/**
	 * 是否停止通知成功，成功返回true，失败返回false
	 */
	private boolean isStopNoticeSucc;
	
	/**
	 * 停止内容详情
	 */
	private String stopDetail;

	/**
	 * stop失败返回的错误信息
	 */
	private String errorMsg;

	public boolean isStopNoticeSucc() {
		return isStopNoticeSucc;
	}

	public void setStopNoticeSucc(boolean isStopNoticeSucc) {
		this.isStopNoticeSucc = isStopNoticeSucc;
	}

	public String getStopDetail() {
		return stopDetail;
	}

	public void setStopDetail(String stopDetail) {
		this.stopDetail = stopDetail;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
