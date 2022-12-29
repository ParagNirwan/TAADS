package com.team9.taads.api;

import com.team9.taads.configuration.PDFConfig;
import com.team9.taads.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class PDFController {

    private final PDFService pdfService;
    private final PDFConfig pdfConfig;
    @Autowired
    public PDFController(PDFService pdfService, PDFConfig pdfConfig){
        this.pdfService = pdfService;
        this.pdfConfig = pdfConfig;
    }

    @PostMapping
    @RequestMapping(path="/")
    public void writePDF(){
        pdfService.addText();
    }

    @GetMapping
    @RequestMapping(path = "/wholepdf")
    public String allText(){
        return pdfService.allText();
    }


    @GetMapping
    @RequestMapping(path="/mcw/{id}")
    public String mcw(@PathVariable("id") Long id){
        return pdfService.mcw(id);
    }


}
