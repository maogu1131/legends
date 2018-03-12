package com.tongbanjie.legends.server.component;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.commons.lang.Result;
import com.tongbanjie.commons.util.HttpClientUtils;
import com.tongbanjie.legends.client.enums.MethodFlag;
import com.tongbanjie.legends.client.model.JobInvokeResponse;
import com.tongbanjie.legends.client.model.JobRequest;
import com.tongbanjie.legends.server.component.policy.InvokePolicy;
import com.tongbanjie.legends.server.component.policy.InvokePolicyFactory;
import com.tongbanjie.legends.server.dao.JobInfoDAO;
import com.tongbanjie.legends.server.dao.JobSnapshotDAO;
import com.tongbanjie.legends.server.dao.dataobject.JobInfo;
import com.tongbanjie.legends.server.dao.dataobject.JobSnapshot;
import com.tongbanjie.legends.server.dao.dataobject.enums.JobInfoInvokePolicyEnum;
import com.tongbanjie.legends.server.dao.dataobject.enums.JobInfoTypeEnum;
import com.tongbanjie.legends.server.dao.dataobject.enums.JobSnapshotStatusEnum;
import com.tongbanjie.legends.server.service.JobInfoService;
import com.tongbanjie.legends.server.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author sunyi
 *         Created on 15/11/4
 */
@Component
public class JobInvokeComponent {

	private Logger logger = LoggerFactory.getLogger(JobInvokeComponent.class);

	// 连接超时或响应超时时间
	protected static int INVOKE_TIME_OUT = 1 * 1000;

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Autowired
	private JobInfoService jobInfoService;

	@Autowired
	private JobSnapshotDAO jobSnapshotDAO;

	@Autowired
	private InvokePolicyFactory invokePolicyFactory;

	@Autowired
	private SmsService smsService;

	/**
	 * 如果在 invoke 阶段应用停机，可能导致任务不会被正常调用。
	 */
	public boolean invoke(Long jobInfoId, Long jobSnapshotId) throws Exception {

		JobInfo jobInfo = null;
		JobSnapshot jobSnapshot = null;

		try {
			jobInfo = jobInfoDAO.findById(jobInfoId);
			jobSnapshot = jobSnapshotDAO.findById(jobSnapshotId);

			// 进入 invoke 阶段.
			initToInvoke(jobSnapshot);

			String url = null;
			// 获取 InvokePolicy
			InvokePolicy jobExecutePolicy = getInvokePolicy(jobInfo);
			while (true) {
				try {
					url = jobExecutePolicy.getNextUrl();
					if (url == null) {
						// 所有的链接失败.
						jobFail("Invoking 阶段, 调用目标服务器全部失败, 任务结束.", jobInfo, jobSnapshot);
						return false;
					}

					// 调用目标服务器,启动任务.
					JobInvokeResponse invokeRes = invoke(url, jobInfo, jobSnapshot);

					if (invokeRes.isInvokedSucc()) {
						// 任务调用成功
						invokeToExecute(url, jobSnapshot);
						return true;
					} else {
						// 任务调用失败
						invokeFail(url, invokeRes.getErrorMsg(), jobSnapshot);
						continue;
					}
				} catch (Throwable e) {

					if (logger.isErrorEnabled()) {
						logger.error("Invoke " + url + " error! " + jobSnapshot.toString(), e);
					}

					// 任务调用时发生异常，失败。
					invokeFail(url, e.getMessage(), jobSnapshot);
					continue;
				}
			}
		} catch (Exception e) {
			if (jobInfo != null && jobInfo.getOwnerPhone() != null) {
				smsService.sendAlertSms(jobInfo.getOwnerPhone(), jobInfoId, jobInfo.getName(), jobSnapshotId, "任务Invoke阶段发生异常！");
			}
			throw e; // 因为会把异常抛出去， 这里就不重复记日志了
		}
	}

