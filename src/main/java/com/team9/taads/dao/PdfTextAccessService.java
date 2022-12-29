package com.team9.taads.dao;

import com.team9.taads.model.Pdf;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


@Repository
public class PdfTextAccessService implements PdfDAO{

    Pdf pdf;
    public static String pdfText;
    public static String pdfTextMain;
    File file;
    String moduleName;
    String moduleContent;
    String moduleContentFinal = "";


    @Override
    public String insertPdfText(String pdfText) {
        pdfText = String.valueOf(new Pdf(pdfText));
        return pdfText;
    }

    @Override
    public String allText() {
        try{
            file = new File("E:\\PDF Test\\IMACS.pdf");
            pdfText = new Tika().parseToString(file);
            pdfTextMain = pdfText.substring(pdfText.indexOf("Module Name Agile Software Development"));


        } catch (TikaException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pdfTextMain;
    }

    @Override
    public String oneModule() {
        System.out.println(pdfTextMain);
        moduleContent = pdfTextMain.substring(pdfTextMain.indexOf(moduleName));

        try {
            moduleContentFinal = moduleContent.substring(moduleContent.indexOf(moduleName), moduleContent.indexOf("name"));
        } catch (StringIndexOutOfBoundsException e) {

        } finally {
            moduleContentFinal = moduleContent.substring(moduleContent.indexOf(moduleName), moduleContent.indexOf("applied"));
        }

        return moduleContentFinal;
    }


}
