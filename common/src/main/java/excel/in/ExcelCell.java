package excel.in;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCell {
	public enum TitleType {
		
		/** SIMPLE */
		SIMPLE, 
		
		/** MULTIPLE */
		MULTIPLE, 
		
		/** COMBINATION */
		COMBINATION,
		
		/** DYNAMIC_TEXT */
		DYNAMIC_TEXT
	}
	/**
	 * excel中的标题
	 * 
	 * @return
	 */
	String value() default "";
	
	/**
	 * 用来标记列的判断
	 * SIMPLE 为最后一列
	 * COMBINATION 为所有层的表头的组合 如  A-B-C-XXX, A-B-C-YYY  
	 * MULTIPLE 通过最后一行的表头来判断数据，但表头前几行必须保存到其他的变量
	 * @return
	 */
	TitleType titleType() default TitleType.SIMPLE;
	
	/**
	 * 额外的字段  使用json格式保存多个值
	 * [{'row1':'dd'},{'row2':'bb'}]
	 * @return
	 */
	String extFields() default "";

}
