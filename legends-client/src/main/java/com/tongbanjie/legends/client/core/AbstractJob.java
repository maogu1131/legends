package com.tongbanjie.legends.client.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 附带停止标志的JOB, 子类继承AbstractJob, 通过isTerminated(), 来判断是否停止任务的执行<br/>
 *
 * <ol>
 *     <li>通过标志isTerminated()停止任务(需要任务开发者, 在任务中判断)</li>
 *     <li>如果任务线程处于阻塞中如sleep,wait,io等, 系统会停止该任务</li>
 * </ol>
 *
 * 之前把中断任务这个功能想的太简单了，目前这种实现有一定问题存在，但对于不会同时执行的任务，还是没问题的。
 *<ul>
 *      <li>如果使用 Thread.interceptor 方式，业务实现必须使用 Sleep 等，且 InterruptedException 还可能会被 catch 掉，起不到作用</li>
 *      <li>如果使用一个变量作为控制标示，因某个Job多次调用执行时，使用的是同一个对象，会对这Job的其他执行情况造成影响</li>
 *      <li>如果使用 ThreadLocal 保存一个变量作为控制标示，但外部线程无法改变任务线程的内容，且因使用线程池的原因，对后续任务执行可能会造成影响</li>
 *<ul/>
 * @author san.feng
 * 
 *
 * 注意: 因为是单例模式-所以停止的是JOB下的所有线程哈.<br>
 * 
 * 我知道的, 就这么多了.<br>
 * 
 */
public abstract class AbstractJob implements Job{

	
	/**
	 * 任务终止标志 
	 */
	private AtomicBoolean terminated = new AtomicBoolean(false);


	/**
	 * 判断是否继续执行任务
	 */
	public boolean isTerminated() {
		return this.terminated.get();
	}
	

	@Override
	public abstract String execute(String param);
	

	/**
	 * 停止任务操作
	 */
	public void stop() {
		this.terminated.set(true);
	}
	
	/**
	 * 停止任务操作
	 */
	public void open() {
		this.terminated.set(false);
	}
}
