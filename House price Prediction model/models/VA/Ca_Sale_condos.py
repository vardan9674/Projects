#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Jul  7 10:38:50 2019

@author: vardanchennupati
"""

import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt
from sklearn.model_selection import ShuffleSplit
import seaborn as sns
from sklearn import preprocessing
from sklearn.linear_model import ElasticNet
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error
from sklearn.model_selection import cross_val_score
import math
import sklearn.linear_model
import numpy
from sklearn.svm import SVC
from sklearn.model_selection import KFold
from sklearn.model_selection import StratifiedKFold
from sklearn.ensemble import BaggingRegressor
from sklearn.ensemble import BaggingClassifier,AdaBoostRegressor
from sklearn.ensemble import BaggingClassifier,AdaBoostClassifier
from sklearn.utils import shuffle
from scipy import stats
from sklearn.cluster import KMeans


data= pd.read_csv('va5_sale_townhouse.csv')

data = data.rename(columns = {"bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","cost/rent":"Rent","Price/sqft:":"Price_sqft","Year built:":"Yearbuilt"}) 
