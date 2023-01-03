package com.team9.taads.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table
public class PDFEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="moduleName")
    private String moduleName;
    @Column(name="moduleContent", length = 8192)
    private String moduleContent;

    @Column(name="electiveType")
    private String electiveType;

    @Column(name="oneGram")
    private String oneGram;

    @Column(name="twoGram")
    private String twoGram;

    @Column(name="threeGram")
    private String threeGram;

    @Column(name="linkOccupation")
    private String linkOccupation;

    @Column(name="linkSkill")
    private String linkSkill;



    public PDFEntity() {
    }

    //Constructors
    @Autowired
    public PDFEntity(String moduleName,String pdfTextMain) {
        this.moduleName = moduleName;
        this.moduleContent = pdfTextMain;
    }


    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleContent() {
        return moduleContent;
    }

    public void setModuleContent(String moduleContent) {
        this.moduleContent = moduleContent;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getElectiveType() {
        return electiveType;
    }

    public void setElectiveType(String electiveType) {
        this.electiveType = electiveType;
    }

    public String getOneGram() {
        return oneGram;
    }

    public void setOneGram(String oneGram) {
        this.oneGram = oneGram;
    }

    public String getTwoGram() {
        return twoGram;
    }

    public void setTwoGram(String twoGram) {
        this.twoGram = twoGram;
    }

    public String getThreeGram() {
        return threeGram;
    }

    public void setThreeGram(String threeGram) {
        this.threeGram = threeGram;
    }

    public String getLinkOccupation() {
        return linkOccupation;
    }

    public void setLinkOccupation(String linkOccupation) {
        this.linkOccupation = linkOccupation;
    }

    public String getLinkSkill() {
        return linkSkill;
    }

    public void setLinkSkill(String linkSkill) {
        this.linkSkill = linkSkill;
    }
//ToString Method


    @Override
    public String toString() {
        return "PDFEntity{" +
                "id=" + id +
                ", moduleName='" + moduleName + '\'' +
                ", moduleContent='" + moduleContent + '\'' +
                ", electiveType='" + electiveType + '\'' +
                ", oneGram='" + oneGram + '\'' +
                ", twoGram='" + twoGram + '\'' +
                ", threeGram='" + threeGram + '\'' +
                ", linkOccupation='" + linkOccupation + '\'' +
                ", linkSkill='" + linkSkill + '\'' +
                '}';
    }
}
