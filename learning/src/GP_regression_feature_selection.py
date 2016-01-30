"""
Simple Gaussian Processes regression with an RBF kernel
@ Kashif Shah
"""
import pylab as pb
import numpy as np
import GPy
pb.ion()
pb.close('all')

X = np.genfromtxt('/Users/kashif/Documents/projects/quest-master/learning/data/features/wmt2012_qe_baseline/train-79-features.qe.tsv')
test_X = np.genfromtxt('/Users/kashif/Documents/projects/quest-master/learning/data/features/wmt2012_qe_baseline/test-79-features.qe.tsv')
Y = np.genfromtxt('/Users/kashif/Documents/projects/quest-master/learning/data/features/wmt2012_qe_baseline/training.effort').reshape(-1, 1)
test_Y = np.genfromtxt('/Users/kashif/Documents/projects/quest-master/learning/data/features/wmt2012_qe_baseline/test.effort').reshape(-1, 1)

# rescale X
mx = np.mean(X,axis=0)
sx = np.std(test_X,axis=0)

ok = (sx > 0)
X = X[:,ok]
test_X = test_X[:,ok]
sx, mx = sx[ok], mx[ok]

X = (X - mx) / sx
test_X = (test_X - mx) / sx

print 'Dropped features with constant values:'
print np.nonzero(ok == False)[0]

# we could centre Y too?

D = X.shape[1]

if True:
    X = X[:50,:]
    Y = Y[:50,:]

# construct kernel
#rbf = GPy.kern.rbf_ARD(D)
rbf = GPy.kern.rbf(D, ARD=True)
noise = GPy.kern.white(D)
kernel = rbf + noise

# create simple GP model
#m = GPy.models.GP_regression(X,Y, kernel = kernel)
m = GPy.models.GPRegression(X,Y, kernel = kernel)
#m.tie_param('rbf_ARD_0_lengthscale')
m.constrain_positive('')
m.optimize('bfgs', max_f_eval = 50, messages = True)
print m

#mu, s2 = m.predict(test_X)
mu = m.predict(test_X)
mae = np.mean(np.abs(mu - test_Y))
rmse = np.mean((mu - test_Y) ** 2) ** 0.5

print 'SEiso -- mae', mae, 'rmse', rmse

# the iso kernel initialises the ARD one to avoid local minima
m.untie_everything()
m.optimize('bfgs', max_f_eval = 100, messages = True)
print m

#mu, s2 = m.predict(test_X)
mu  = m.predict(test_X)
mae = np.mean(np.abs(mu - test_Y))
rmse = np.mean((mu - test_Y) ** 2) ** 0.5

print 'SEard -- mae', mae, 'rmse', rmse
#print mae
#print rmse

length_scales = m['.*lengthscale.*'] #m.get_param()[1:-1]

sort_ls = np.argsort(length_scales)

print 'Feature ranking by length scale'
print sort_ls

