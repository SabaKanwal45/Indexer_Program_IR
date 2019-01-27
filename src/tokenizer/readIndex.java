/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.RandomAccessFile;
import java.util.Scanner;
import static tokenizer.Tokenizer.Stemmer_for_tokens;

/**
 *
 * @author saba
 */
public class readIndex {

    public static String getDocId(String docName) {
        String docId = "";
        File docIds = new File("docids.txt");
        //System.out.println("Inside Document Handler");
        try {
            BufferedReader doc_ids_file = new BufferedReader(new FileReader(docIds));
            String str = "";
            while ((str = doc_ids_file.readLine()) != null) {
                //System.out.println(str);
                String[] doc_info = str.split("\t");
                //System.out.println(doc_info[1]);
                if (doc_info[1].equals(docName)) {
                    //System.out.println(doc_info[0]);
                    docId = doc_info[0];
                    break;
                }
            }
            doc_ids_file.close();

        } catch (Exception e) {
            System.out.println("Inside Exception of getting Document Id");

        }
        return docId;
    }

    public static void searchDocument(String docId) {
        File doc_index = new File("doc_index.txt");
        //System.out.println("Inside Term Handler");
        try {
            BufferedReader doc_index_file = new BufferedReader(new FileReader(doc_index));
            String str = "";
            int disinctTerms = 0;
            int totalTerms = 0;
            while ((str = doc_index_file.readLine()) != null) {
                //System.out.println(str);
                String[] term_info = str.split("\t");
                //System.out.println(term_info[1]);
                if (term_info[0].equals(docId)) {
                    //System.out.println(term_info[0]);
                    disinctTerms += 1;
                    totalTerms += term_info.length - 2;
                    //break;
                } else {
                    if (disinctTerms > 0) {
                        break;
                    }
                }
            }
            doc_index_file.close();
            System.out.println("Distinct terms: " + disinctTerms);
            System.out.println("Total terms: " + totalTerms);

        } catch (Exception e) {
            System.out.println("Inside Exception of getting Term Id");

        }
    }

    public static void handle_document_query(String docName) {
        System.out.println("Listing for document: " + docName);
        String docId = getDocId(docName);
        if (docId != "") {
            System.out.println("DOCID: " + docId);
            searchDocument(docId);
        } else {
            System.out.println("Document not found");
        }

    }

    public static String getTermId(String term) {
        String termId = "";
        File termsIds = new File("termids.txt");
        //System.out.println("Inside Term Handler");
        try {
            BufferedReader term_ids_file = new BufferedReader(new FileReader(termsIds));
            String str = "";
            while ((str = term_ids_file.readLine()) != null) {
                //System.out.println(str);
                String[] term_info = str.split("\t");
                //System.out.println(term_info[1]);
                if (term_info[1].equals(term)) {
                    //System.out.println(term_info[0]);
                    termId = term_info[0];
                    break;
                }
            }
            term_ids_file.close();

        } catch (Exception e) {
            System.out.println("Inside Exception of getting Term Id");

        }
        return termId;
    }
    public static String query_Preprocessing(String Term) {
        List<String> tokens = new ArrayList<String>();
        tokens.add(Term);
        tokens = Stemmer_for_tokens(tokens);
        //Term = tokens[0];
        for (String token : tokens) {
            Term = token;
        }
        return Term;
    }
    public static String[] SearchTermInfo(String termId) {
        File term_info = new File("term_info.txt");
        String output[] = {"", "", "", ""};
        //System.out.println("Inside Term Handler");
        try {
            BufferedReader term_info_file = new BufferedReader(new FileReader(term_info));
            String str = "";
            int disinctTerms = 0;
            int totalTerms = 0;
            while ((str = term_info_file.readLine()) != null) {
                //System.out.println(str);
                String[] term_detail = str.split("\t");
                //System.out.println(term_info[1]);
                if (term_detail[0].equals(termId)) {
                    //System.out.println(term_info[0]);
                    output[0] = term_detail[0];
                    output[1] = term_detail[1];
                    output[2] = term_detail[2];
                    output[3] = term_detail[3];
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Inside Exception of getting Term Id");

        }
        return output;

    }

    public static void handle_term_query(String Term) {
        Term = query_Preprocessing(Term);
        System.out.println("Listing for term: " + Term);
        String termId = getTermId(Term);
        if (termId != "") {
            System.out.println("TERMID: " + termId);
            String term_info[] = SearchTermInfo(termId);
            System.out.println("Number of documents containing term: " + term_info[3]);
            System.out.println("Term frequency in corpus: " + term_info[2]);
            System.out.println("Inverted list offset: " + term_info[1]);
        } else {
            System.out.println("Termt not found");
        }

    }

    public static void handle_both_queries(String docName, String Term) {
        System.out.println("Inverted list for term: " + Term);
        System.out.println("In document: " + docName);
        Term = query_Preprocessing(Term);
        String termId = getTermId(Term);
        if (termId != "") {
            System.out.println("TERMID: " + termId);
        }
        String docId = getDocId(docName);
        if (docId != "") {
            System.out.println("DOCID: " + docId);
        }
        if (termId != "" && docId != "") {
            try {
                String term_info[] = SearchTermInfo(termId);
                File term_index = new File("term_index.txt");
                RandomAccessFile raf = new RandomAccessFile(term_index, "r");
                raf.seek(Integer.parseInt(term_info[1]));
                String posting = raf.readLine();
                raf.close();
                String[] split_posting = posting.split("\t");
                //int prev_doc_Id = 0;
                int com_doc_Id = 0;
                int pos = 0;
                int term_freq = 0;
                List<String> positions = new ArrayList<String>();
                for (int index = 1; index < split_posting.length; index++) {
                    String[] seperte_t_d = split_posting[index].split(":");
                    com_doc_Id += Integer.parseInt(seperte_t_d[0]);
                    //System.out.println("docId: "+com_doc_Id);
                    if (com_doc_Id == Integer.parseInt(docId)) {
                        term_freq += 1;
                        pos = pos + Integer.parseInt(seperte_t_d[1]);
                        positions.add(String.valueOf(pos));
                    }
                }
                System.out.println("Term frequency in document: " + term_freq);
                System.out.print("Positions:");
                int index = 1;
                for (String position : positions) {
                    if (index == term_freq) {
                        System.out.print(" " + position);
                    } else {
                        System.out.print(" " + position + ",");
                    }
                    index += 1;
                }

            } catch (Exception e) {

            }
        }

    }

    public static void read_Index() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Your Complete Query: ");
        String input = scanner.nextLine();
        String input_Array[] = input.split(" ");
        System.out.println("");
        System.out.println("");
        if (input_Array.length > 0) {
            if (input_Array.length == 2) {
                //Single query
                if (input_Array[0].toLowerCase().equals("--doc")) {
                    handle_document_query(input_Array[1]);
                } else if (input_Array[0].toLowerCase().equals("--term")) {
                    handle_term_query(input_Array[1]);
                }

            } else if (input_Array.length == 4) {
                //Both document and term query
                if (input_Array[0].toLowerCase().equals("--doc") && input_Array[2].toLowerCase().equals("--term")) {
                    handle_both_queries(input_Array[1], input_Array[3]);
                } else if (input_Array[2].toLowerCase().equals("--doc") && input_Array[0].toLowerCase().equals("--term")) {
                    handle_both_queries(input_Array[3], input_Array[1]);
                }

            }
        }
    }

}
