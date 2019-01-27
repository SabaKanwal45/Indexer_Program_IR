# Indexer_Program_IR

You will write three programs:

A tokenizer, which reads a document collection and creates documents containing indexable tokens An indexer, which reads a collection of tokenized documents and constructs an inverted index A tool which reads your index and prints information read from it

Part 1: Tokenizing Documents The first step in creating an index is tokenization. You must convert a document into a stream of tokens suitable for indexing. Your tokenizer should follow these steps:

Accept a directory name as a command line argument, and process all files found in that directory Extract the document text with an HTML parsing library, ignoring the headers at the beginning of the file and all HTML tags Split the text into tokens which match the following regular expression: \w+(.?\w+)* Convert all tokens to lowercase (this is not always ideal, but indexing intelligently in a case-sensitive manner is tricky) Apply stop-wording to the document by ignoring any tokens found in this list Apply stemming to the document using any standard algorithm – Porter, Snowball, and KStem stemmers are appropriate. You should use a stemming library for this step. Your tokenizer will write three files: docids.txt – A file mapping a document's filename (without path) to a unique integer, its DOCID. Each line should be formatted with a DOCID and filename separated by a tab, as follows: 1234\tclueweb12-0000tw-13-04988 termids.txt – A file mapping a token found during tokenization to a unique integer, its TERMID. Each line should be formatted with a TERMID and token separated by a tab, as follows: 567\tasparagus doc_index.txt – A forward index containing the position of each term in each file. One line of this file contains all the positions of a single token in a single document. Each line should contain a DOCID, a TERMID, and a list of all the positions of that term in that document (the first term has position 1, the second has position 2, etc.). The DOCID, TERMID, and positions should be separated by tabs, as follows: 1234\t567\t1\t3\t12\t42 Extra credit: Implement your program using constant memory, with respect to the number of documents and the length of a document. This more realistic requirement would allow you to index any number of documents of any length. It is OK for memory usage to grow with respect to vocabulary size: we expect from Heap's Law that we would eventually run out of memory, so this would not work for a real Internet crawl, but that our vocabulary size will grow very slowly with respect to the number of documents we process.

Part 2: Inverting the index The final steps in index construction are inverting the index and preparing for fast random access to terms' inverted lists. Write a program which reads doc_index.txt to produce the following files.

term_index.txt – An inverted index containing the file position for each occurrence of each term in the collection. Each line should contain the complete inverted list for a single term. That is, it should contain a TERMID followed by a list of DOCID:POSITION values. However, in order to support more efficient compression you must apply delta encoding to the inverted list. The first DOCID for a term and the first POSITION for a document will be stored normally. Subsequent values should be stored as the offset from the prior value. Instead of encoding an inverted list like this: 567\t1234:9\t1234:13\t1240:3\t1240:7 you should encode it like this: 567\t1234:9\t0:4\t6:3\t0:4 Note that in order to do this, your DOCIDs and POSITIONs must be sorted in ascending order. term_info.txt – A file that provides fast access time to the inverted list for any term in your index, and also provides extra metadata for the term. Each line of this file should contain a TERMID followed by a tab-separated list of properties: 567\t1542\t567\t315 1542: The offset in bytes to the beginning of the line containing the inverted list for that term in term_index.txt. If you jump to this location and read one line, the first symbol you see should be the TERMID. 567: The total number of occurrences of the term in the entire corpus 315: The total number of documents in which the term appears As an aside, in a production setting you would want to be able to find the record for a term in constant or logarithmic time. One simple way to do that is to store the data in fixed-length records in a binary-encoded file sorted by TERMID, and then find the record for a given term using binary search. Since vocabulary growth is approximately logarithmic in the number of documents indexed (Heap's Law), this search would be approximately O(log log n) for the number of documents in your index. Faster methods exist, when this isn't good enough. For instance, you could use a constant-time hash function to select a bucket to perform a log-time search within. Disk I/O times also factor in, making a naive binary search suboptimal. Extra credit: Implement your program using constant memory with respect to the number of documents and term positions.

Extra credit: Implement your program in linear time with respect to the length of doc_index.txt, and using constant memory with respect to the number of documents, term positions, and terms. This stricter requirement is worth more points.

Part 3: Reading the index Now that you have an inverted index of the corpus, you'll want to be able to do something with it. This is mostly left for the next project. For now, we will just write the code to pull up some statistics from the index. Write a program which implements the following command line interface. Your program must not scan the inverted index linearly; it must look up the offset in term_info.txt and jump straight to the correct inverted list.

Keep in mind as you design this program that you will be reusing much of this code in the next project.

You can call the program anything you like, and in Java your command will look slightly different. Note that the values in the output examples below are made up.

Passing just --doc DOCNAME will list the following document information:

$ ./read_index.py --doc clueweb12-0000tw-13-04988

Listing for document: clueweb12-0000tw-13-04988 DOCID: 1234 Distinct terms: 25 Total terms: 501 Passing just --term TERM will stem the term and then list the following term information:

$ ./read_index.py --term asparagus

Listing for term: asparagus TERMID: 567 Number of documents containing term: 315 Term frequency in corpus: 567 Inverted list offset: 1542 Passing both --term TERM and --doc DOCNAME will show the inverted list for the document/term:

$ ./read_index.py --term asparagus --doc clueweb12-0000tw-13-04988

Inverted list for term: asparagus In document: clueweb12-0000tw-13-04988 TERMID: 567 DOCID: 1234 Term frequency in document: 4 Positions: 134, 155, 201, 233 We will evaluate your program by running these commands for selected documents and terms.

Submission Checklist Remember to submit your files in a folder named pr1. In order to receive the points for any extra credit you attempt, you must say in README.txt which extra credit problems you have attempted.

README.txt Part 1: Your source code Part 1: docids.txt (zipped or gzipped) Part 1: termids.txt (zipped or gzipped) Part 1: doc_index.txt (zipped or gzipped) Part 2: Your source code Part 2: term_index.txt (zipped or gzipped) Part 2: term_info.txt (zipped or gzipped) Part 3: Your source code
