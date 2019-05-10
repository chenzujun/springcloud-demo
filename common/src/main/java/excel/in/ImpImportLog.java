package excel.in;

import java.io.Serializable;


public class ImpImportLog implements Serializable {

	private static final long serialVersionUID = 4904846009574413670L;


	/**
	 * 导入序号
	 */
	private int importNo;

	/**
	 * 导入操作人
	 */
	private String userId;

	/**
	 * 导入时间
	 */
	private String importDate;



	public int getImportNo() {
		return importNo;
	}

	public void setImportNo(int importNo) {
		this.importNo = importNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

}
