package excel.in;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import enums.ElementType;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("unused")
public class SheetHandler<ModelType> extends DefaultHandler {

	/** 开多少行才开始算有用数据	*/
	private int start = 2;
	/** 当前读取第几行 	*/
	private int line = 0;

	private Map<Integer, String> row;
	private Map<Integer, String> oldRow;
	
	/** 单元格的值 	 */
	private String lastContents;
	
	/** 解析后返回的model	*/
	private List<ModelType> dataList;
	private SharedStringsTable sst;

	private String cellS;
	
	/** 记录excel里面的类型 	*/
	private String cellType; 

	private Map<Integer, String> titleMap = new HashMap<Integer, String>();
	private Map<Integer, String> titleComplexMap = new HashMap<Integer, String>();
	private Map<Integer, String> titleSimpleMap = new HashMap<Integer, String>();

	private Map<Integer, Object> fieldsMap = new HashMap<Integer, Object>();

	private Map<String, Map<String, String>> newTempRow = new HashMap<String, Map<String, String>>();

	private String t = "";
	private ModelType modelType;
	private Class<ModelType> clazz;
	private Integer curCell = 0;
	private String tempValue = "";
	private boolean valueIsNull;
	
	/** 判断是否需要保存 	*/
	private boolean valueFlag; 
	
	/** 保存当前的内容 	*/
	private String value; 

	private int thisColumn = -1;

