#!/usr/bin/env python
# encoding: utf-8
'''
learn_model_CRF -- Program that learns machine translation quality estimation
models through Conditional Random Fields.

@author:	 Gustavo Henrique Paetzold
		
@copyright:  2015. All rights reserved.
		
@license:	Apache License 2.0

@contact:	ghpaetzold@outlook.com
@deffield	updated: Updated
'''

from argparse import ArgumentParser, RawDescriptionHelpFormatter
from evaluation_measures import root_mean_squared_error, mean_absolute_error
from sklearn.ensemble.forest import ExtraTreesClassifier
from sklearn.grid_search import GridSearchCV
from sklearn.linear_model.coordinate_descent import LassoCV
from sklearn.linear_model.least_angle import LassoLarsCV, LassoLars
from sklearn.linear_model.randomized_l1 import RandomizedLasso
from sklearn.metrics.metrics import mean_squared_error, f1_score, \
	precision_score, recall_score
from sklearn.svm.classes import SVR, SVC
from sklearn_utils import scale_datasets, open_datasets, assert_number, \
	assert_string
import logging as log
import numpy as np
import os
import sys
import yaml
import codecs

from customize_scorer import pearson_corrcoef, binary_precision, classify_report_bin, classify_report_bin_regression, classify_report_regression

__all__ = []
__version__ = 0.1
__date__ = '2012-11-01'
__updated__ = '2012-11-01'

DEBUG = 0
PROFILE = 0

DEFAULT_SEP = "\t"

class CLIError(Exception):
	'''Generic exception to raise and log different fatal errors.'''
	def __init__(self, msg):
		super(CLIError).__init__(type(self))
		self.msg = "E: %s" % msg
	def __str__(self):
		return self.msg
	def __unicode__(self):
		return self.msg

def predict_test_labels(learning, crfsuite, algorithm, parameters):
	"""
	Predicts quality labels for a test set.

	@param learning: configuration dictionary about the learning strategy to be used.
	@param crfsuite: path to the CRFSuite application.
	@param algorithm: learning algorithm to be used.
	@param parameters: algorithm parameters to be used.
	"""
	temp_input_file = learning.get("temp_input", None)
	if not temp_input_file:
		msg = "Path to temporary output file is missing."
		raise Exception(msg)

	model_file = learning.get("model_file", None)
	if not model_file:
		msg = "Path to model file is missing."
		raise Exception(msg)

	output_file = learning.get("output_file", None)
	if not output_file:
		msg = "Path to output file is missing."
		raise Exception(msg)

	comm = crfsuite + ' tag -m ' + model_file + ' ' + temp_input_file + ' > ' + output_file
	print(comm)
	os.system(comm)		

def learn_quality_estimation_model(learning, crfsuite, algorithm, parameters):
	"""
	Learns a quality estimation model through CRFSuite.

	@param learning: configuration dictionary about the learning strategy to be used.
	@param crfsuite: path to the CRFSuite application.
	@param algorithm: learning algorithm to be used.
	@param parameters: algorithm parameters to be used.
	"""

	temp_input_file = learning.get("temp_input", None)
	if not temp_input_file:
		msg = "Path to temporary input file is missing."
		raise Exception(msg)

	model_file = learning.get("model_file", None)
	if not model_file:
		msg = "Path to model file is missing."
		raise Exception(msg)
	comm = crfsuite + ' learn -m ' + model_file + ' --algorithm=' + algorithm	

	for parameter in parameters.keys():
		comm += ' --set='+parameter+'='+str(parameters[parameter])

	comm += ' ' + temp_input_file

	os.system(comm) 

def create_temp_input_file(learning, crfsuite, algorithm, parameters, X, y=[]):
	"""
	Produces the temp_input file for the training of a CRF model.
	
	@param learning: configuration dictionary about the learning strategy to be used.
	@param crfsuite: path to the CRFSuite application.
	@param algorithm: learning algorithm to be used.
	@param parameters: algorithm parameters to be used.
	@param X: the matrix containing feature values
	@param y: the vector containing labels
	"""

	temp_input_file = learning.get("temp_input", None)
	if not temp_input_file:
		msg = "Path to temporary input file is missing."
		raise Exception(msg)
	
	f = codecs.open(temp_input_file, 'w', 'utf-8')
	for i in range(0, len(X)):
		features = X[i]
		label = ''
		if len(y)>0:
			label = y[i]
		line = label
		for feature in features:
			line += '\t' + feature
		f.write(line.strip() + '\n')
	f.close()
	
