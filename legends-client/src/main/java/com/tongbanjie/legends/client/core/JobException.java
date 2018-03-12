/**
 * 
 */
package com.tongbanjie.legends.client.core;

/** 
* @ClassName: JobException 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author hou.qingqing@tongbanjie.com 
* @date 2015年11月24日 下午1:58:43 
*  
*/
public class JobException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -724108424065656190L;

	public JobException() {
		super();
	}

	public JobException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobException(String s) {
		super(s);
	}

	public JobException(Throwable cause) {
		super(cause);
	}
	
}
