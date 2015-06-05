#!/usr/bin/env python

def pearson_corrcoef(x,  y):
    from scipy.stats import pearsonr
    return " ".join(map(lambda x : str(x), pearsonr(x,  y)))
    #return pearsonr(x,  y)[0]

def binary_precision(x, y):
    assert(len(x) == len(y))
    return (sum(a == b and a == -1 for a, b in zip(x, y)) * 1.0 / sum(a == -1 for a in x))

def classify_report_bin(x, y):
    assert(len(x) == len(y))
    positive = 0
    negative = 0
    pos2pos = 0
    pos2neg = 0
    neg2pos = 0
    neg2neg = 0
    for a, b in zip(x, y):
        if a == 1:
            positive += 1
            if b == 1:
                pos2pos += 1
            elif b == -1:
                pos2neg += 1
            else:
                return "N/A"
        elif a == -1:
            negative += 1
            if b == 1:
                neg2pos += 1
            elif b == -1:
                neg2neg += 1
            else:
                return "N/A"
        else:
            return "N/A"
    tp = pos2pos
    fp = neg2pos
    tn = neg2neg
    fn = pos2neg
    accuracy = (tp + tn) * 1.0 / len(x)
    tpr = tp * 1.0 / (tp + fn) #sensitivity
    fpr = fp * 1.0 / (tn + fp)
    return "%d Pos, %d Neg\n%d TP, %d FN; %d TN, %d FP\nAcurracy = %f\nTPR(Sensitivity) = %f, FPR = %f"% \
           (positive, negative, tp, fn, tn, fp, accuracy, tpr, fpr)

def find_eer(fprs, tprs):
    diff_fprs_frrs = []
    for fpr, tpr in zip(fprs, tprs):
        frr = 1 - tpr #false rejection rate
        diff = abs(frr - fpr)
        if diff == 0:
            return fpr
        else:
            diff_fprs_frrs.append((diff, fpr, frr))
    diff_fprs_frrs = sorted(diff_fprs_frrs, key=lambda x:x[0])
    #print diff_fprs_frrs
    firstpoint = 0
    secondpoint = 1
    distthd = 0.001
    while secondpoint < len(diff_fprs_frrs):
        x1 = diff_fprs_frrs[firstpoint][1]
        y1 = diff_fprs_frrs[firstpoint][2]
        x2 = diff_fprs_frrs[secondpoint][1]
        y2 = diff_fprs_frrs[secondpoint][2]
        if abs(x1 - x2) < distthd or abs(y1 - y2) < distthd:
            secondpoint += 1
            continue
        else:
            return (x1 * y2 - x2 * y1) / (x1 + y2 - x2 -y1)
    raise Exception, "not valid nubmers"

def classify_report_bin_regression(x, y):
    assert(len(x) == len(y))
    positive = 0
    negative = 0
    import numpy as np
    thds = np.linspace(-1,1,100)
    pos2pos = [0 for i in thds]
    pos2neg = [0 for i in thds]
    neg2pos = [0 for i in thds]
    neg2neg = [0 for i in thds]
    for a, b in zip(x, y):
        if a == 1:
            positive += 1
            for i, thd in enumerate(thds):
                if b >= thd:
                    pos2pos[i] += 1
                else:
                    pos2neg[i] += 1
        elif a == -1:
            negative += 1
            for i, thd in enumerate(thds):
                if b >= thd:
                    neg2pos[i] += 1
                else:
                    neg2neg[i] += 1
        else:
            return "N/A"
    tps = pos2pos
    fps = neg2pos
    tns = neg2neg
    fns = pos2neg
    #accuracy = (tp + tn) * 1.0 / len(x)
    tprs = []
    fprs = []
    for tp, fp, tn , fn in zip(tps, fps, tns, fns):
        tpr = tp * 1.0 / (tp + fn) #sensitivity
        fpr = fp * 1.0 / (tn + fp)
        tprs.append(tpr)
        fprs.append(fpr)

    eer = find_eer(fprs, tprs)

    import matplotlib.pyplot as plt
    figure = plt.figure()
    ax = figure.add_subplot(111)
    ax.plot(fprs, tprs)

    max_yticks = 20
    yloc = plt.MaxNLocator(max_yticks)
    ax.yaxis.set_major_locator(yloc)
    ax.xaxis.set_major_locator(yloc)
    ax.grid(True)
    plt.title ('ROC curve')
    plt.xlabel('FPRS')
    plt.ylabel('TPRS')
    plt.plot([0, 1], [1, 0], 'k-')
    plt.plot([0, 1], [1, 0], 'k-')
    plt.plot([eer, eer], [1, 1-eer], 'k-')
    plt.plot([0, eer], [1-eer, 1-eer], 'k-')
    ax.text(eer+ 0.02, 1-eer, "EER = %.2f%%" % (eer * 100))
    plt.show()
    return "EER = %f" % eer

def classify_report_regression(x, y, refthd):
    assert(len(x) == len(y))
    positive = 0
    negative = 0
    import numpy as np
    thds = np.linspace(1,5,1000)
    pos2pos = [0 for i in thds]
    pos2neg = [0 for i in thds]
    neg2pos = [0 for i in thds]
    neg2neg = [0 for i in thds]
    for a, b in zip(x, y):
        if a >= refthd:
            positive += 1
            for i, thd in enumerate(thds):
                if b >= thd:
                    pos2pos[i] += 1
                else:
                    pos2neg[i] += 1
        else:
            negative += 1
            for i, thd in enumerate(thds):
                if b >= thd:
                    neg2pos[i] += 1
                else:
                    neg2neg[i] += 1
    tps = pos2pos
    fps = neg2pos
    tns = neg2neg
    fns = pos2neg
    #accuracy = (tp + tn) * 1.0 / len(x)
    tprs = []
    fprs = []
    for tp, fp, tn , fn in zip(tps, fps, tns, fns):
        tpr = tp * 1.0 / (tp + fn) #sensitivity
        fpr = fp * 1.0 / (tn + fp)
        tprs.append(tpr)
        fprs.append(fpr)

    eer = find_eer(fprs, tprs)

    import matplotlib.pyplot as plt
    figure = plt.figure()
    ax = figure.add_subplot(111)
    ax.plot(fprs, tprs)

    max_yticks = 20
    yloc = plt.MaxNLocator(max_yticks)
    ax.yaxis.set_major_locator(yloc)
    ax.xaxis.set_major_locator(yloc)
    ax.grid(True)
    plt.title ('ROC curve')
    plt.xlabel('FPRS')
    plt.ylabel('TPRS')
    plt.plot([0, 1], [1, 0], 'k-')
    plt.plot([0, 1], [1, 0], 'k-')
    plt.plot([eer, eer], [1, 1-eer], 'k-')
    plt.plot([0, eer], [1-eer, 1-eer], 'k-')
    ax.text(eer+ 0.02, 1-eer, "EER = %.2f%%" % (eer * 100))
    plt.show()
    return "EER = %f" % eer
    
if __name__ == "__main__":
    a = [1,  2,  3,  4,  5]
    b = [2,  3,  4,  5,  9]
    print pearson_corrcoef(a, b)
    

    
