package edu.brandeis.llc.stanford_nndep_wrapper;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static String ANNOTATION_DELIMITER;
    private static String TOKEN_DELIMITER = " ";
    private static DependencyParser p = new DependencyParser(new Properties());



    public static void main(String[] args) {

        Options options = new Options();
        Option help = new Option("h", "help", true, "print this message");
        Option inFile = new Option("i", "input", true, "input file name (defaults to STDIN)\n" +
                "Existing tokenization (with whitespace) is preserved.\n"+
                "Additionally POS or lemma+POS can be passed\n" +
                "(see \"delim\" option)\n" +
                "e.g. Karen/NNP flew/VBP to/TO New_York/NNP ./PUNC\n" +
                "e.g. Karen/Karen/NNP flew/fly/VBP to/to/TO New_York/New_York/NNP ././PUNC");
        Option outFile = new Option("o", "output", true, "output file name (defaults to STDOUT)\n" +
                "CoNLL-X format will be used for dependency annotation\n" +
                "All runtime messages go to STDERR");
        Option delimiter = new Option("d", "delim", true, "input annotation delimiter \nMUST be a single char (defaults to '/')");
        options.addOption(help);
        options.addOption(inFile);
        options.addOption(outFile);
        options.addOption(delimiter);

        CommandLineParser parser = new DefaultParser();
        CommandLine argv = null;
        try {
            argv = parser.parse(options, args);
        } catch (ParseException e) {
            help(options);
        }

        String infile = argv.hasOption('i') ? argv.getOptionValue('i') : null;
        String outfile = argv.hasOption('o') ? argv.getOptionValue('o') : null;
        ANNOTATION_DELIMITER = argv.hasOption('d') && argv.getOptionValue('d').length() == 1 ? argv.getOptionValue('d') : "/";

        // quick sanity check
//        String sample = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./.";
//        String sample = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./." +
//                "\nThe/the/DT Fulton_County_Grand_Jury/fcgj/NNP said/say/VB Friday/friday/NN an/a/DT investigation/investigae/NN of/of/IN Atlanta/atlanta/NN 's/s/POS recent/recent/JJ primary_election/pe/NN produced/produce/VB `/`/` no/no/DT evidence/evidence/NN '/'/' that/that/IN any/a/DT irregularities/irregularity/NN took_place/take_place/VB ./.";
//        Scanner input = new Scanner(sample);

        Scanner input = null;
        if (infile == null) {
            input = new Scanner(System.in);
            System.err.println("accepting input from STDIN");
        } else {
            try {
                input = new Scanner(new File(infile));
            } catch (FileNotFoundException e) {
                error("file not found: " + infile);
            }
        }
        PrintStream out = null;
        if (outfile == null) {
            out = new PrintStream(System.out);
        } else {
            File outf = new File(outfile);
            if (!outf.exists()) {
                try {
                    outf.createNewFile();
                } catch (IOException e) {
                    error("could not create the output file: " + outfile);
                }
            }
            try {
                out = new PrintStream(new FileOutputStream(outf));
            } catch (FileNotFoundException ignored) {
            }
        }

//        Scanner input = new Scanner(infile == null ? System.in : new File(infile));
        int sentIdx = 1;
        p.loadModelFile("english_UD.gz");
        if (input == null) {
            error("input source is not found");
            return;
        }
        while (input.hasNext()) {
            String inputLine = input.nextLine();
            String[] inputTokens = inputLine.split(TOKEN_DELIMITER);
            CoreLabelTokenFactory tf = new CoreLabelTokenFactory(false);
            List<CoreLabel> sentenceTokens = new ArrayList<>();
            StringBuilder originalSentence = new StringBuilder();
            for (String token : inputTokens) {
                String[] tsplit = token.split(ANNOTATION_DELIMITER);
                String word = null;
                String pos = null;
                String lemma = null;
                if (tsplit.length == 2) {
                    word = tsplit[0];
                    pos = tsplit[1];
                } else if (tsplit.length == 3) {
                    word = tsplit[0];
                    lemma = tsplit[1];
                    pos = tsplit[2];
                } else {
                    error("unknown input format");
                }
                originalSentence.append(word).append(" ");
                CoreLabel coreToken = tf.makeToken(word, 0, 0);
                coreToken.setTag(pos);
                if (lemma != null) {
                    coreToken.setLemma(lemma);
                }
                sentenceTokens.add(coreToken);
            }

            // leave comments of sentence metadata
            out.printf("# sent = %d\n# text = %s\n",
                    sentIdx,
                    originalSentence.toString()
            );

            GrammaticalStructure tree = p.predict(sentenceTokens);
            int tokenIdx = 1;
            for (TypedDependency rel : tree.typedDependencies()) {
                CoreLabel token = sentenceTokens.get(tokenIdx - 1);
                if (tokenIdx == rel.dep().index()) {

                    out.printf("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t_\t_%n",
                            tokenIdx,
                            token.word(),
                            token.lemma() != null ? token.lemma() : "_",
                            token.tag(),
                            token.tag(),
                            rel.gov().index(),
                            rel.reln().getShortName());
                } else {
                    error("indexed mismatch: " + token.index() + " // " + token.word());
                }
                tokenIdx++;
            }
            out.println();
            sentIdx++;
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static void help(Options options) {
        System.out.println("This program wraps around stanford neural dependency parser. ");
        System.out.println("\tstanford parser version: coreNLP 3.9.2");
        System.out.println();
        HelpFormatter helper = new HelpFormatter();
        helper.printHelp(" ", options);
        System.exit(0);
    }
}


