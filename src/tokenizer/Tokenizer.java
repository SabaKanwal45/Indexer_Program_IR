/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;
//For command line input

import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.text.BreakIterator;

// HTML Parser
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
//Split in to tokens PTB Tokenizer 
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.BufferedWriter;
import java.io.FileWriter;
//Poster Stemmer
import org.apache.lucene.analysis.snowball.*;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 *
 * @author saba
 */
public class Tokenizer {

    /**
     * @param args the command line arguments
     */
    public static String parse_html_doc(File inputFile) {
        String text = "";
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            //To remove text from out side of tags
            Document doc = Jsoup.parse(fis, null, "UTF-8", Parser.xmlParser());
            // to remove meta tags and script tags text
            Document html_doc = Jsoup.parse(doc.body().html(), "UTF-8");
            // Required text
            text = html_doc.body().text();
        } catch (Exception e) {
            text = "-1";

        }
        return text;
    }

    // Convert from string to tokens and convert each token into LowerCase
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<String>();
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(text),
                new CoreLabelTokenFactory(), "");
        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            String token = label.toString().replaceAll("[^\\p{Alpha}]+", "");
            if (token.length() > 1) {
                tokens.add(token.toLowerCase());
            }
        }
        return tokens;

    }

    //Remove all Stop words from tokens
    public static List<String> remove_stop_words(List<String> tokens) {
        List<String> stop_words = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\" + "stoplist.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                stop_words.add(str);
            }
            for (String stop_word : stop_words) {
                while (tokens.contains(stop_word)) {
                    tokens.remove(stop_word);
                }
            }

        } catch (Exception e) {

        }

        return tokens;

    }

    public static List<String> Stemmer_for_tokens(List<String> tokens) {
        List<String> stemmed_tokens = new ArrayList<String>();
        for (String token : tokens) {
            PorterStemmer stemmer = new PorterStemmer();
            stemmer.setCurrent(token); //set string you need to stem
            stemmer.stem();  //stem the word
            stemmed_tokens.add(stemmer.getCurrent());//get the stemmed word
        }

        return stemmed_tokens;

    }

    public static HashMap writeForwardIndex(int docId, int termId, HashMap termsGlobalMap, List<String> tokens, Boolean appendFile) {

        File termIds = new File("termids.txt");
        HashMap forward_index = new HashMap();
        int position = 1;

        try {
            BufferedWriter termFile = new BufferedWriter(new FileWriter(termIds, appendFile));
            for (String token : tokens) {
                //System.out.println(',');
                //If term not in Global Map add it
                if (termsGlobalMap.containsKey(token) == false) {
                    termsGlobalMap.put(token, termId);
                    //write Terms Ids File
                    termFile.write(termId + "\t" + token);
                    termFile.newLine();
                    termId += 1;
                }
                // Update forward Index map
                String current_term_id = termsGlobalMap.get(token).toString();
                if (forward_index.containsKey(current_term_id) == false)//if Hashmap doesnt already contain n
                {
                    forward_index.put(current_term_id, position);
                } else {
                    String prePos = forward_index.get(current_term_id).toString();
                    String NewPos = prePos + "\t" + position;
                    forward_index.put(current_term_id, NewPos);

                }
                position += 1;
                //System.out.println(token);
            }
            termFile.close();
            File doc_index = new File("doc_index.txt");
            BufferedWriter doc_index_writer = new BufferedWriter(new FileWriter(doc_index, appendFile));
            Iterator it = forward_index.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String id = pair.getKey().toString();
                String pos = pair.getValue().toString();
                doc_index_writer.write(docId + "\t" + id + "\t" + pos);
                doc_index_writer.newLine();
            }
            doc_index_writer.close();
        } catch (Exception e) {

        }

        return termsGlobalMap;
    }

    /*pulic static List<String> create_tokens_from_string(){
        
        return tokens;
    }*/
    public static void forward_index() {
        // For directoty name input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter directory name: ");
        String directory_name = scanner.next();
        String directory_path = System.getProperty("user.dir") + "\\" + directory_name;
        System.out.print("Searching for path " + directory_path);
        File folder;
        int docId = 1;
        int termId = 1;
        File docIds = new File("docids.txt");
        // Reading files from a folder
        try {
            folder = new File(directory_path);
            File[] listOfFiles = folder.listFiles();
            Boolean appendFile = false;
            //
            HashMap termsGlobalMap = new HashMap();
            BufferedWriter docIdWriter = new BufferedWriter(new FileWriter(docIds, false));
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String text = parse_html_doc(file);
                    //write to docIds file
                    if (text == "-1") {
                        System.out.println(file.getName() + " skipped due to parsing error");
                    } else {
                        docIdWriter.write(docId + "\t" + file.getName());
                        docIdWriter.newLine();
                        List<String> tokens = new ArrayList<String>();
                        tokens = tokenize(text);
                        tokens = remove_stop_words(tokens);
                        tokens = Stemmer_for_tokens(tokens);
                        termsGlobalMap = writeForwardIndex(docId, termsGlobalMap.size() + 1, termsGlobalMap, tokens, appendFile);
                        appendFile = true;
                        docId += 1;
                    }
                }
            }
            docIdWriter.close();
        } catch (Exception e) {
            System.out.println('\n');
            System.out.println("No Such directory found on current path");
            System.out.println(e);
        }
    }

}
