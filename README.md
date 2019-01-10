# stanford-nndep-wrapper

A simple wrapper around stanford dependency parser. 

## Usage: 
```
This program wraps around stanford neural dependency parser.
        stanford parser version: coreNLP 3.9.2

usage:
 -d,--delim <arg>    input annotation delimiter
                     MUST be a single char (defaults to '/')
 -h,--help <arg>     print this message
 -i,--input <arg>    input file name (defaults to STDIN)
                     Existing tokenization (with whitespace) is preserved.
                     Additionally POS or lemma+POS can be passed
                     (see "delim" option)
                     e.g. Karen/NNP flew/VBP to/TO New_York/NNP ./PUNC
                     e.g. Karen/Karen/NNP flew/fly/VBP to/to/TO
                     New_York/New_York/NNP ././PUNC
 -o,--output <arg>   output file name (defaults to STDOUT)
                     CoNLL-X format will be used for dependency annotation
                     All runtime messages go to STDERR

```

