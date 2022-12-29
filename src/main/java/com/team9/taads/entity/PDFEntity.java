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
    @Column(name="pdfTextMain", length = 8192)
    private String pdfTextMain;

    public PDFEntity() {
    }

    //Constructors
    @Autowired
    public PDFEntity(String pdfTextMain) {
        this.pdfTextMain = pdfTextMain;
    }


    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPdfTextMain() {
        return pdfTextMain;
    }

    public void setPdfTextMain(String pdfTextMain) {
        this.pdfTextMain = pdfTextMain;
    }



    //ToString Method

    @Override
    public String toString() {
        return "PDFEntity{" +
                "id=" + id +
                ", pdfTextMain='" + pdfTextMain + '\'' +
                '}';
    }
}
