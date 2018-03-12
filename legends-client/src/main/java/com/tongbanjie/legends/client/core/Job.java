package com.tongbanjie.legends.client.core;

/**
 * 对外提供的接口，业务方需要实现该接口，并在execute()方法中实现相关业务逻辑代码
 * 
 * @author chen.jie
 * 
 */
public interface Job {

	/**
	 * 执行业务逻辑代码，返回的结果由业务方自定义
	 * <p>
	 * 注：如果业务上需要区分执行成功或失败的情况，则往外抛出异常即可。 例如：
	 * </p>
	 * 
	 * <pre>
	 * public String execute(String param) {
	 * 	boolean flag = true;
	 * 	if (flag) {
	 * 		throw new RuntimeException(&quot;false&quot;);
	 * 	} else {
	 * 		return &quot;success&quot;;
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @return String
	 */
	public String execute(String param);
}
