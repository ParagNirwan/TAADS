package com.team9.taads.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.team9.taads.dao.PdfDAO;
import com.team9.taads.entity.PDFEntity;
import com.team9.taads.repository.PDFRepository;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.commons.math3.stat.Frequency;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static com.team9.taads.dao.PdfTextAccessService.pdfText;

@Service
public class PDFService {

    private final PDFRepository pdfRepository;
    Frequency freq = new Frequency();

    @Autowired
    public PDFService(PDFRepository pdfRepository, PdfDAO pdfDAO) {
        this.pdfRepository = pdfRepository;

    }

    //The NGram Code.............................................................................................................................
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

    //The TFIDF function...........................................................................................................................
    public HashMap<String, Double> tfidf(List<String> ngram) {

        //TF
        LinkedHashMap<String, Integer> TF = new LinkedHashMap<>();
        int[] TFn = new int[ngram.size()];
        for (int i = 0; i < ngram.size(); i++) {

            TF.put(ngram.get(i), (int) freq.getCount(ngram.get(i)));
            TFn[i] = (int) freq.getCount(ngram.get(i));

        }


        //IDF
        LinkedHashMap<String, Double> IDF = new LinkedHashMap<>();
        Double[] IDFn = new Double[ngram.size()];
        for (int i = 0; i < ngram.size(); i++) {
            IDF.put(ngram.get(i), Math.log(ngram.size() + TFn[i]));
            IDFn[i] = Math.log(ngram.size() + TFn[i]);
        }

        LinkedHashMap<String, Double> TFIDF = new LinkedHashMap<>();
        for (int i = 0; i < ngram.size(); i++) {
            TFIDF.put(ngram.get(i), TFn[i] * IDFn[i]);
        }
        return TFIDF;

    }


