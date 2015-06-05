'''
Created on Aug 29, 2012

@author: desouza
'''
import codecs
import numpy as np
import logging as log

def read_labels_file(path, delim, encoding='utf-8'):
    '''Reads the labels of each column in the training and test files (features 
    and reference files).
    
    @param path: the path of the labels file
    @param delim: the character used to separate the label strings.
    @param encoding: the character encoding used to read the file. 
    Default is 'utf-8'.
    
    @return: a list of strings representing each feature column.
    '''
    labels_file = codecs.open(path, 'r', encoding)
    lines = labels_file.readlines()
    
    if len(lines) > 1:
        log.warn("labels file has more than one line, using the first.")
    
    if len(lines) == 0:
        log.error("labels file is empty: %s" % path)
    
    labels = lines[0].strip().split(delim)
    
    return labels
    
    
def read_reference_file(path, delim, encoding='utf-8', tostring=False):
    """Parses the file that contains the references and stores it in a numpy array.
    
       @param path the path of the file.
       @delim char the character used to separate values.
       
       @return: a numpy array representing each instance response value
    """
    
    # reads the references to a vector
    refs_file = codecs.open(path, 'r', encoding)
    refs_lines = []
    for line in refs_file:
        cols = line.strip().split(delim)
        refs_lines.append(cols[0])

    if tostring:
        refs = np.array(refs_lines, dtype='str')
    else:
        refs = np.asfarray(refs_lines)
    
    return refs


def read_features_file(path, delim, encoding='utf-8', tostring=False):
    '''
    Reads the features for each instance and stores it on an numpy array.
    
    @param path: the path to the file containing the feature set.
    @param delim: the character used to separate the values in the file pointed by path.
    @param encoding: the character encoding used to read the file.
    
    @return: an numpy array where the columns are the features and the rows are the instances.
    '''
    # this method is memory unneficient as all the data is kept in memory
    feats_file = codecs.open(path, 'r', encoding='utf-8')
    feats_lines = []
    line_num = 0
    for line in feats_file:
        if line == "":
            continue
        toks = tuple(line.strip().split(delim))
        cols = []
        for t in toks:
            if t != '':
                try:
                    if tostring:
                        cols.append(t)
                    else:
                        cols.append(float(t))
                except ValueError as e:
                    log.error("%s line %s: %s" % (e, line_num, t))
        
        line_num += 1
        feats_lines.append(cols)
    
    #    print feats_lines
    feats = np.asarray(feats_lines)
    
    return feats


if __name__ == '__main__':
    pass