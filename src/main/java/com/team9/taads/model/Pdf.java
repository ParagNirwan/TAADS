package com.team9.taads.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class Pdf {



    String pdfText;
    String pdfTextMain;
    File File;
    String moduleName ="agile software development";




    //constructors

    public Pdf(@JsonProperty("pdfText") String pdfText) {
        this.pdfText = pdfText;
    }
    public Pdf(String pdfText, String pdfTextMain, java.io.File file, String moduleName) {
        this.pdfText = pdfText;
        this.pdfTextMain = pdfTextMain;
        File = file;
        this.moduleName = moduleName;
    }




    //Getters and Setters
    public String getPdfText() {
        return pdfText;
    }

    public void setPdfText(String pdfText) {
        this.pdfText = pdfText;
    }

    public String getPdfTextMain() {
        return pdfTextMain;
    }

    public void setPdfTextMain(String pdfTextMain) {
        this.pdfTextMain = pdfTextMain;
    }

    public java.io.File getFile() {
        return File;
    }

    public void setFile(java.io.File file) {
        File = file;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


}
