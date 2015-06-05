'''
sklearn_utils -- Helper functions to deal with data in the formats sklearn uses.
Utilities to read from text files to numpy arrays used by sklearn.
 
@author:     Jose' de Souza
        
@copyright:  2012. All rights reserved.
        
@license:    Apache License 2.0

@contact:    jose.camargo.souza@gmail.com
@deffield    updated: Updated
'''

from features_file_utils import read_labels_file, read_features_file, \
    read_reference_file
from sklearn import preprocessing
import logging as log
import numpy as np
import os

def assert_number(generic_list):
    '''
    Checks whether the list is composed only by numeric datatypes.
    
    @param generic_list: a list containing any object type.
    @return: True if the list contains only numeric objects. False otherwise.
    '''
    for i in generic_list:
        if not isinstance(i, (int, float)):
            return False
    return True

def assert_string(generic_list):
    for i in generic_list:
        if not isinstance(i, str):
            return False
    return True

def open_datasets(train_path, train_ref_path, test_path, 
                  test_ref_path, delim, labels_path=None, tostring=False):
    
    if not os.path.isfile(os.path.abspath(train_path)):
        raise IOError("training dataset path is not valid: %s" % train_path)
    
    if not os.path.isfile(os.path.abspath(train_ref_path)):
        raise IOError("training references path is not valid: %s" % train_ref_path)
    
    if not os.path.isfile(os.path.abspath(test_path)):
        raise IOError("test dataset path is not valid: %s" % test_path)
    
    if not os.path.isfile(os.path.abspath(test_ref_path)):
        raise IOError("test references path is not valid: %s" % test_ref_path)

    labels = []
    if labels_path is not None:
        if not os.path.isfile(os.path.abspath(labels_path)):
            raise IOError("labels file is not valid: %s" % labels_path)

        labels = read_labels_file(labels_path, delim)

    X_train = read_features_file(train_path, delim, tostring=tostring)
    y_train = read_reference_file(train_ref_path, delim, tostring=tostring)
    
    X_test = read_features_file(test_path, delim, tostring=tostring)
    y_test = read_reference_file(test_ref_path, delim, tostring=tostring)
    
    if len(X_train.shape) != 2:
        raise IOError("the training dataset must be in the format of a matrix with M lines and N columns.")

    if len(X_test.shape) != 2:
        raise IOError("the test dataset must be in the format of a matrix with M lines and N columns.")
        
    if X_train.shape[0] != y_train.shape[0]:
        print X_train.shape[0],  y_train.shape[0]
        raise IOError("the number of instances in the train features file does not match the number of references given.")
        
    if X_test.shape[0] != y_test.shape[0]:
        raise IOError("the number of instances in the test features file does not match the number of references given.")

    if X_train.shape[1] != X_test.shape[1]:
        raise IOError("the number of features in train and test datasets is different.")

    return X_train, y_train, X_test, y_test, labels



def scale_datasets(X_train, X_test):
    log.info("Scaling datasets...")

    log.debug("X_train shape = %s,%s" % X_train.shape)
    log.debug("X_test shape = %s,%s" % X_test.shape)
    
    # concatenates the whole dataset so that the scaling is
    # done over the same distribution
    dataset = np.concatenate((X_train, X_test))
    scaled_dataset = preprocessing.scale(dataset)
    
    # gets the scaled datasets splits back
    X_train = scaled_dataset[:X_train.shape[0]]
    X_test = scaled_dataset[X_train.shape[0]:]

    log.debug("X_train after scaling = %s,%s" % X_train.shape)
    log.debug("X_test after scaling = %s,%s" % X_test.shape)

    return X_train, X_test

if __name__ == '__main__':
    pass
