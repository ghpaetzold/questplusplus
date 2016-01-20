QuEst++
================
An open source tool for pipelined Translation Quality Estimation. 

This open source software is aimed at quality estimation (QE) for machine translation. It was developed by Professor Lucia Specia's team at the University of Sheffield and includes contributions from a number of researchers. This particular release was made possible through the [EXPERT](http://expert-itn.eu/) project and funding from [EAMT](http://www.eamt.org).

**QuEst++** is a new release of [**QuEst**](https://github.com/lspecia/quest), including support for word- and document-level QE. **QuEst++** has two independent modules: *Feature Extractor Module* (developed in Java) and *Machine Learning Module* (developed in Python).  

-----------------------------------------------------------------------

**Citing QuEst++**
  
  Lucia Specia, Gustavo Henrique Paetzold and Carolina Scarton (2015): Multi-level Translation Quality Prediction with QuEst++. In *Proceedings of ACL-IJCNLP 2015 System Demonstrations*, Beijing, China, pp. 115-120. [[PDF](https://aclweb.org/anthology/P/P15/P15-4020.pdf)] [[BIBTEX](http://staffwww.dcs.shef.ac.uk/people/C.Scarton/publications/specia2015.bib)]
  
-----------------------------------------------------------------------

# System requirements
 The Java and python required are:

1. [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)(JDK-1.8) 
  1. [NetBeans 8.1](https://netbeans.org/downloads/) (recommended) OR
  2. [Apache Ant](http://ant.apache.org/bindownload.cgi) (>= 1.9.3) 	
2. [Python 2.7.6](https://www.python.org/downloads/) (or above - only 2.7 stable distributions)
  1. [NumPy and SciPy](http://www.scipy.org/install.html) (NumPy >=1.6.1 and SciPy >=0.9)
  2. [scikit-learn](https://pypi.python.org/pypi/scikit-learn/0.15.2) (version 0.15.2)
  3. [PyYAML](http://pyyaml.org/)
  4. [CRFsuite](http://www.chokkan.org/software/crfsuite/)
  
**Please note:** For Linux, the *Feature Extractor Module* should work with both OpenJDK and Oracle versions ([java-8-oracle](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) recommended)

On Ubuntu, it's easier to install Oracle distribution:
 
```
sudo apt-get install oracle-java8-installer
```

(Check http://ubuntuhandbook.org/index.php/2014/02/install-oracle-java-6-7-or-8-ubuntu-14-04/ if you don't find that version)

NetBeans has issues to build on Linux. Get Ant instead to build through command line:

```
sudo apt-get install ant
```
# Feature extractor
This module implements a number of feature extractors, for word, sentence and document levels.

## Dependencies - tools

Some of the libraries required to compile and run the code are included in the `lib` directory in the root directory of the distribution. The Java libraries should be included there when possible. However, there are two libraries that were not included into the `lib` directory due their size (used for word-level features only):

- [Stanford Core NLP 3.5.1 models](http://nlp.stanford.edu/software/stanford-corenlp-full-2015-01-29.zip) (place the file `stanford-corenlp-3.5.1-models.jar` in the `lib`)
- [Stanford Core NLP Spanish models](http://nlp.stanford.edu/software/stanford-spanish-corenlp-2015-01-08-models.jar)

Apart from these libraries files, **QuEst++** requires other external tools / scripts to extract the baseline features. The paths for these external tools are set in a *configuration file* under `config` folder:

- [Perl  5](https://www.perl.org/get.html) (or above)
- [SRILM](http://www.speech.sri.com/projects/srilm/manpages/) (for Language Model features only)
- Tokenizer (available at `lang_resources` folder - from [Moses toolkit](http://www.statmt.org/moses/))
- Truecaser (available at `lang_resources` folder - from [Moses toolkit](http://www.statmt.org/moses/))

For advanced features at sentence and document levels, the following tools can be necessary:

- [TreeTagger](http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/)
- [Berkeley Parser](https://github.com/slavpetrov/berkeleyparser) (the file `BerkeleyParser-1.7.jar` is already inclued in the `lib` directory)

Please note that above list is not exhaustive. Advance set of features require external tools, see details in the features documentation.

## Dependencies - resources

The resources required for word, sentence and document-level baseline features are:
- corpus for source language 
- corpus for target language
- LM for source language 
- LM for target language
- ngram counts file for source language
- ngram counts file for target language

For sentence and document-level features only:
- Truecase model for source language
- Truecase model for target language
- Giza lex file

For word-level only:
- POS ngram counts file for source language
- POS ngram counts file for target language
- corpus com POS information for source language
- corpus com POS information for target language
- reference translations in the target language
- stop words list of the source language
- translation probabilities of the source language
- [Universal WordNet plugin](http://resources.mpi-inf.mpg.de/yago-naga/uwn/uwn.zip) (unzip this file inside the `lang_resources` folder)

Examples of these resources are provided in the `lang_resources` folder. 
Resources for several languages can be downloaded from [WMT15](http://www.statmt.org/wmt15/quality-estimation-task.html).
Advanced features may require specific data (please read the documentation of the specific features). 

## Input files
For word and sentence levels, the input files contain one sentence per line. 
For document level, the input files contain paths to documents (one document per line).
Both source and target files should have the same number of lines. 

An alignment file should also be provided for word-level feature extraction. 
This file is generated by [Fast Align](https://github.com/clab/fast_align).
Alternatively, we can provide the path for the Fast Align tool on the configuration file and **QuEst++** will generate the missing resource.

## Output file
The output file contain the features extracted separated by `tab`.
Word-level features output are features templates for CRF algorithm.
Sentence and document-level features are real values separated by `tab`.

## Build
You can build using [NetBeans](https://netbeans.org/downloads/) (version 8.1) - recommended.

Alternatively, you can use [Apache Ant](http://ant.apache.org/bindownload.cgi) (>= 1.9.3):

  `ant "-Dplatforms.JDK_1.8.home=/usr/lib/jvm/java-8-<<version>>"`
  
The ant command will create all classes needed to use **QuEst++** and a `QuEst++.jar` file.

## Basic Usage
1. Word-Level:

```
java -cp QuEst++.jar:lib/* shef.mt.WordLevelFeatureExtractor -lang english spanish -input input/source.word-level.en input/target.word-level.es -alignments lang_resources/alignments/alignments.word-level.out -config config/config.word-level.properties
```

2. Sentence-level:

```
java -cp QuEst++.jar shef.mt.SentenceLevelFeatureExtractor -tok -case true -lang english spanish -input input/source.sent-level.en input/target.sent-level.es -config config/config.sentence-level.properties
```

1. Document-level:

```
java -cp QuEst++.jar shef.mt.DocLevelFeatureExtractor -tok -case true -lang english spanish -input input/source.doc-level.en input/target.doc-level.es -config config/config.doc-level.properties
```

Omit the option `-tok` if the input files are already tokenised.
The option `-case` can be `no` (no casing), `true` (truecase) or `lower` (lowercase)

**Please note:** 
1. We provide examples of input and language resources for the basic usage commands.
2. One need to adapt the configuration file by providing the paths to the scripts where they are installed on your own system (such as SRILM and TreeTagger paths).
 
## Configuration File

**QuEst++** configuration file is a structured file (extension .properties) that contains information about the language pairs, featureset and paths to resources and tools. Information about language pairs and features are showed below:

```
sourceLang.default	= spanish
targetLang.default	= english
output			= output/test
input 			= input/test
resourcesPath 		= ./lang_resources
featureConfig 		= config/features/features_blackbox_17.xml
```

- `sourceLang.default` - default source language
- `targetLang.default` - default target language
- `output` - output folder
- `input` - input folder (where temporary files will be written)
- `resourcesPath` - language resources path
- `featureConfig` - features configuration file

An example of parameters related to baseline features (for sentence and document level) are presented below:

```
source.corpus               = ./lang_resources/english/sample_corpus.en
source.lm		    = ./lang_resources/english/english_lm.lm
source.truecase.model       = ./lang_resources/english/truecase-model.en
source.ngram                = ./lang_resources/english/english_ngram.ngram.clean
source.tokenizer.lang       = en
giza.path                   = ./lang_resources/giza/lex.e2s
tools.ngram.path	    = /export/tools/srilm/bin/i686-m64/
```
- `source.corpus` - path to a corpus of the source language
- `source.lm` - path to a language model file of the source language
- `source.truecase.model` - path to a truecase model of the source language
- `source.ngram` - path to a ngram count file of the source language
- `source.tokenizer.lang` - language for the tokenizer
- `giza.path` - path to the Giza++ lex file
- `tools.ngram.path` - path to SRILM

Similarly the config file contains parameters for the target language and for other resources and tools.

## Feature Configuration File

This is an XML file containing the features that should be extracted. This file is an input in the configuration file in the 'featureConfig' parameter. An example of this file is showed below:

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<features>
  <feature class="shef.mt.features.impl.bb.Feature1001" description="number of tokens in the source sentence" index="1001"/>
  <feature class="shef.mt.features.impl.bb.Feature1002" description="number of tokens in the target sentence" index="1002"/>
  <feature class="shef.mt.features.impl.bb.Feature1006" description="average source token length" index="1006"/>
</features>
```

If this file is used, three features will be extracted by the Feature Extractor module. 

-----------------------------------------------------------------------

# Machine Learning

The function of this module is to build models for machine translation (MT) quality estimation (QE). The input files are a set of instances with features that describe sentence pairs (source and target sentences). The features can be extracted using the FeatureExtractor program as explained above.

## Installation

The program itself does not require any installation step, it is just a matter
of running it provided that all the system requirements for Python are installed.

## Running

Note: Following commands are based on the assumption that all files are under 'learning' directory. 
The program takes only one input parameter, the configuration file. For
example:

  `python src/learn_model.py config/svr.cfg`

Please note that the file `svr.cfg` can be replaced by any other configuration file for different algorithms. 
Predictions are saved in a file called `predicted.csv` in the `learning` folder.

For building CRF (Conditional Random Fiels) models for word-level QE, use:

  `python src/learn_model_CRF.py config/crf.cfg`

Predictions are saved in a file called `output_file.txt` in the `learning` folder.

## Configuration file

The configuration uses the YAML format. Its layout is
quite straightforward. It is formed by key and value pairs that map directly
to dictionaries (in Python) or hash tables with string keys. One example is
as follows:

```
learning:
    method: LassoLars
    parameters:
        alpha: 1.0
        max_iter: 500
        normalize: True
        fit_intercept: True
        fit_path: True
        verbose: False
```

Each keyword followed by a ":" represents an entry in a hash. In this example,
the dictionary contains an entry "learning" that points to another dictionary
with two entries "method" and "parameters". The values of each entry can be
lists, dictionaries or primitive values like floats, integers, booleans or strings.
Please note that each level in the example above is indented with 4 spaces.

For more information about the YAML format please refer to http://www.yaml.org/ .

The configuration file is composed of three main parts: input and generic
options, feature selection, and learning.

Input comprises the following four parameters:

```
x_train: ./data/features/wmt2012_qe_baseline/training.qe.baseline.tsv
y_train: ./data/features/wmt2012_qe_baseline/training.effort
x_test: ./data/features/wmt2012_qe_baseline/test.qe.baseline.tsv
y_test: ./data/features/wmt2012_qe_baseline/test.effort
```

The first two are the paths to the files containing the features for the
training set and the responses for the training set, respectively. The
last two options refer to the test dataset features and response values,
respectively.

The format of the feature files is any format that uses a character to
separate the columns. The default is the tabulator char (tab, or '\t') as
this is the default format generated by the features extractor module.

Two other options are available:

```
scale: true
separator: "\t"
```

'scale' applies scikit-learn's scale() function to remove the mean and divide by
 the unit standard deviation for each feature. This function is applied to
 the concatenation of the training and test sets. More information about the
 scale function implemented by scikit-learn can be found at
 http://scikit-learn.org/dev/modules/generated/sklearn.preprocessing.scale.html

'separator' sets the character used to delimit the columns in the input files.

For CRF algorithm a parameter related to the folder or the CRFsuite also need to be set:

```
crfsuite: <<path-to-crfsuite>>
```

Configuration files for some of the implemented algorithms are available in the `config`
directory.

## Available algorithms

Currently these are the algorithms available in the script:

* SVR: epsilon Support Vector Regression
The parameters exposed in the "Parameters" section of the configuration file are:
	- C
	- epsilon
	- kernel
	- degree
	- gamma
	- tol
	- verbose

Documentation about these parameters is available at
http://scikit-learn.org/stable/modules/generated/sklearn.svm.SVR.html#sklearn.svm.SVR

* SVC: C-Support Vector Classification
The parameters exposed in the "Parameters" section of the configuration file are:
	- C
	- coef0
	- kernel
	- degree
	- gamma
	- tol
	- verbose

Documentation about these parameters is available at
http://scikit-learn.org/stable/modules/generated/sklearn.svm.SVC.html#sklearn.svm.SVC

* LassoCV: Lasso linear model with iterative fitting along a regularization path.
The best model is selected by cross-validation.
The parameters exposed in the "Parameters" section of the configuration file are:
	- eps
	- n_alphas
	- normalize
	- precompute
	- max_iter
	- tol
	- cv
	- verbose

Documentation about these parameters is available at
http://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LassoCV.html#sklearn.linear_model.LassoCV


* LassoLars: Lasso model fit with Least Angle Regression (Lars)
The parameters exposed in the "Parameters" section of the configuration file are:
	- alpha
	- fit_intercept
	- verbose
	- normalize
	- max_iter
	- fit_path

Documentation about these parameters is available at
http://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LassoLars.html#sklearn.linear_model.LassoLars

* LassoLarsCV: Cross-validated Lasso using the LARS algorithm
The parameters exposed in the "Parameters" section of the configuration file are:
  - max_iter
	- normalize
	- max_n_alphas
	- n_jobs
	- cv
	- verbose

Documentation about these parameters is available at
http://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LassoLarsCV.html#sklearn.linear_model.LassoLarsCV

* CRF: Conditional Random Fields algorithm
The parameters exposed in the "Parameters" section of the configuration file are:
  - variance
  
Documentation about these parameters is available at http://www.chokkan.org/software/crfsuite/  

## Parameter optimization

It is possible to optimize the parameters of the chosen method. This option is
set by the "optimize" setting under "learning" in the configuration file. The
script uses the scikit-learn's GridSearchCV implementation of grid search to
optimize parameters using cross-validation. To optimize the C, gamma and
epsilon parameters for the SVR, the learning section of the configuration
file could look as follows:

```
learning:
    method: SVR
optimize:
    kernel: [rbf]
    C: [1, 10, 2]
    gamma: [0.0001, 0.01, 2]
    epsilon: [0.1, 0.2, 2]
    cv: 3
    n_jobs: 1
    verbose: True
```

The parameter kernel is a list of strings representing the available kernels
implemented by scikit-learn. In this example only the "RBF" kernel is used.

* The SVR parameters C, gamma and epsilon are set with lists with 3 indexes:
    - the beginning of the range (begin value included)
    - the end of the range (end value included)
    - the number of samples to be generated within [beginning, end]

* The remaining parameters modify the behavior of the GridSearchCV class:
    - cv is the number of cross-validation folds
    - n_jobs is the number of parallel jobs scheduled to run the CV process
    - verbose is a boolean or integer value indicating the level of verbosity

For more information about the GridSearchCV class please refer to
http://scikit-learn.org/stable/modules/generated/sklearn.grid_search.GridSearchCV.html#sklearn.grid_search.GridSearchCV


## Feature selection

Another possible option is to perform feature selection prior to the learning
process. To set up a feature selection algorithm it is necessary to add the
"feature_selection" section to the configuration file. This section is
independent of the "learning" section:

```
feature_selection:
    method: RandomizedLasso
    parameters:
        cv: 10

learning:
    ...
```

Currently, the following feature selection algorithms are available:

* RandomizedLasso: works by resampling the training data and computing a Lasso
on each resampling. The features selected more often are good features.
The exposed parameters are:

    - alpha
    - scaling
    - sample_fraction
    - n_resampling
    - selection_threshold
    - fit_intercept
    - verbose
    - normalize
    - max_iter
    - n_jobs

These parameters and the method are documented at:
http://scikit-learn.org/stable/modules/generated/sklearn.linear_model.RandomizedLasso.html#sklearn.linear_model.RandomizedLasso


* ExtraTreesClassifier: meta estimator that fits a number of randomized decision trees (a.k.a. extra-trees) on various sub-samples of the dataset and use averaging to improve the predictive accuracy and control over-fitting.
The exposed parameters are:

    - n_estimators
    - max_depth
    - min_samples_split
    - min_samples_leaf
    - min_density
    - max_features
    - bootstrap
    - compute_importances
    - n_jobs
    - random_state
    - verbose

Documentation about the parameters and the method can be found at:
http://scikit-learn.org/stable/modules/generated/sklearn.ensemble.ExtraTreesClassifier.html#sklearn.ensemble.ExtraTreesClassifie

# License

The license for the Java code and any python and shell scripts developed here is the very permissive BSD License (http://en.wikipedia.org/wiki/BSD_licenses). For pre-existing code and resources, e.g., scikit-learn, SRILM and Berkeley parser, please check their websites. 