	/**
	 * 初始化成功后, 记录信息, 准备开始执行 invoke 阶段.
	 */
	protected void initToInvoke(JobSnapshot jobSnapshot) {

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setStatus(JobSnapshotStatusEnum.INVOKING);
		update.setDetail("准备调用目标服务器. " + getNowTime() + "\n");
		jobSnapshotDAO.updateByIdAndConcatDetail(update);
	}

	/**
	 * 获取调用策略.
	 */
	protected InvokePolicy getInvokePolicy(JobInfo jobInfo) {
		JobInfoInvokePolicyEnum policy = jobInfo.getInvokePolicy();
		String urls = jobInfo.getUrls();
		List<String> urlList = Arrays.asList(urls.split(","));
		return invokePolicyFactory.getJobExecutePolicy(policy, urlList);
	}

	private JobInvokeResponse invoke(String url, JobInfo jobInfo, JobSnapshot jobSnapshot) throws Exception {


		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setDetail("调用【" + url + "】中......\n");

		jobSnapshotDAO.updateByIdAndConcatDetail(update);

		JobRequest req = new JobRequest();
		req.setJobDetailId(jobSnapshot.getId());
		req.setMethodFlag(MethodFlag.INVOKE);
		req.setClassFullPath(jobInfo.getClassPath());
		req.setParam(jobInfo.getParam());
		String reqBody = JSON.toJSONString(req);
		String resBody = HttpClientUtils.post(url, reqBody, "application/json", "utf-8", INVOKE_TIME_OUT, INVOKE_TIME_OUT);

		JobInvokeResponse invokeRes = JSON.parseObject(resBody, JobInvokeResponse.class);
		return invokeRes;
	}

	private void invokeFail(String url, String errorMsg, JobSnapshot jobSnapshot) {

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setDetail("调用【" + url + "】失败, " + getNowTime() + "\n" + "errorMsg: " + errorMsg + "\n");

		jobSnapshotDAO.updateByIdAndConcatDetail(update);
	}

	protected void invokeToExecute(String url, JobSnapshot jobSnapshot) {
		String ip = getIpFromUrl(url);

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setUrl(url);
		update.setIp(ip);
		update.setStatus(JobSnapshotStatusEnum.EXECUTING);
		update.setDetail("调用【" + url + "】成功, 开始执行任务. " + getNowTime() + "\n");

		jobSnapshotDAO.updateByIdAndConcatDetail(update);
	}

	/**
	 * 任务失败时.
	 */
	protected void jobFail(String errorMessage, JobInfo jobInfo, JobSnapshot jobSnapshot) {

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setResult(errorMessage);
		update.setStatus(JobSnapshotStatusEnum.ERROR);
		update.setTimeConsume(0L);
		update.setDetail("任务失败：" + errorMessage + getNowTime() + "\n");
		jobSnapshotDAO.updateByIdAndConcatDetail(update);

		JobInfoTypeEnum type = jobInfo.getType();

		if (type.equals(JobInfoTypeEnum.ONCE)) {
			Result<Boolean> result = jobInfoService.deleteJobInfoById(jobInfo.getId());
			if (!result.isSuccess()) {
				logger.error("jobInfoService.deleteById fail! " + result.getErrorMsg());
			}
		}

		if (jobInfo != null && jobInfo.getOwnerPhone() != null) {
			smsService.sendAlertSms(jobInfo.getOwnerPhone(), jobInfo.getId(), jobInfo.getName(), jobSnapshot.getId(), errorMessage);
		}

	}

	/**
	 * 获取现在的时间 yyyy-MM-dd HH:mm:ss
	 */
	private String getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 从URL里提取IP或域名.
	 */
	private String getIpFromUrl(String url) {
		url = url.substring(url.indexOf("//") + 2);
		url = url.substring(0, url.indexOf("/"));
		if (url.contains(":"))
			return url.substring(0, url.indexOf(":"));
		return url;
	}
}
