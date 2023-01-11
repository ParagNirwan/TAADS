package com.team9.taads.api;


import com.team9.taads.service.PDFService;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PDFController {

    private final PDFService pdfService;

    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
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
    @RequestMapping(path="/module={id}")
    public String mcw(@PathVariable("id") Long id) throws FileNotFoundException {
        return pdfService.getModules(id);
    }


}
