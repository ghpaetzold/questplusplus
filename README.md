QuEst++
================
An open source tool for pipelined Translation Quality Estimation. 

QuEst++ has two independent modules: *Feature Extractor Module* (developed in Java) and *Machine Learning Module* (developed in Python). This particular release was made possible through the [EXPERT](http://expert-itn.eu/) project. 

-----------------------------------------------------------------------

**Citing QuEst++**
  
  Lucia Specia, Gustavo Henrique Paetzold and Carolina Scarton (2015): Multi-level Translation Quality Prediction with QuEst++. In *Proceedings of ACL-IJCNLP 2015 System Demonstrations*, Denver, CO, pp. 118-125. [[PDF](http://aclweb.org/anthology/N/N15/N15-2016.pdf)] [[BIBTEX](http://staffwww.dcs.shef.ac.uk/people/C.Scarton/publications/specia2015.bib)]
  
-----------------------------------------------------------------------

# System requirements
 The Java and python required are:

1. [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)(JDK-1.8)
2. [Python 2.7.6](https://www.python.org/downloads/) (or above - only 2.7 stable distributions)
  1. [SciPy and NumPy](http://www.scipy.org/install.html) (SciPy >=0.9 and NumPy >=1.6.1)
  2. [scikit-learn](http://scikit-learn.org/stable/install.html)

**Please note:** for Linux, the *Feature Extractor Module* works with both Oracle and OpenJDK distributions.

# Feature extractor
This module implements a number of feature extractors, for word, sentence and document levels.

## Dependencies

Some of the libraries required to compile and run the code are included in the `lib` directory in the root directory of the distribution. The Java libraries should be included there when possible. However, there are two libraries that were not included into the `lib` directory due their size (used for word-level features only):

- [Stanford Core NLP 3.5.1 models](http://nlp.stanford.edu/software/stanford-corenlp-full-2015-01-29.zip) (place the file `stanford-corenlp-3.5.1-models.jar` in the `lib`)
- [Stanford Core NLP Spanish models](http://nlp.stanford.edu/software/stanford-spanish-corenlp-2015-01-08-models.jar)

Apart from these libraries files, **QuEst++** requires other external tools / scripts to extract sentence- and document-level baseline features. The paths for these external tools are set in a *configuration file* under `config` folder:

- [Perl  5](https://www.perl.org/get.html) (or above)
- [SRILM](http://www.speech.sri.com/projects/srilm/manpages/) (for Language Model features only)
- Tokenizer (available at `lang_resources` folder - from [Moses toolkit](http://www.statmt.org/moses/))
- Truecaser (available at `lang_resources` folder - from [Moses toolkit](http://www.statmt.org/moses/))

For advanced features at sentence and document levels, the following tools can be necessary:

- [TreeTagger](http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/)
- [Berkeley Parser](https://github.com/slavpetrov/berkeleyparser) (the file `BerkeleyParser-1.7.jar` is already inclued in the `lib` directory)

Please note that above list is not exhaustive. Advance set of features require external tools, see details in the features documentation.

## Build
You can build using [NetBeans](https://netbeans.org/downloads/) (version 8.1) - recommended.

Alternatively, you can use [Apache Ant](http://ant.apache.org/bindownload.cgi) (>= 1.9.3):

  `ant "-Dplatforms.JDK_1.8.home=/usr/lib/jvm/java-8-<<version>>"`
  
The ant command will create all classes needed to use **QuEst++** and a `QuEst++.jar` file.


## Basic Usage
1. Word-Level:
  `java -cp QuEst++.jar:lib/* shef.mt.WordLevelFeatureExtractor -lang english spanish -input input/source.word-level.en input/target.word-level.es -alignments lang_resources/alignments/alignments.word-level.out -config config/config.word-level.properties`

2. Sentence-level:
  `java -cp QuEst++.jar shef.mt.SentenceFeatureExtractor -tok -case true -lang english spanish -input input/source.sent-level.en input/target.sent-level.es -config config/config.sentence-level.properties`

1. Document-level:
  `java -cp QuEst++.jar shef.mt.DocLevelFeatureExtractor -tok -case true -lang english spanish -input input/source.doc-level.en input/target.doc-level.es -config config/config.doc-level.properties`

Omit the option `-tok` if the input files are already tokenised.
The option `-case` can be `no` (no casing), `true` (truecase) or `lower` (lowercase)
