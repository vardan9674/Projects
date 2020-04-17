#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue May 28 19:09:27 2019

@author: vardanchennupati
"""

import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt

data= pd.read_csv('new5.csv')

data = data.rename(columns = {"zip": "Zip","cost/rent":"Cost","status":"Status","bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","Price/sqft:":"Price_persqft","Year built":"Yearbuilt","Lot:":"Lot"}) 



#cleaning the data

#parking attribute

data=data.replace('No Data','0')
data=data.replace('1 space','1')
data=data.replace('2 spaces','2')
data=data.replace('Attached Garage','1')
data=data.replace('3 spaces','3')
data=data.replace('4 spaces','4')
data=data.replace('Carport','1')
data=data.replace('Off street, On street','0')
data=data.replace('6 spaces','6')
data=data.replace('None','0')
data=data.replace('5 spaces','5')
data=data.replace('8 spaces','8')
data=data.replace('off street','0')
data=data.replace('Off street, Attached Garage','1')
data=data.replace('Off street','0')
data=data.replace('On street','1')
data=data.replace('Detached Garage','1')
data=data.replace('11 spaces','11')

#Lot a
data['Lot']=data.Lot.str.replace(',','')
data['Lot']=data.Lot.str.replace('sqft','')
data['Lot']=data.Lot.str.replace('.','')
data['Lot']=data.Lot.str.replace('acres','')

#Cost
data['Cost'] = data.Cost.str.replace(',','')
data['Cost']=data.Cost.str.replace('$','')

#price_persqft
data['Price_persqft'] = data.Price_persqft.str.replace(',','')
data['Price_persqft']=data.Price_persqft.str.replace('$','')









X= data.loc[:,['Area','Parking','Bedroom','Bathroom','Price_persqft','Yearbuilt']].values
Y=data.loc[:,['Cost']].values
print(X)
print(Y)
X=X.astype(np.float64)
Y=Y.astype(np.float64)


#imputer
from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="most_frequent",axis=0)
imputer=imputer.fit(X[:,2:6])
X[:,2:6]=imputer.transform(X[:,2:6])
#
from sklearn.model_selection import cross_val_score, train_test_split
from sklearn.linear_model import LinearRegression, LassoCV, RidgeCV
from sklearn.preprocessing import StandardScaler, PolynomialFeatures
import statsmodels.api as sm # with this i can do a quick linear regression
import seaborn as sns
import matplotlib.pyplot as plt
import matplotlib.lines as mlines
import matplotlib.transforms as mtransforms
#

p = PolynomialFeatures()
features_poly = p.fit_transform(X)
poly_df = pd.DataFrame(features_poly, columns=p.get_feature_names())


#spliting

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.4, random_state=0)



#scaler
from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
sc_y=StandardScaler()
X=sc_x.fit_transform(X)
Y=sc_y.fit_transform(Y)

#Multilinear
from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
regressor.score(X_test,y_test)

#Random
from sklearn.ensemble import RandomForestRegressor
random = RandomForestRegressor(n_estimators=10,random_state=42)
random.fit(X_train, y_train)
#print the value of random forest value
print('train score: ',random.score(X_train,y_train))
print('test score: ',random.score(X_test, y_test))
randomforest=random.score(X_test, y_test)
y_predict=random.predict(X_test)

#svr
from sklearn.svm import SVR
svr= SVR(kernel='rbf')
svr.fit(X_train, y_train) 
svr.score(X_test,y_test)


#rigid
ridge = RidgeCV()
ridge.fit(X_train, y_train)
ridge.score(X_train, y_train)
ridge.score(X_test,y_test)

#lassso
lasso = LassoCV()
lasso.fit(X_train, y_train)
lasso.score(X_test,y_test)
lasso.score(X_train,y_train)

#knn
import numpy as np
from sklearn.model_selection import GridSearchCV
from sklearn.neighbors import KNeighborsClassifier
#create new a knn model
knn = KNeighborsClassifier()
#create a dictionary of all values we want to test for n_neighbors
params_knn = {'n_neighbors': np.arange(1, 25)}
#use gridsearch to test all values for n_neighbors
knn_gs = GridSearchCV(knn, params_knn, cv=1)
#fit model to training data
knn_gs.fit(X_train, y_train)

import pandas
from sklearn import model_selection
from sklearn.ensemble import BaggingClassifier
from sklearn.tree import DecisionTreeClassifier

X= data.loc[:,['Area','Parking','Bedroom','Bathroom','Price_persqft','Yearbuilt']].values
Y=data.loc[:,['Cost']].values
seed = 7
kfold = model_selection.KFold(n_splits=10, random_state=seed)
cart = DecisionTreeClassifier()
num_trees = 100
model = BaggingClassifier(base_estimator=cart, n_estimators=num_trees, random_state=seed)
results = model_selection.cross_val_score(model, X, Y, cv=kfold)
print(results.mean())
#
from sklearn.ensemble import RandomForestClassifier
seed = 7
num_trees = 100
max_features = 3
kfold = model_selection.KFold(n_splits=10, random_state=seed)
model = RandomForestClassifier(n_estimators=num_trees, max_features=max_features)
results = model_selection.cross_val_score(model, X, Y, cv=kfold)
print(results.mean())

#adaboost
from sklearn.ensemble import AdaBoostClassifier
seed = 7
num_trees = 30
kfold = model_selection.KFold(n_splits=10, random_state=seed)
model = AdaBoostClassifier(n_estimators=num_trees, random_state=seed)
results = model_selection.cross_val_score(model, X, Y, cv=kfold)
print(results.mean())

#
from sklearn.ensemble import GradientBoostingClassifier
seed = 7
num_trees = 100
kfold = model_selection.KFold(n_splits=10, random_state=seed)
model = GradientBoostingClassifier(n_estimators=num_trees, random_state=seed)
results = model_selection.cross_val_score(model, X, Y, cv=kfold)
print(results.mean())