def get_configuration_objects(config):
	"""
	Loads configuration data.

	@param config: config node loaded through yaml.load().
	"""
	# Get learning parameters:
	learning = config.get("learning", None)
	if not learning:
		msg = "Learning parameters are missing."
		raise Exception(msg)
		
	crfsuite = learning.get("crfsuite", None)
	if not crfsuite:
		msg = "Path to CRFSuite is missing."
		raise Exception(msg)
		
	algorithm = learning.get("algorithm", None)
	if not algorithm:
		msg = "Learning algorithm is missing."
		raise Exception(msg)
		
	parameters = learning.get("parameters", None)
	if not parameters:
		msg = "Learning parameters are missing."
		raise Exception(msg)

	return learning, crfsuite, algorithm, parameters

def fit_predict(config, X_train, y_train, X_test=None, y_test=None, ref_thd=None):
	'''
	Uses the configuration dictionary settings to train a model using the
	specified training algorithm. If set, also evaluates the trained model 
	in a test set. Additionally, performs feature selection and model parameters
	optimization.
	
	@param config: the configuration dictionary obtained parsing the 
	configuration file.
	@param X_train: the np.array object for the matrix containing the feature
	values for each instance in the training set.
	@param y_train: the np.array object for the response values of each instance
	in the training set.
	@param X_test: the np.array object for the matrix containing the feature
	values for each instance in the test set. Default is None.
	@param y_test: the np.array object for the response values of each instance
	in the test set. Default is None.
	'''	

	l, c, a, p = get_configuration_objects(config)
	
	create_temp_input_file(l, c, a, p, X_train, y=y_train)

	learn_quality_estimation_model(l, c, a, p)
	
	if X_test is not None:
		create_temp_input_file(l, c, a, p, X_test)
		predict_test_labels(l, c, a, p)
				
	#if y_test is not None:

def run(config):
	'''
	Runs the main code of the program. Checks for mandatory parameters, opens
	input files and performs the learning steps.
	'''
	# check if the mandatory parameters are set in the config file
	x_train_path = config.get("x_train", None)
	if not x_train_path:
		msg = "'x_train' option not found in the configuration file. \
		The training dataset is mandatory."
		raise Exception(msg)

	y_train_path = config.get("y_train", None)
	if not y_train_path:
		msg = "'y_train' option not found in the configuration file. \
		The training dataset is mandatory."
		raise Exception(msg)
		
	learning = config.get("learning", None)
	if not learning:
		msg = "'learning' option not found. At least one \
		learning method must be set."
		raise Exception(msg)
	
	# checks for the optional parameters
	x_test_path = config.get("x_test", None)
	y_test_path = config.get("y_test", None)

	separator = config.get("separator", DEFAULT_SEP)
	
	labels_path = config.get("labels", None)

	log.info("Opening input files ...")

	# open feature and response files
	X_train, y_train, X_test, y_test, labels = open_datasets(x_train_path, y_train_path, x_test_path, y_test_path, separator, labels_path, tostring=True)
	log.info("Opened input files.")

	# fits training data and predicts the test set using the trained model
	y_hat = fit_predict(config, X_train, y_train, X_test, y_test, config.get("ref_thd", None))

def main(argv=None): # IGNORE:C0111
	'''Command line options.'''
	
	if argv is None:
		argv = sys.argv
	else:
		sys.argv.extend(argv)

	program_name = os.path.basename(sys.argv[0])
	program_version = "v%s" % __version__
	program_build_date = str(__updated__)
	program_version_message = '%%(prog)s %s (%s)' % (program_version, program_build_date)
	program_shortdesc = __import__('__main__').__doc__.split("\n")[1]
	program_license = "%s%s" % (program_shortdesc, str(__date__))

	try:
		# Setup argument parser
		parser = ArgumentParser(description=program_license, 
								formatter_class=RawDescriptionHelpFormatter)
		
		parser.add_argument("configuration_file", action="store", 
							help="path to the configuration file (YAML file).")
		parser.add_argument("-v", "--verbose", dest="verbose", action="count", 
							help="set verbosity level [default: %(default)s]")
		parser.add_argument('-V', '--version', action='version', 
							version=program_version_message)

		# Process arguments
		args = parser.parse_args()
		
		cfg_path = args.configuration_file
		
		if args.verbose:
			log.basicConfig(level=log.DEBUG)
		else:
			log.basicConfig(level=log.INFO)
			
		# opens the config file
		config = None
		with open(cfg_path, "r") as cfg_file:
			config = yaml.load(cfg_file.read())
		 
		run(config)
		
	except KeyboardInterrupt:
		### handle keyboard interrupt ###
		return 0

if __name__ == "__main__":
	if DEBUG:
		sys.argv.append("-v")
	sys.exit(main())