    //Highest from tfidf
    public List<String> highesttfidf(HashMap<String, Double> x) {
        List<String> y = new ArrayList<>();
        for (Map.Entry<String, Double> set :
                x.entrySet()) {
            if (set.getValue() >= 8) {
                y.add(set.getKey());
            }

        }
        return y;

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

    public String getModules(Long id) throws IOException {


        String textToAnalyze = String.valueOf(pdfRepository.findById(id));
        String textToAnalyze2 = textToAnalyze.substring(textToAnalyze.indexOf("Module Contents") + 16, textToAnalyze.indexOf("Teaching"));


//Stop Words Remove
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


        System.out.println(remaining);


        String[] remainingArray = remaining.toArray(new String[0]);
        List<String> lemmatisedWords = new ArrayList<>();


        //Lemmatisation
        try {
            InputStream posModelIn = new FileInputStream("E:\\taads\\project resources\\en-pos-maxent.bin");
            // loading the parts-of-speech model from stream
            POSModel posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            POSTaggerME posTagger = new POSTaggerME(posModel);
            // Tagger tagging the tokens
            String[] tags = posTagger.tag(remainingArray);

            // loading the dictionary to input stream
            InputStream dictLemmatizer = new FileInputStream("E:\\taads\\project resources\\en-lemmatizer.dict.txt");
            // loading the lemmatizer with dictionary
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

            // finding the lemmas
            String[] lemmas = lemmatizer.lemmatize(remainingArray, tags);

            for (int i = 0; i < remainingArray.length; i++) {
                System.out.println(remainingArray[i] + " -" + tags[i] + " : " + lemmas[i]);
            }

            for (int i = 0; i < remainingArray.length; i++) {
                if (lemmas[i].equals("0")) {
                    lemmatisedWords.add(lemmas[i]);
                } else {
                    lemmatisedWords.add(remainingArray[i]);
                }
            }

            System.out.println(lemmatisedWords);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //putting lemmatised words in a string.
        String lemmaplusstop = "";
        for (int i = 0; i < lemmatisedWords.size(); i++) {
            lemmaplusstop += lemmatisedWords.get(i) + " ";
        }

        System.out.println(lemmaplusstop);


        //Getting N Grams because
        List<String> unigrams = generateNGrams(1, lemmaplusstop);
        List<String> bigrams = generateNGrams(2, lemmaplusstop);
        List<String> trigrams = generateNGrams(3, lemmaplusstop);
        System.out.println("************UNIGRAMS*********** \n ************UNIGRAMS*********** \n************UNIGRAMS*********** ");
        System.out.println(unigrams);
        System.out.println("************BIGRAMS*********** \n ************BIGRAMS*********** \n************BIGRAMS*********** ");
        System.out.println(bigrams);
        System.out.println("************TRIGRAMS*********** \n ************TRIGRAMS*********** \n************TRIGRAMS*********** ");
        System.out.println(trigrams);


////////////////////////////////////////////////////////////////

        //Calculating most occurred word

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


        //here we call tfidf function somehow


        HashMap<String, Double> tfidf = new HashMap<>(tfidf(unigrams));
        //  System.out.println(tfidf);

        //sorting the hashmap
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        tfidf.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        System.out.println(sortedMap);


        //sorted Map to list

        List<String> s = new ArrayList<>(sortedMap.keySet());


        //Fetching Keywords using API
        JSONArray finalResult2 = new JSONArray();
        JSONArray finalResult1 = new JSONArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String uriSkill = "https://ec.europa.eu/esco/api/search?language=en&type=skill&text=" + s.get(s.size() - 1) + "+"+ s.get(s.size()-2);
        String uriOccupation = "https://ec.europa.eu/esco/api/search?language=en&type=occupation&text=" + s.get(s.size() - 1)+ "+"+ s.get(s.size()-2);

        URL urlgetSkill = new URL(uriSkill);
        URL urlgetOccupation = new URL(uriOccupation);
        String jsonO = "";

        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlgetSkill.openConnection();

        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();

            JsonElement je = JsonParser.parseString(response.toString());


////////////////// The Code for JSON Object
            JSONObject jsonObject = new JSONObject(je.toString()).getJSONObject("_embedded");
            JSONArray results = jsonObject.getJSONArray("results");
            System.out.println(results);

            for(int i=0;i<results.length();i++)
            {

                JSONObject obj = results.getJSONObject(i);
                JSONObject newobj = new JSONObject();
                newobj.put("Skills",obj.get("title"));
//                  newobj.put("URI",obj.get("uri"));
                finalResult1.put(newobj);

            }

            //System.out.println(""+finalResult);

//        Iterator<String> keys = jsonObject.keys();

////////////////////





            System.out.println("\nESCO Occupation Result in JSON: " + gson.toJson(je));

            jsonO += gson.toJson(je);
        } else {
            System.out.println(responseCode);
        }

        connection.disconnect();
        String jsonS = "";
        System.out.println("\n\n\n\n\n\n***************************************************************\n***************************************************************************\n*********************************************************\n\n\n\n\n");

        readLine = null;
        connection = (HttpURLConnection) urlgetOccupation.openConnection();
        connection.setRequestMethod("GET");
        responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();

            JsonElement je = JsonParser.parseString(response.toString());



            ////////////////// The Code for JSON Object so that we can throw it happily ever after
            JSONObject jsonObject = new JSONObject(je.toString()).getJSONObject("_embedded");
            JSONArray results = jsonObject.getJSONArray("results");
            System.out.println(results);

            for(int i=0;i<results.length();i++)
            {

                JSONObject obj = results.getJSONObject(i);
                JSONObject newobj = new JSONObject();
                newobj.put("Occupation",obj.get("title"));
//                  newobj.put("URI",obj.get("uri"));
                finalResult2.put(newobj);

            }

           // System.out.println("\n\n\n\n\n\n\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+finalResult);

//        Iterator<String> keys = jsonObject.keys();

////////////////////


            System.out.println("\nESCO Occupation Result in JSON: " + gson.toJson(je));
            jsonS += gson.toJson(je);
        }


        return textToAnalyze2 + "\n\n\n\n" + finalResult1 + finalResult2;

    }

}
