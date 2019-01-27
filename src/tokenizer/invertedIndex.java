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
import java.io.LineNumberReader;

/**
 *
 * @author saba
 */
public class invertedIndex {

    public static void inverted_index() {
        HashMap<String, HashMap<String, List<String>>> forward_index = new HashMap<String, HashMap<String, List<String>>>();
        //String path = System.getProperty("user.dir") + "\\" + "doc_index.txt";
        File readFile = new File("doc_index.txt");
        String strline;
        int i = 1;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            while ((strline = reader.readLine()) != null) {
                String line[] = strline.split("\t");
                if (forward_index.containsKey(line[1]) == false) {
                    HashMap<String, List<String>> doc_pos_map = new HashMap<String, List<String>>();
                    // Make Positions Array
                    List<String> positions = new ArrayList<String>(Arrays.asList(line));
                    //remove docId and termId from position
                    positions.remove(0);
                    positions.remove(0);
                    //map with key docId and value positions of term
                    doc_pos_map.put(line[0], positions);
                    forward_index.put(line[1], doc_pos_map);

                } else {
                    HashMap<String, List<String>> doc_pos_map = forward_index.get(line[1]);
                    List<String> positions = new ArrayList<String>(Arrays.asList(line));
                    positions.remove(0);
                    positions.remove(0);
                    doc_pos_map.put(line[0], positions);
                    forward_index.put(line[1], doc_pos_map);
                }
            }
            File term_index = new File("term_index.txt");
            BufferedWriter term_index_writer = new BufferedWriter(new FileWriter(term_index, false));
            //RandomAccessFile(String name, String mode)
            //RandomAccessFile term_index_writer = new RandomAccessFile(readFile, "rw");
            int offset = 0;
            File term_info = new File("term_info.txt");
            BufferedWriter term_ifo_writer = new BufferedWriter(new FileWriter(term_info, false));
            Iterator term_it = forward_index.entrySet().iterator();
            while (term_it.hasNext()) {
                Map.Entry<String, HashMap<String, List<String>>> pair = (Map.Entry<String, HashMap<String, List<String>>>) term_it.next();
                String term_id = pair.getKey();
                HashMap<String, List<String>> doc_pos_map = pair.getValue();
                TreeMap<String, List<String>> sorted = new TreeMap<>();

                    // Copy all data from hashMap into TreeMap 
                sorted.putAll(doc_pos_map);
                //Iterator doc_it = sorted.entrySet().iterator();
                
                String posting = term_id;
                int PreviousDoc = 0;
                int totalDocs = 0;
                int totalOccurrences = 0;
                for (Map.Entry<String, List<String>> doc_pair : sorted.entrySet()) {
                    totalDocs += 1;
                    //Map.Entry<String, List<String>> doc_pair = (Map.Entry<String, List<String>>) doc_it.next();
                    String doc_id = doc_pair.getKey();
                    List<String> positions = doc_pair.getValue();
                    int previousPos = 0;
                    for (String pos : positions) {
                        posting = posting + '\t' + (Integer.parseInt(doc_id) - PreviousDoc) + ":" + (Integer.parseInt(pos) - previousPos);
                        PreviousDoc = Integer.parseInt(doc_id);
                        previousPos = Integer.parseInt(pos);
                        totalOccurrences += 1;
                    }
                }
                //posting = posting + '\n';
                String term_information = term_id + '\t' + offset + '\t' + totalOccurrences + '\t' + totalDocs;
                term_ifo_writer.write(term_information);
                term_ifo_writer.newLine();
                //term_index_writer.writeBytes(posting);
                term_index_writer.write(posting);
                term_index_writer.newLine();
                offset = offset + posting.length() + 2;

            }
            term_ifo_writer.close();
            term_index_writer.close();
        } catch (Exception e) {
            System.out.println("file not found");
        }
    }

}
