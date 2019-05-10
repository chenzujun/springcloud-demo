package excel.in;


import org.apache.commons.beanutils.BeanUtils;


public class ImportExcelUtils {
	
	/**
	 * 设置导入实体 继承实体的值
	 * @param bean
	 * @param importLog
	 */
	public static void setEntityProperty(Object bean, ImpImportLog importLog){
		
		try {
			BeanUtils.setProperty(bean, "importNo", importLog.getImportNo());
			BeanUtils.setProperty(bean, "userId", importLog.getUserId());
			BeanUtils.setProperty(bean, "importDate", importLog.getImportDate());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
