#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed May 29 10:44:21 2019

@author: vardanchennupati
"""

import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt

data= pd.read_csv('VA_rent.csv')

np.random.seed(0)

#checking the data

data.sample(5)

#missing data
data=data.replace('No Data','0')
missing_data=data.isnull().sum()
#percentage of missing data
total_cell=np.product(data.shape)
total_missing=missing_data.sum()

#calculating the percentage
(total_missing/total_cell)*100

df=data.drop(data.columns[[0,1,2,3,5,6,7,8,9,10,15,16,17,18,19,20,21,22]],axis=1)

df=df.drop([57],axis=0)
df = df.rename(columns = {"bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","cost/rent":"Rent"}) 


#parking
df['Parking'].value_counts()
df=df.replace('No Data','0')
df=df.replace('1 space','1')
df=df.replace('2 spaces','2')
df=df.replace('Attached Garage','1')
df=df.replace('3 spaces','3')
df=df.replace('4 spaces','4')
df=df.replace('Carport','1')
df=df.replace('Off street, On street','0')
df=df.replace('6 spaces','6')
df=df.replace('None','0')
df=df.replace('5 spaces','5')
df=df.replace('8 spaces','8')
df=df.replace('off street','0')
df=df.replace('Off street, Attached Garage','1')
df=df.replace('Off street','0')
df=df.replace('On street','1')
df=df.replace('Detached Garage','1')
df=df.replace('11 spaces','11')
df=df.replace('Carport, Off street, On street','1')
df=df.replace('Carport, Off street','1')
df=df.replace('Attached Garage, Detached Garage','1')
df=df.replace('Off street, On street, Attached Garage','1')
df=df.replace('None, Attached Garage','0')
df['Parking']=df.Parking.astype(int)

#cost
df['Rent'] = df.Rent.str.replace(',','')
df['Rent']=df.Rent.str.replace('$','')
df['Rent']=df.Rent.astype(int)

df.isnull().sum()
#
df['Bathroom'].value_counts()

X=df.iloc[:,:-1].values
Y=df.iloc[:,4].values

#replacing missing values
from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="mean",axis=0)
imputer=imputer.fit(X[:,:])
X[:,:]=imputer.transform(X[:,:])

X.isnull().sum()
# training and testting
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=.3, random_state=42)


from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X_train=sc_x.fit_transform(X_train)
X_test=sc_x.transform(X_test)
y_train=sc_x.fit_transform(y_train)
y_test=sc_x.fit_transform(y_test)

from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
print(regressor.score(X_train,y_train))
print(regressor.score(X_test,y_test))


from sklearn.ensemble import RandomForestRegressor
random = RandomForestRegressor(random_state=42)
random.fit(X_train, y_train)
#print the value of random forest value
print('train score: ',random.score(X_train,y_train))
print('test score: ',random.score(X_test, y_test))
randomforest=random.score(X_test, y_test)
y_predict=random.predict(X_test)

from sklearn.model_selection import cross_val_score
clf = RandomForestRegressor()
scores = cross_val_score(clf, X_test, y_test, cv=5)
scores.mean()                                              

import numpy as np
from sklearn.model_selection import GridSearchCV
from sklearn.neighbors import KNeighborsClassifier
#create new a knn model
knn = KNeighborsClassifier()
#create a dictionary of all values we want to test for n_neighbors
params_knn = {'n_neighbors': np.arange(1, 25)}
#use gridsearch to test all values for n_neighbors
knn_gs = GridSearchCV(knn, params_knn, cv=5)
#fit model to training data
knn_gs.fit(X_train, y_train)

knn_best = knn_gs.best_estimator_
#check best n_neigbors value
print(knn_gs.best_params_)


from sklearn.ensemble import RandomForestClassifier
#create a new random forest classifier
rf = RandomForestClassifier()
#create a dictionary of all values we want to test for n_estimators
params_rf = {'n_estimators': [50, 100, 200]}
#use gridsearch to test all values for n_estimators
rf_gs = GridSearchCV(rf, params_rf, cv=5)
#fit model to training data
rf_gs.fit(X_train, y_train)

#save best model
rf_best = rf_gs.best_estimator_
#check best n_estimators value
print(rf_gs.best_params_)


from sklearn.linear_model import LogisticRegression
#create a new logistic regression model
log_reg = LogisticRegression()
#fit the model to the training data
log_reg.fit(X_train, y_train)

print('knn: {}'.format(knn_best.score(X_test, y_test)))
print('rf: {}'.format(rf_best.score(X_test, y_test)))
print('log_reg: {}'.format(log_reg.score(X_test, y_test)))


from sklearn.ensemble import VotingClassifier
#create a dictionary of our models
estimators=[('knn', knn_best), ('rf', rf_best), ('log_reg', log_reg)]
#create our voting classifier, inputting our models
ensemble = VotingClassifier(estimators, voting='hard')

#it model to training data
ensemble.fit(X_train, y_train)
#test our model on the test data
ensemble.score(X_test, y_test)