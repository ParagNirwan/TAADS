package com.team9.taads.service;


import com.team9.taads.dao.PdfDAO;
import com.team9.taads.entity.PDFEntity;
import com.team9.taads.repository.PDFRepository;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.StringList;
import org.apache.commons.math3.stat.Frequency;
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import static com.team9.taads.dao.PdfTextAccessService.pdfText;

@Service
public class PDFService {


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
        List<String> moduleName = new ArrayList<String>();

        String regex = "Name";
        modules = List.of(pdfTextMain.split(regex));

        modules2 = modules.subList(1, modules.size()); //1 to get 0th element removed


        //Code to get the Module Names

//        String x = modules2.get(0).substring(0,modules2.get(0).indexOf("Module Responsibility"));
//        System.out.println(x);

        for (int i = 0; i < modules2.size(); i++) {
            moduleName.add(modules2.get(i).substring(0, modules2.get(i).indexOf("Responsibility") - 7));
        }


        //Code to save the data in the DATABASE
        for (int i = 0; i < modules2.size(); i++) {
            PDFEntity pdfEntityText = new PDFEntity(moduleName.get(i), modules2.get(i));
            pdfRepository.save(pdfEntityText);
        }
    }

    public String getModules(Long id) throws FileNotFoundException {


        String textToAnalyze = String.valueOf(pdfRepository.findById(id));
        String textToAnalyze2 = textToAnalyze.substring(textToAnalyze.indexOf("Module Contents")+16, textToAnalyze.indexOf("Teaching"));




        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        ArrayList<String> remaining = new ArrayList<>();
        ArrayList<String> remaining2 = new ArrayList<>();
        try {

            Analyzer analyzer = new StandardAnalyzer(stopWords);

            TokenStream tokenStream = analyzer.tokenStream(String.valueOf(stopWords), new StringReader(textToAnalyze2));
            CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                remaining2.add(term.toString());
            }
            remaining = remaining2;
            tokenStream.close();
            analyzer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] remainingArray = remaining.toArray(new String[0]);
        List<String> lemmatisedWords = new ArrayList<>();




        //Lemmatisation
        try{
            InputStream posModelIn = new FileInputStream("E:\\project resources\\en-pos-maxent.bin");
            // loading the parts-of-speech model from stream
            POSModel posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            POSTaggerME posTagger = new POSTaggerME(posModel);
            // Tagger tagging the tokens
            String tags[] = posTagger.tag(remainingArray);

            // loading the dictionary to input stream
            InputStream dictLemmatizer = new FileInputStream("E:\\project resources\\en-lemmatizer.dict.txt");
            // loading the lemmatizer with dictionary
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

            // finding the lemmas
            String[] lemmas = lemmatizer.lemmatize(remainingArray, tags);

            for(int i=0;i< remainingArray.length;i++){
                System.out.println(remainingArray[i]+" -"+tags[i]+" : "+lemmas[i]);
            }

            for(int i=0;i< remainingArray.length;i++){
                if(lemmas[i].equals("0")){
                    lemmatisedWords.add(lemmas[i]);
                }
                else {
                    lemmatisedWords.add(remainingArray[i]);
                }
            }

            System.out.println(lemmatisedWords);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        //Getting N Grams because I can't get nevermind.
        List<String> unigrams = generateNGrams(1,textToAnalyze2);
        List<String> bigrams = generateNGrams(2,textToAnalyze2);
        List<String> trigrams = generateNGrams(3,textToAnalyze2);
        System.out.println("************UNIGRAMS*********** \n ************UNIGRAMS*********** \n************UNIGRAMS*********** ");
        System.out.println(unigrams);
        System.out.println("************BIGRAMS*********** \n ************BIGRAMS*********** \n************BIGRAMS*********** ");
        System.out.println(bigrams);
        System.out.println("************TRIGRAMS*********** \n ************TRIGRAMS*********** \n************TRIGRAMS*********** ");
        System.out.println(trigrams);


////////////////////////////////////////////////////////////////

        //Calculating most occurred word
        Frequency freq = new Frequency();
        String[] moduleWords2 = new String[remaining2.size()];
        moduleWords2 = remaining2.toArray(moduleWords2);

        HashMap<String, Integer> wordCount2 = new HashMap<>();

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




        return textToAnalyze2 + mostRelevant2;

    }

    //The NGram Code.
    public static List<String> generateNGrams(int n, String str) {
        List<String> ngrams = new ArrayList<>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + n; j++) {
                sb.append((j > i ? " " : "") + words[j]);
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

}
