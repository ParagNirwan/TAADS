package com.team9.taads.configuration;

import com.team9.taads.entity.PDFEntity;
import com.team9.taads.repository.PDFRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ucar.nc2.grib.GribData;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Configuration
public class PDFConfig {

    private static String pdfText;
    private static String pdfTextMain;
    private static File file;

    @Bean
    CommandLineRunner commandLineRunner(PDFRepository repository) {
        return args -> {



        };
    }
}
