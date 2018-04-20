package com.tongbanjie.legends.client.core;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.legends.client.enums.JobStatus;
import com.tongbanjie.legends.client.enums.MethodFlag;
import com.tongbanjie.legends.client.model.*;
import com.tongbanjie.legends.client.utils.AccessUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * JOB任务启动类(通过SERVLET)
 *
 * @author chen.jie
 * @author san.feng add STOP function
 */
public class JobExecutorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String CHAR_SET = "UTF-8";

	private static final String THREAD_NUM = "nThreads"; //执行任务的线程池的数量

	private ApplicationContext context;

	private ExecutorService pool;




	/**
	 * 存放正在执行中的job_id的队列
	 */
	private ConcurrentHashMap<Long, Future> executingQueue = new ConcurrentHashMap<Long, Future>();

	/**
	 * 存放已完成的job_id以及执行结果的队列
	 */
	private ConcurrentHashMap<Long, JobResult> finishedQueue = new ConcurrentHashMap<Long, JobResult>();

	/**
	 * 缓存执行的job
	 */
	private ConcurrentHashMap<String, Job> jobCache = new ConcurrentHashMap<String, Job>();


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		String nThreads = config.getInitParameter(THREAD_NUM);
		if (StringUtils.isBlank(nThreads)) {
			pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
		} else {
			pool = Executors.newFixedThreadPool(Integer.valueOf(nThreads));
		}
	}


	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response) throws ServletException, IOException {
		handleJob(request, response);
	}


	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response) throws ServletException, IOException {
		handleJob(request, response);
	}


	private void handleJob(HttpServletRequest request,
	                       HttpServletResponse response) throws IOException {

		/**if (!AccessUtils.isAllow(request)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			System.err.println("来访者IP【" + AccessUtils.extractIp(request) + "】，不在允许请求范围内，所以拒绝服务，可以修改 Servlet allow 配置解决。");
			return;
		}**/

		JobRequest jobRequest = getJobRequestFromHttpRequest(request);

		if (jobRequest == null) {
			Map<String, String> errorMap = new HashMap<String, String>(1);
			errorMap.put("errorMsg", "Http request needs [JobRequest] param.");
			writeResponse(response, errorMap);
			return;
		}

		MethodFlag flag = jobRequest.getMethodFlag();
		switch (flag) {
			case TEST: {
				JobTestResponse jobTestResponse = new JobTestResponse();

				String classFullPahth = jobRequest.getClassFullPath();
				if (StringUtils.isBlank(classFullPahth)) {
					jobTestResponse.setSuccess(false);
					jobTestResponse.setResult("Class full path is null.");
				} else {
					try {
						this.getJob(jobRequest.getClassFullPath());
						jobTestResponse.setSuccess(true);
						jobTestResponse.setResult("Test Success!");
					} catch (Exception e) {

						e.printStackTrace();

						jobTestResponse.setSuccess(false);
						jobTestResponse.setResult(e.getMessage());
					}
				}

				writeResponse(response, jobTestResponse);

				break;
			}
			case INVOKE: {
				JobInvokeResponse invokeResp = processInvokeRequest(jobRequest);
				writeResponse(response, invokeResp);

				break;
			}
			case EXECUTING: {
				JobExecutingResponse execResp = processExecRequest(jobRequest);
				writeResponse(response, execResp);

				break;
			}
			case STOP: {
				JobStopResponse stopResp = processStopRequest(jobRequest);
				writeResponse(response, stopResp);

				break;
			}
			default: {
				throw new IOException("Unknown Http Request.");
			}
		}
	}

	/**
	 * 处理server端invoke()方法的请求
	 */
	private synchronized JobInvokeResponse processInvokeRequest(JobRequest jobRequest) {
		JobInvokeResponse invokeResp = new JobInvokeResponse();
		try {
			Job job = getJob(jobRequest.getClassFullPath());
			Future<?> future = pool.submit(new Task(job, jobRequest));

			if (!executingQueue.containsKey(jobRequest.getJobDetailId())) {
				executingQueue.put(jobRequest.getJobDetailId(), future);
			}

			invokeResp.setInvokedSucc(true);
			return invokeResp;

		} catch (Exception e) {
			e.printStackTrace();

			invokeResp.setInvokedSucc(false);
			invokeResp.setErrorMsg(e.getMessage());
			return invokeResp;
		}

	}


	private JobStopResponse processStopRequest(JobRequest jobRequest) {
		JobStopResponse stopResp = new JobStopResponse();
		StringBuilder stopDetail = new StringBuilder();

		try {

			// 1. 如果Job继承了AbstractJob, 设置停止标志
			Job job = getJob(jobRequest.getClassFullPath());

			if (job instanceof AbstractJob) {
				AbstractJob absJob = (AbstractJob) job;
				absJob.stop();
				TimeUnit.SECONDS.sleep(3);
				stopDetail.append("任务正在停止..... 请稍后查看任务状态");
			} else {
				stopDetail.append("该任务没有继承AbstractJob接口, 不确一定能停掉这个任务.(注:只有任务存在sleep,wait等阻塞情况时, 才能停掉");
			}

			// 2. 通过 interrupt 尝试停止JOB线程
			Long jobDetailId = jobRequest.getJobDetailId();
			Future future = executingQueue.get(jobDetailId);
			if (future != null) {
				future.cancel(true);
			}

			stopResp.setStopNoticeSucc(true);
			stopResp.setStopDetail(stopDetail.toString());

		} catch (Throwable t) {

			t.printStackTrace();

			stopResp.setStopNoticeSucc(false);
			stopResp.setErrorMsg("执行停止任务发生异常, 异常信息:" + t.getClass().getName());
		}

		return stopResp;
	}


	/**
	 * 处理server端executing()方法的请求
	 */
	private JobExecutingResponse processExecRequest(JobRequest jobRequest) {
		JobExecutingResponse execResp = new JobExecutingResponse();
		long jobDetailId = jobRequest.getJobDetailId();

		// 先从正在执行的队列中检查，没有再去已完成的队列中检查
		if (executingQueue.containsKey(jobDetailId)) {
			execResp.setJobStatus(JobStatus.EXECUTING);
		} else if (finishedQueue.containsKey(jobDetailId)) {
			execResp.setJobStatus(JobStatus.FINISHED);
			execResp.setJobResult(finishedQueue.get(jobDetailId));
			finishedQueue.remove(jobDetailId);
		} else {
			execResp.setJobStatus(JobStatus.UNKNOW);
		}

		return execResp;
	}

	/**
	 * 先校验类名对应的bean是否存在，以及是否实现了{@link Job}}接口，再放到缓存中去
	 */
	private Job getJob(String classFullPath) throws Exception {
		Job job = jobCache.get(classFullPath);
		if (job == null) {
			Object obj = null;
			try {
				Class<?> clazz = Class.forName(classFullPath);
				obj = context.getBean(clazz);
			} catch (Exception e) {
				if (e instanceof ClassNotFoundException) {
					throw new Exception("[" + classFullPath + "] doesn't exists!");
				}
				if (e instanceof BeansException) {
					throw new Exception("Spring applicationContext doesn't contains [" + classFullPath + "] bean!");
				}
			}
			if (!(obj instanceof Job)) {
				throw new Exception(classFullPath + " doesn't implements [com.tongbanjie.legends.client.core.Job] interface!");
			}
			job = (Job) obj;
			jobCache.putIfAbsent(classFullPath, job);
		}
		return job;
	}

	/**
	 * 从httpRequest请求中获取JobRequest对象
	 */
	private JobRequest getJobRequestFromHttpRequest(HttpServletRequest request)
			throws IOException {
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String input;
		while ((input = reader.readLine()) != null) {
			sb.append(input);
		}
		String requestConent = sb.toString();
		JobRequest obj;
		try {
			obj = JSON.parseObject(requestConent, JobRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return obj;
	}


	/**
	 * 将结果返回给server端
	 */
	private void writeResponse(HttpServletResponse response, Object obj)
			throws IOException {
		ServletOutputStream sos = response.getOutputStream();
		String respBody = JSON.toJSONString(obj);
		try {
			sos.write(respBody.getBytes(CHAR_SET));
			sos.flush();
		} finally {
			sos.close();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		pool.shutdown();
	}

	/**
	 * 负责执行job.execute()方法，并且将jobDetail_id从executingQueue移到finishedQueue中
	 *
	 * @author chen.jie
	 */
	class Task implements Runnable {
		private Job job;
		private JobRequest jobRequest;

		public Task(Job job, JobRequest jobRequest) {
			this.job = job;
			this.jobRequest = jobRequest;
		}

		@Override
		public void run() {

			// 执行过程
			JobResult jobResult = new JobResult();
			jobResult.setJobDetailId(jobRequest.getJobDetailId());
			jobResult.setActualStartTime(new Date()); // 真正开始执行 Job 的时间，之前有可能在线程池中等待。

			long startTime = System.currentTimeMillis();
			try {
				String result = job.execute(jobRequest.getParam());
				jobResult.setSuccess(true);
				jobResult.setResult(result);
			} catch (Throwable e) {
				e.printStackTrace();
				jobResult.setSuccess(false);
				jobResult.setResult(e.getClass().getName() + ": " + e.getMessage());
			}

			jobResult.setActualFinishTime(new Date());
			long endTime = System.currentTimeMillis();
			jobResult.setTimeConsume((endTime - startTime) / 1000);

			// 执行完, 数据整理
			finishedQueue.put(jobRequest.getJobDetailId(), jobResult);
			executingQueue.remove(jobRequest.getJobDetailId());
			// Job停止后, 设置停止标志为启动
			if (this.job instanceof AbstractJob) {
				((AbstractJob) this.job).open();
			}
		}

	}
}
