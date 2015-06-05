#!/usr/bin/env python
# encoding: utf-8
'''
learn_model -- Program that learns machine translation quality estimation
models

learn_model is a program with which is possible to learn models for
sentence-pair quality estimation models using the algorithms implemented in the
scikit-learn machine learning toolkit.

It defines functions to work with different machine learning algorithms as well
as feature selection techniques and features preprocessing. The only dependency
so far is the sklearn package. ConfigParser is used to parse the configuration
file which has a similar layout to the Java properties file.

@author:     Jose' de Souza
        
@copyright:  2012. All rights reserved.
        
@license:    Apache License 2.0

@contact:    jose.camargo.souza@gmail.com
@deffield    updated: Updated
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

def set_selection_method(config):
    """
    Given the configuration settings, this function instantiates the configured
    feature selection method initialized with the preset parameters.
    
    TODO: implement the same method using reflection (load the class dinamically
    at runtime)
    
    @param config: the configuration file object loaded using yaml.load()
    @return: an object that implements the TransformerMixin class (with fit(),
    fit_transform() and transform() methods).
    """
    transformer = None
    
    selection_cfg = config.get("feature_selection", None)
    if selection_cfg:
        method_name = selection_cfg.get("method", None)
        
        # checks for RandomizedLasso
        if method_name == "RandomizedLasso":
            p = selection_cfg.get("parameters", None)
            if p:
                transformer = \
                RandomizedLasso(alpha=p.get("alpha", "aic"), 
                                scaling=p.get("scaling", .5), 
                                sample_fraction=p.get('sample_fraction', .75), 
                                n_resampling=p.get('n_resampling', 200),
                                selection_threshold=p.get('selection_threshold', .25), 
                                fit_intercept=p.get('fit_intercept', True), 
                                # TODO: set verbosity according to global level
                                verbose=True, 
                                normalize=p.get('normalize', True), 
                                max_iter=p.get('max_iter', 500), 
                                n_jobs=p.get('n_jobs', 1))
            else:
                transformer = RandomizedLasso()
        
        # checks for ExtraTreesClassifier
        elif method_name == "ExtraTreesClassifier":
            p = selection_cfg.get("parameters", None)
            if p:
                transformer = \
                ExtraTreesClassifier(n_estimators=p.get('n_estimators', 10),
                                     max_depth=p.get('max_depth', None),
                                     min_samples_split=p.get('min_samples_split', 1),
                                     min_samples_leaf=p.get('min_samples_leaf', 1),
                                     min_density=p.get('min_density', 1),
                                     max_features=p.get('max_features', 'auto'),
                                     bootstrap=p.get('bootstrap', False),
                                     compute_importances=p.get('compute_importances', True),
                                     n_jobs=p.get('n_jobs', 1),
                                     random_state=p.get('random_state', None),
                                     # TODO: set verbosity according to global level
                                     verbose=True)
            else:
                transformer = ExtraTreesClassifier()

    return transformer


def set_scorer_functions(scorers):
    scores = []
    for score in scorers:
        if score == 'mae':
            scores.append((score, mean_absolute_error))
        elif score == 'rmse':
            scores.append((score, root_mean_squared_error))
        elif score == 'mse':
            scores.append((score, mean_squared_error))
        elif score == 'f1_score':
            scores.append((score, f1_score))
        elif score == 'precision_score':
            scores.append((score, precision_score))
        elif score == 'recall_score':
            scores.append((score, recall_score))
        elif score == 'pearson_corrcoef':
            scores.append((score, pearson_corrcoef))
        elif score == 'binary_precision':
            scores.append((score, binary_precision))
            
    return scores


def set_optimization_params(opt):
    params = {}
    for key, item in opt.items():
        # checks if the item is a list with numbers (ignores cv and n_jobs params)
        if isinstance(item, list) and (len(item) == 3) and assert_number(item):
            # create linear space for each parameter to be tuned
            params[key] = np.linspace(item[0], item[1], num=item[2], endpoint=True)
            
        elif isinstance(item, list) and assert_string(item):
            print key, item
            params[key] = item
    
    return params


def optimize_model(estimator, X_train, y_train, params, scores, folds, verbose, n_jobs):
    clf = None
    for score_name, score_func in scores:
        log.info("Tuning hyper-parameters for %s" % score_name)
        
        log.debug(params)
        log.debug(scores)
        
        clf = GridSearchCV(estimator, params, loss_func=score_func, 
                           cv=folds, verbose=verbose, n_jobs=n_jobs)
        
        clf.fit(X_train, y_train)
        
        log.info("Best parameters set found on development set:")
        log.info(clf.best_params_)
        
    return clf.best_estimator_


def set_learning_method(config, X_train, y_train):
    """
    Instantiates the sklearn's class corresponding to the value set in the 
    configuration file for running the learning method.
    
    TODO: use reflection to instantiate the classes
    
    @param config: configuration object
    @return: an estimator with fit() and predict() methods
    """
    estimator = None
    
    learning_cfg = config.get("learning", None)
    if learning_cfg:
        p = learning_cfg.get("parameters", None)
        o = learning_cfg.get("optimize", None)
        scorers = \
        set_scorer_functions(learning_cfg.get("scorer", ['mae', 'rmse']))
        
        method_name = learning_cfg.get("method", None)
        if method_name == "SVR":
            if o:
                tune_params = set_optimization_params(o)
                estimator = optimize_model(SVR(), X_train, y_train, 
                                          tune_params, 
                                          scorers, 
                                          o.get("cv", 5),
                                          o.get("verbose", True),
                                          o.get("n_jobs", 1))
                
            elif p:
                estimator = SVR(C=p.get("C", 10),
                                epsilon=p.get('epsilon', 0.01),
                                kernel=p.get('kernel', 'rbf'),
                                degree=p.get('degree', 3),
                                gamma=p.get('gamma', 0.0034),
                                tol=p.get('tol', 1e-3),
                                verbose=False)
            else:
                estimator = SVR()
        
        elif method_name == "SVC":
            if o:
                tune_params = set_optimization_params(o)
                estimator = optimize_model(SVC(), X_train, y_train,
                                           tune_params,
                                           scorers,
                                           o.get('cv', 5),
                                           o.get('verbose', True),
                                           o.get('n_jobs', 1))
                
            elif p:
                estimator = SVC(C=p.get('C', 1.0),
                                kernel=p.get('kernel', 'rbf'), 
                                degree=p.get('degree', 3),
                                gamma=p.get('gamma', 0.0),
                                coef0=p.get('coef0', 0.0),
                                tol=p.get('tol', 1e-3),
                                verbose=p.get('verbose', False))
            else:
                estimator = SVC()
                    
        elif method_name == "LassoCV":
            if p:
                estimator = LassoCV(eps=p.get('eps', 1e-3),
                                    n_alphas=p.get('n_alphas', 100),
                                    normalize=p.get('normalize', False),
                                    precompute=p.get('precompute', 'auto'),
                                    max_iter=p.get('max_iter', 1000),
                                    tol=p.get('tol', 1e-4),
                                    cv=p.get('cv', 10),
                                    verbose=False)
            else:
                estimator = LassoCV()
        
        elif method_name == "LassoLars":
            if o:
                tune_params = set_optimization_params(o)
                estimator = optimize_model(LassoLars(), X_train, y_train, 
                                          tune_params,
                                          scorers,
                                          o.get("cv", 5),
                                          o.get("verbose", True),
                                          o.get("n_jobs", 1))
                
            if p:
                estimator = LassoLars(alpha=p.get('alpha', 1.0),
                                      fit_intercept=p.get('fit_intercept', True),
                                      verbose=p.get('verbose', False),
                                      normalize=p.get('normalize', True),
                                      max_iter=p.get('max_iter', 500),
                                      fit_path=p.get('fit_path', True))
            else:
                estimator = LassoLars()
        
        elif method_name == "LassoLarsCV":
            if p:
                estimator = LassoLarsCV(max_iter=p.get('max_iter', 500),
                                        normalize=p.get('normalize', True),
                                        max_n_alphas=p.get('max_n_alphas', 1000),
                                        n_jobs=p.get('n_jobs', 1),
                                        cv=p.get('cv', 10),
                                        verbose=False)
            else:
                estimator = LassoLarsCV()
                
    return estimator, scorers


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
    # sets the selection method
    transformer = set_selection_method(config)

    # if the system is configured to run feature selection
    # runs it and modifies the datasets to the new dimensions
    if transformer is not None:
        log.info("Running feature selection %s" % str(transformer))
        
        log.debug("X_train dimensions before fit_transform(): %s,%s" % X_train.shape)
        log.debug("y_train dimensions before fit_transform(): %s" % y_train.shape)
        
        X_train = transformer.fit_transform(X_train, y_train)
        
        log.debug("Dimensions after fit_transform(): %s,%s" % X_train.shape)
        
        if X_test is not None:
            X_test = transformer.transform(X_test)
    
    
    # sets learning algorithm and runs it over the training data
    estimator, scorers = set_learning_method(config, X_train, y_train)
    log.info("Running learning algorithm %s" % str(estimator))
    estimator.fit(X_train, y_train)
    
    if (X_test is not None) and (y_test is not None):
        log.info("Predicting unseen data using the trained model...")
        y_hat = estimator.predict(X_test)
        log.info("Evaluating prediction on the test set...")
        for scorer_name, scorer_func in scorers:
            v = scorer_func(y_test, y_hat)
            log.info("%s = %s" % (scorer_name, v))
        log.info("Customized scores: ")
        try:
            log.info("pearson_corrcoef = %s" % pearson_corrcoef(y_test,  y_hat))
        except:
            pass
        try:
            log.info("Precision score: = %s" % precision_score(y_test, y_hat))
        except:
            pass
        try:
            log.info("Recall score: = %s" % recall_score(y_test, y_hat))
        except:
            pass
        try:
            log.info("F1 score: = %s" % f1_score(y_test, y_hat))
        except:
            pass
        try:
            log.info("MAE: = %s" % mean_absolute_error(y_test, y_hat))
        except:
            pass
        try:
            log.info("RMSE: = %s" % root_mean_squared_error(y_test, y_hat))
        except:
            pass
        try:
            res = classify_report_bin(y_test, y_hat)
            if "N/A" <> res:
                log.info("Classify report bin: = %s" % res)
            else:
                res = classify_report_bin_regression(y_test, y_hat)
                if "N/A" <> res:
                    log.info("Classify report bin regression: = %s" % res)
                else:
                    if ref_thd is None:
                        log.error("No ref thd defined")
                    else:
                        refthd = float(ref_thd)
                        res = classify_report_regression(y_test, y_hat, refthd)
                        log.info("Classify report regression: = %s" % res)
        except Exception, e:
            print e
        with open("predicted.csv", 'w') as _fout:
            for _x,  _y in zip(y_test, y_hat):
                print >> _fout,  "%f\t%f" % (_x,  _y)

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
        
    scale = config.get("scale", True)

    log.info("Opening input files ...")
    log.debug("X_train: %s" % x_train_path)
    log.debug("y_train: %s" % y_train_path)
    log.debug("X_test: %s" % x_test_path)
    log.debug("y_test_path: %s" % y_test_path)

    # open feature and response files    
    X_train, y_train, X_test, y_test, labels = \
    open_datasets(x_train_path, y_train_path, x_test_path,
                  y_test_path, separator, labels_path)

    if scale:
        # preprocess and execute mean removal
        X_train, X_test = scale_datasets(X_train, X_test)

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
    program_license = '''%s

  Created by José de Souza on %s.
  Copyright 2012. All rights reserved.
  
  Licensed under the Apache License 2.0
  http://www.apache.org/licenses/LICENSE-2.0
  
  Distributed on an "AS IS" basis without warranties
  or conditions of any kind, either express or implied.

USAGE
''' % (program_shortdesc, str(__date__))

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
