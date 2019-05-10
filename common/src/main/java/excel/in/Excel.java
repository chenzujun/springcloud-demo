package excel.in;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {
	public enum Struct {
		
		/** ANNOTATION */
		ANNOTATION,
		
		/** XML */
		XML
	}

	/**
	 * 从多少行开始解析数据
	 * 
	 * @return
	 */
	int start() default 2;

	/**
	 * 解析到多少行结束（暂时没实现）
	 * 
	 * @return
	 */
	int end() default Integer.MAX_VALUE;

	/**
	 * 默认使用注解配置
	 * 
	 * @return
	 */
	Struct struct() default Struct.ANNOTATION;

}
