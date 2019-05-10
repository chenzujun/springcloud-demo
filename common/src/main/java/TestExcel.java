import Bean.User;
import excel.in.ExcelImport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author chenjun
 * @date 2019/5/9
 * @since V1.0.0
 */
public class TestExcel {

    public static void testReadExcel(String fileName) throws Exception {
        InputStream fileInputStream = new FileInputStream(new File(fileName));
        List<User> list = ExcelImport.importSheet(fileInputStream, User.class);
        System.out.println(list.toString());
    }

    public static void testReadExcels(String fileName) throws Exception {
        InputStream fileInputStream = new FileInputStream(new File(fileName));
        List<User> list = ExcelImport.importSheets(fileInputStream, User.class);
        System.out.println(list.toString());
    }

    public static void main(String[] args) throws Exception {
        testReadExcel("C:\\Users\\chenjun01\\Desktop\\test.xlsx");
    }
}