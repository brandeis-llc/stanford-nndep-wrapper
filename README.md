# stanford-nndep-wrapper

A simple wrapper around stanford dependency parser. 

## Usage: 
```
This program wraps around stanford neural dependency parser. 
	stanford parser version: corenlp 3.9.2

	Currently only supports input through STDIN and output to STDOUT

	INPUT:  Pass a sentence line by line via STDIN
	        Existing tokenization (with whitespace) is always preserved. 
	        Additionally POS or lemma+POS can be passed - use '/' as delimiter. 
	        e.g. Karen/NNP flew/VBP to/TO New_York/NNP ./PUNC
	        e.g. Karen/Karen/NNP flew/fly/VBP to/to/TO New_York/New_York/NNP ././PUNC
	OUTPUT: CoNLL-X format dependency annotation is printed to STDOUT
	        All runtime messages go to STDERR
```

