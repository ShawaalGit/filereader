package com.file.readwrite.service;

import com.file.readwrite.entity.XmlData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class FileService {

    /**
     * ReadFile method reads specified data from the xml(only) in the given directory
     **/
    public List<XmlData> readFile(String mainDirPath) throws ParserConfigurationException, IOException, SAXException {
        List<File> files;
        /**
         * With the help of FileFilterUtils we specify the suffix as "XML Document (.xml)"
         * only files with this suffix will be read
         **/
        IOFileFilter notInfoFilter = FileFilterUtils
                .notFileFilter(FileFilterUtils.suffixFileFilter("XML Document (.xml)", IOCase.SYSTEM));
        files = new ArrayList<>(FileUtils.listFiles(new File(mainDirPath), notInfoFilter, TrueFileFilter.INSTANCE));
        /**Here we are using linkedlist as we dont know
         * how many files and how many entries we are going to make in the list**/
        List<XmlData> xmlDataList = new LinkedList<>();

        for (File file : files) {
            String fileName = file.getName();
            File xmlFile = new File(file.getParent() + "/" + fileName);
            if (xmlFile.isFile()) {
                /**
                 * As the xml had a Doctype type to ignore it we used DocumentBuilderFactory and set feature
                 * then document is used to parse the xml and read data from it.
                 **/
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(new File(xmlFile.toURI()));
                String emailId = document.getElementsByTagName("IPV_SCETV_MD")
                        .item(0)
                        .getAttributes()
                        .getNamedItem("CURATOR_PUSH_EMAIL_ADDRESS")
                        .getNodeValue();
                String department = document.getElementsByTagName("SCETV_DEPARTMENT_TMD")
                        .item(0)
                        .getAttributes()
                        .getNamedItem("DEPARTMENT")
                        .getNodeValue();
                XmlData xmlData = new XmlData(fileName, emailId, department);
                xmlDataList.add(xmlData);
            }
        }
        return xmlDataList;
    }

    /**
     * An excel sheet is generated is created with the data that was extracted from the xml
     * file is generated in the main directory of the project.
     **/
    public void createExcel(List<XmlData> fileList, String path) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("File List");
        int headerRow = 0;
        int rowNum = 1;
        for (XmlData xmlData : fileList) {
            Row row = null;

            row = sheet.createRow(headerRow);
            Cell DepartmentCell = row.createCell(0);
            DepartmentCell.setCellValue("Department");
            Cell EmailCell = row.createCell(1);
            EmailCell.setCellValue("Email");

            row = sheet.createRow(rowNum++);

            Cell departmentCell = row.createCell(0);
            departmentCell.setCellValue(xmlData.getDepartment());

            Cell metadataCell = row.createCell(1);
            metadataCell.setCellValue(xmlData.getEmailId());
        }

        try (FileOutputStream outputStream = new FileOutputStream(path + "//" + "FileList.xlsx")) {

            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
