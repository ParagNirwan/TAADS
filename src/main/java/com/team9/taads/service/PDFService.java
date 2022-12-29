package com.team9.taads.service;

import com.team9.taads.configuration.PDFConfig;
import com.team9.taads.dao.PdfDAO;
import com.team9.taads.entity.PDFEntity;
import com.team9.taads.repository.PDFRepository;
import opennlp.tools.ngram.NGramModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.stat.Frequency;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.team9.taads.dao.PdfTextAccessService.pdfText;
import static java.util.stream.DoubleStream.concat;

@Service
public class PDFService {

    private static PDFConfig pdfConfig;
    private final PDFRepository pdfRepository;

    @Autowired
    public PDFService(PDFRepository pdfRepository, PdfDAO pdfDAO) {
        this.pdfRepository = pdfRepository;

    }

    public String allText() {
        return pdfRepository.findAll().toString();
    }


    public void addText() {
        String pdfTextMain;
        try {
            File file = new File("E:\\PDF Test\\IMACS.pdf");
            pdfText = new Tika().parseToString(file);
            pdfTextMain = pdfText.substring(pdfText.indexOf("Module Name"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }

        List<String> modules = new ArrayList<String>();
        List<String> modules2 = new ArrayList<String>();
        List<String> modules3 = new ArrayList<String>();

        String regex = "Name";
        modules = List.of(pdfTextMain.split(regex));

        modules2 = modules.subList(1     ,modules.size()); //1 to get 0th element removed



        for (int i = 0; i < modules2.size(); i++) {
            PDFEntity pdfEntityText = new PDFEntity(modules2.get(i));
            pdfRepository.save(pdfEntityText);
        }


    }

    public String mcw(Long id) {
        //most common words logic N grams and shit

        String textToAnalyze = String.valueOf(pdfRepository.findById(id));
        String textToAnalyze2 = textToAnalyze.substring(textToAnalyze.indexOf("Module Contents"), textToAnalyze.indexOf("Teaching"));


        //most common words N-Grams (Uni,Bi and Tri) + Stop Words Removal

        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        ArrayList<String> remaining = new ArrayList<>();
        ArrayList<String> remaining2 = new ArrayList<>();
        try{

            Analyzer analyzer = new StandardAnalyzer(stopWords);

            TokenStream tokenStream= analyzer.tokenStream(String.valueOf(stopWords),new StringReader(textToAnalyze2));
            CharTermAttribute term  = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while(tokenStream.incrementToken()){
                remaining2.add(term.toString());
            }
            remaining = remaining2;
            tokenStream.close();
            analyzer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Calculating most occurred word
        Frequency freq = new Frequency();
        String[] moduleWords2 = new String[remaining2.size()];
        moduleWords2 = remaining2.toArray(moduleWords2);

        HashMap<String,Integer> wordCount2 = new HashMap<>();

        for (int i = 0; i < moduleWords2.length; i++) {
            freq.addValue(moduleWords2[i].trim()); //removes space
        }

        for (int i = 0; i < moduleWords2.length; i++) {
            // System.out.println(moduleWords2[i] + "=" + freq.getCount(moduleWords2[i]));
            wordCount2.put(moduleWords2[i], (int) freq.getCount(moduleWords2[i]));
        }

        HashMap<String, Integer> mostRelevant2 = new HashMap<>();
        for (int i = 0; i < moduleWords2.length; i++) {
            // System.out.println(moduleWords2[i] + "=" + freq.getCount(moduleWords2[i]));
            if (freq.getCount(moduleWords2[i]) >= 5 && freq.getCount(moduleWords2[i]) < 15) {
                mostRelevant2.put(moduleWords2[i], (int) freq.getCount(moduleWords2[i]));
            }
        }

        /////Here will be played the games of N Grams Later






        return textToAnalyze2 + mostRelevant2;

    }
}
