'''
evaluation_measures -- Additional metrics used in learn_model not implemented in
sklearn.


@author:     Jose' de Souza
        
@copyright:  2012. All rights reserved.
        
@license:    Apache License 2.0

@contact:    jose.camargo.souza@gmail.com
@deffield    updated: Updated
'''
from sklearn.metrics.metrics import mean_squared_error
from sklearn.metrics.pairwise import manhattan_distances
import numpy as np

def mean_absolute_error(x, y):
    vector = manhattan_distances(x, y)
    summation = np.sum(vector)
                     
    mae = summation / y.shape[0]
    
    return mae

def root_mean_squared_error(x, y):
    mse = mean_squared_error(x, y)
    rmse = np.sqrt(mse)
    return rmse



if __name__ == '__main__':
    pass
