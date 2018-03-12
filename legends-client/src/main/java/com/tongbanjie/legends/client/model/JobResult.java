package com.tongbanjie.legends.client.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 封装了job执行结果等相关信息，通过Response返回给server端
 * 
 * @author chen.jie
 * 
 */
public class JobResult implements Serializable {

	private static final long serialVersionUID = 1530198121024793393L;

	/**
	 * 对应job_detail表中的id
	 */
	private long jobDetailId;

	/**
	 * job执行成功还是失败，成功返回true，失败返回false
	 */
	private boolean isSuccess;

	/**
	 * job执行花费的时长，单位：秒
	 */
	private long timeConsume;

	/**
	 * 成功、失败，返回的结果
	 * <p>
	 * 注：这个结果由业务方自定义，会返回到控制台界面，方便查看
	 * </p>
	 */
	private String result;

	private Date actualStartTime; //任务线程真实开始运行时间

	private Date actualFinishTime; //任务线程真实开始运行时间

	public long getJobDetailId() {
		return jobDetailId;
	}

	public void setJobDetailId(long jobDetailId) {
		this.jobDetailId = jobDetailId;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public long getTimeConsume() {
		return timeConsume;
	}

	public void setTimeConsume(long timeConsume) {
		this.timeConsume = timeConsume;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public Date getActualFinishTime() {
		return actualFinishTime;
	}

	public void setActualFinishTime(Date actualFinishTime) {
		this.actualFinishTime = actualFinishTime;
	}
}