	public SheetHandler(Class<ModelType> clazz, SharedStringsTable sst) {
		this.dataList = Lists.newArrayList();
		this.clazz = clazz;
		this.sst = sst;

		lastContents = "";
		start = getStart();

	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (ElementType.row.toString().equals(name)) {
			row = new HashMap<Integer, String>(16, 0.75f);
			titleMap = new HashMap<Integer, String>(16, 0.75f);
			curCell = 0;
			tempValue = "";
			line++;
		} else if (ElementType.c.toString().equals(name)) { 
			cellS = attributes.getValue("s");
			cellType = attributes.getValue("t");
			valueIsNull = true;

			// 获取单元格列顺序号(按照A,B,C绑定列号)
			String r = attributes.getValue("r");
			int firstDigit = -1;
			for (int c = 0; c < r.length(); ++c) {
				if (Character.isDigit(r.charAt(c))) {
					firstDigit = c;
					break;
				}
			}
			thisColumn = nameToColumn(r.substring(0, firstDigit));

		} else if (ElementType.v.toString().equals(name)) {
			valueFlag = true;
			valueIsNull = false;
			lastContents = "";
		}
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public void endElement(String uri, String localName, String name) throws SAXException {

		String thisStr = null;

		if (ElementType.row.toString().equals(name)) {
			
		    if (line >= start){
		    	
		    	boolean emptyLine = true;
				for (Entry<Integer, String> entry : row.entrySet()) {
					if (!StringUtils.isBlank(entry.getValue())){
						emptyLine = false;
						break;
					}
				}
				
			    if(emptyLine){
			    	return;
			    }
				
				oldRow = new HashMap<Integer, String>(16, 0.75f);
				oldRow.putAll(row);
				Set<Integer> keySet = row.keySet();
				try {
					modelType = clazz.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (Integer key : keySet) {
					// 获取当前的值
					String tempVal = row.get(key); 
					// 处理内容里面有上下合拼的情况
					if (null == tempVal || tempVal.equals("")) { 
						tempVal = oldRow.get(key);
					}
					Object fields = fieldsMap.get(key);
					// 第一次处理
					if (fields == null) { 
						Object obj = getField(clazz, key, tempVal);
						// 记录第几行importNo
						try {
							Method method = modelType.getClass().getMethod("setImportNo", int.class);
							method.invoke(modelType, (line - start + 1));
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (obj instanceof String) {
							// 是普通值，直接放到eo里面
							String setMethodName = "set" + obj.toString().substring(0, 1).toUpperCase()
									+ obj.toString().substring(1);
							setData(obj.toString(), setMethodName, tempVal, modelType);
						}
					}
				}
				// 重新整理数据
				if (!newTempRow.isEmpty()) {
					for (Entry<String, Map<String, String>> m : newTempRow.entrySet()) {
						ModelType mt = (ModelType) copy(modelType);
						Map<String, String> mp = m.getValue();
						for (Entry<String, String> e : mp.entrySet()) {
							String setMethodName = "set" + e.getKey().substring(0, 1).toUpperCase()
									+ e.getKey().substring(1);
							setData(e.getKey(), setMethodName, e.getValue(), mt);
						}
						dataList.add(mt);
					}
				} else {
					dataList.add(modelType);
				}
			}

			newTempRow = new HashMap<String, Map<String, String>>(16, 0.75f);
			t = "";
		} else if (ElementType.v.toString().equals(name)) {
			 // 处理表头
			if (isTitleLine()) {
				t = convertCellValue();
				titleMap.put(curCell, t);
				titleSimpleMap.put(curCell, t);

				String temp = titleComplexMap.get(curCell) == null ? "" : titleComplexMap.get(curCell) + "-";
				titleComplexMap.put(curCell, temp + t);
			} else {

				tempValue = convertCellValue();
				row.put(thisColumn, tempValue);
			}
			valueFlag = false;
			
		} else if (ElementType.c.toString().equals(name)) {
			if (isTitleLine()) {
				if (valueIsNull && !"".equals(t)) {
					titleMap.put(curCell, t);
					titleSimpleMap.put(curCell, t);
					String temp = titleComplexMap.get(curCell) == null ? "" : titleComplexMap.get(curCell) + "-";
					titleComplexMap.put(curCell, temp + t);
				}
			} else {
				if (valueIsNull) {
					row.put(curCell, "");
				}
			}
			valueIsNull = false;
			curCell++;
		}
	}

	public void setValue(String xclass, ModelType tObject, Method setMethod, String value) throws Exception {
		
		String classType1 = "class java.lang.String";
		String classType2 = "class java.util.Date";
		String classType3 = "class java.lang.Boolean";
		String classType4 = "class java.lang.Short";
		String classType5 = "class java.lang.Integer";
		String classType6 = "class java.lang.Long";
		String classType7 = "class java.lang.Double";
		String classType8 = "class java.math.BigDecimal";
		String classType9 = "int";
		
		if (classType1.equals(xclass)) {
			setMethod.invoke(tObject, value);
		} else if (classType2.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : DateUtil.getJavaDate(new Double(value), false));
		} else if (classType3.equals(xclass)) {
			Boolean boolname = true;
			String booleanValue = "否";
			if (booleanValue.equals(value)) {
				boolname = false;
			}
			setMethod.invoke(tObject, boolname);
		} else if (classType4.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : new Short(value));
		} else if (classType5.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : new Integer(value));
		} else if (classType6.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : new Long(value));
		} else if (classType7.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : new Double(value));
		} else if (classType8.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? null : new BigDecimal(value).setScale(6, BigDecimal.ROUND_HALF_UP));
		} else if (classType9.equals(xclass)) {
			setMethod.invoke(tObject, StringUtils.isBlank(value) ? 0 : Integer.parseInt(value));
		} else {
			setMethod.invoke(tObject, value);
		}
	}

	public boolean isTitleLine() {
		return line < start;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		lastContents += new String(ch, start, length);

	}

	private int nameToColumn(String name) {
		int column = -1;
		for (int i = 0; i < name.length(); ++i) {
			int c = name.charAt(i);
			column = (column + 1) * 26 + c - 'A';
		}
		return column;
	}

	public List<ModelType> getData() {
		return dataList;
	}

	private String convertCellValue() {
		String tmp = lastContents.toString();
		Object result = tmp;
		// 字符串
		if (ElementType.s.toString().equals(cellType)) {
			Integer key = Integer.parseInt(tmp);
			result = new XSSFRichTextString(sst.getEntryAt(key)).toString().trim();
		}
		return result.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getField(Class cla, int num, String val) {

		Field[] declaredFields = cla.getDeclaredFields();

		for (Field filed : declaredFields) {

			ExcelCell annotation = filed.getAnnotation(ExcelCell.class);

			if (annotation == null) {
				continue;
			}

			// 简单-表头内容固定
			if (annotation.titleType().equals(ExcelCell.TitleType.SIMPLE)) {
				String tempTitle = titleSimpleMap.get(num);
				if (null != tempTitle && tempTitle.equals(annotation.value()) && !tempTitle.equals("合计")) {
					return filed.getName();
				}
			}

			// 多列-表头内容固定
			else if (annotation.titleType().equals(ExcelCell.TitleType.MULTIPLE)) {
				String tempTitle = titleComplexMap.get(num);
				if (null != tempTitle && tempTitle.equals(annotation.value())) {
					return filed.getName();
				}
			}

			// 动态列-最后一行表头内容固定，其他行内容可变
			else if (annotation.titleType().equals(ExcelCell.TitleType.COMBINATION)) {
				String tempTitle = titleSimpleMap.get(num);
				String cTitle = titleComplexMap.get(num);
				if (null != tempTitle && tempTitle.equals(annotation.value()) && cTitle.indexOf("合计") == -1) {

					String[] extFields = null;
					try {
						extFields = JSON.parseObject(annotation.extFields(), String[].class);
					} catch (Exception e) {
						e.printStackTrace();
					}

					return getFields(num, extFields, filed, val);

				}
			}

			// 动态列-表头内容可变
			else if (annotation.titleType().equals(ExcelCell.TitleType.DYNAMIC_TEXT)) {

				String tempTitle = titleSimpleMap.get(num);
				String cTitle = titleComplexMap.get(num);

				String first = (filed.getName() + "_dynamicText");
				String getMethodName = "get" + first.substring(0, 1).toUpperCase() + first.substring(1);
				Method getMethod;
				Object lastHeadText = null;
				try {
					getMethod = cla.getDeclaredMethod(getMethodName, new Class[] {});
					lastHeadText = getMethod.invoke(modelType, new Class[] {});
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				if (null != tempTitle && tempTitle.equals(lastHeadText) && cTitle.indexOf("合计") == -1) {

					String[] extFields = null;
					try {
						extFields = JSON.parseObject(annotation.extFields(), String[].class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return getFields(num, extFields, filed, val);
				}
			}
		}

		return null;
	}

	private Object getFields(int num, String[] extFields, Field field, String val) {
		String tm = titleComplexMap.get(num).substring(0, titleComplexMap.get(num).lastIndexOf("-"));
		Map<String, String> m = null;
		if (newTempRow.get(tm) != null) {
			m = newTempRow.get(tm);
		} else {
			m = new HashMap<>(10);
		}
		try {
			int r = 0;
			for (String s : extFields) {
				// field字段对应的excel中文
				m.put(s, titleComplexMap.get(num).split("-")[r]);
				r++;
			}
			m.put(field.getName(), val);
			newTempRow.put(tm, m);
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object copy(Object resource) {
		try {
			Class<? extends Object> classType = resource.getClass();
			Object newObject = classType.newInstance();
			Field[] declaredFields = classType.getDeclaredFields();
			for (Field filed : declaredFields) {
				if (filed.getName().equals("serialVersionUID")) {
					continue;
				}
				String firstLetter = filed.getName().substring(0, 1).toUpperCase();
				String getMethodName = "get" + firstLetter + filed.getName().substring(1);
				String setMethodName = "set" + firstLetter + filed.getName().substring(1);
				Method getMethod = classType.getMethod(getMethodName, new Class[] {});
				Method setMethod = classType.getMethod(setMethodName, new Class[] { filed.getType() });
				Object value = getMethod.invoke(resource, new Object[] {});
				setMethod.invoke(newObject, new Object[] { value });
			}
			return newObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void setData(String fieldName, String setMethodName, String lastContents, ModelType obj) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Method setMethod = clazz.getMethod(setMethodName, new Class[] { field.getType() });

			Type[] ts = setMethod.getGenericParameterTypes();
			String xclass = ts[0].toString();
			setValue(xclass, obj, setMethod, lastContents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getStart() {
		Excel annotation = clazz.getAnnotation(Excel.class);
		if (annotation != null) {
			return annotation.start();
		}
		return 2;
	}
}
