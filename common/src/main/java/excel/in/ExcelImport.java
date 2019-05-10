package excel.in;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ExcelImport {
	/**
	 * 解析第一个sheet
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <ModelType> List<ModelType> importSheet(String fileName, Class<ModelType> clazz) throws Exception {
		return importSheet(new FileInputStream(fileName), clazz);
	}

	/**
	 * 解析第一个sheet
	 * 
	 * @param in
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <ModelType> List<ModelType> importSheet(InputStream in, Class<ModelType> clazz) throws Exception {
		XSSFReader r = open(in);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = getXMLReader();
   		SheetHandler<ModelType> handler = getHandler(clazz, sst);
		parser.setContentHandler(handler);
		InputStream sheet1 = r.getSheet("rId1");
		InputSource sheetSource = new InputSource(sheet1);
		parser.parse(sheetSource);
		sheet1.close();
		return handler.getData();
	}

	/**
	 * 解析所有sheet
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <ModelType> List<ModelType> importSheets(String fileName,
			Class<ModelType> clazz) throws Exception {
		return importSheets(new FileInputStream(fileName), clazz);
	}

	/**
	 * 解析所有sheet
	 * 
	 * @param in
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <ModelType> List<ModelType> importSheets(InputStream in, Class<ModelType> clazz) throws Exception {
		XSSFReader r = open(in);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = getXMLReader();
		SheetHandler<ModelType> handler = getHandler(clazz, sst);
		parser.setContentHandler(handler);
		// 获取excel中所有sheet
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
		return handler.getData();
	}

	static XSSFReader open(InputStream in) throws Exception {
		OPCPackage pkg = OPCPackage.open(in);
		return new XSSFReader(pkg);
	}

	static XMLReader getXMLReader() throws SAXException {
		return XMLReaderFactory
				.createXMLReader("org.apache.xerces.parsers.SAXParser");
	}

	static <ModelType> SheetHandler<ModelType> getHandler(Class<ModelType> clazz, SharedStringsTable sst) throws SAXException {
			
		return new SheetHandler<ModelType>(clazz, sst);
	}

}
