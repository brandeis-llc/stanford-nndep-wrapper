package edu.brandeis.nlp.stanford_nndep_wrapper;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    static DependencyParser p = new DependencyParser(new Properties());

    public static void main(String[] args) {
        p.loadModelFile("english_UD.gz");
        if (args.length > 0) {
            help();
            System.exit(0);
        }

        // quick sanity check
//        String sample = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./.";
//        String sample = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./." +
//                "\nThe/the/DT Fulton_County_Grand_Jury/fcgj/NNP said/say/VB Friday/friday/NN an/a/DT investigation/investigae/NN of/of/IN Atlanta/atlanta/NN 's/s/POS recent/recent/JJ primary_election/pe/NN produced/produce/VB `/`/` no/no/DT evidence/evidence/NN '/'/' that/that/IN any/a/DT irregularities/irregularity/NN took_place/take_place/VB ./.";
//        Scanner input = new Scanner(sample);

        Scanner input = new Scanner(System.in);
        int sentIdx = 1;
        while (input.hasNext()) {
            String inputLine = input.nextLine();
            String[] inputTokens = inputLine.split(" ");
            CoreLabelTokenFactory tf = new CoreLabelTokenFactory(false);
            List<CoreLabel> sentenceTokens = new ArrayList<>();
            String originalSentence = "";
            for (String token : inputTokens) {
                String[] tsplit = token.split("/");
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
                originalSentence = originalSentence + word + " ";
                CoreLabel coreToken = tf.makeToken(word, 0, 0);
                coreToken.setTag(pos);
                if (lemma != null) {
                    coreToken.setLemma(lemma);
                }
                sentenceTokens.add(coreToken);
            }

            // leave comments of sentence metadata
            System.out.printf("# sent = %d\n# text = %s\n",
                    sentIdx,
                    originalSentence
            );

            GrammaticalStructure tree = p.predict(sentenceTokens);
            int tokenIdx = 1;
            for (TypedDependency rel : tree.typedDependencies()) {
                CoreLabel token = sentenceTokens.get(tokenIdx - 1);
                if (tokenIdx == rel.dep().index()) {

                    System.out.printf("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t_\t_%n",
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
            System.out.println();
            sentIdx++;
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
    }

    private static void help() {
        System.out.println("This program wraps around stanford neural dependency parser. ");
        System.out.println("\tstanford parser version: corenlp 3.9.2");
        System.out.println();
        System.out.println("\tCurrently only supports input through STDIN and output to STDOUT");
        System.out.println();
        System.out.println("\tINPUT:  Pass a sentence line by line via STDIN");
        System.out.println("\t        Existing tokenization (with whitespace) is always preserved. ");
        System.out.println("\t        Additionally POS or lemma+POS can be passed - use '/' as delimiter. ");
        System.out.println("\t        e.g. Karen/NNP flew/VBP to/TO New_York/NNP ./PUNC");
        System.out.println("\t        e.g. Karen/Karen/NNP flew/fly/VBP to/to/TO New_York/New_York/NNP ././PUNC");
        System.out.println("\tOUTPUT: CoNLL-X format dependency annotation is printed to STDOUT");
        System.out.println("\t        All runtime messages go to STDERR");
        System.out.println();
    }
}


