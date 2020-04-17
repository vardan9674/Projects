#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jun 11 15:35:06 2019

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
from sklearn.cluster import KMeans
%matplotlib inline


#

data=pd.read_csv('VA_rent.csv')


data = data.rename(columns = {"bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","cost/rent":"Rent"}) 

#replacing for attribute parking
data['Parking'].value_counts()
data=data.replace('No Data','0')
data=data.replace('1 space','1')
data=data.replace('2 spaces','2')
data=data.replace('Attached Garage','1')
data=data.replace('3 spaces','3')
data=data.replace('4 spaces','4')
data=data.replace('Carport','1')
data=data.replace('Off street, On street','1')
data=data.replace('6 spaces','6')
data=data.replace('None','0')
data=data.replace('5 spaces','5')
data=data.replace('8 spaces','8')
data=data.replace('off street','1')
data=data.replace('Off street, Attached Garage','1')
data=data.replace('Off street','1')
data=data.replace('On street','1')
data=data.replace('Detached Garage','2')
data=data.replace('11 spaces','11')
data=data.replace('Carport, Off street, On street','1')
data=data.replace('Carport, Off street','1')
data=data.replace('Attached Garage, Detached Garage','1')
data=data.replace('Off street, On street, Attached Garage','1')
data=data.replace('None, Attached Garage','1')
data=data.replace('10 spaces','10')
data=data.replace('Carport, Attached Garage, Detached Garage','1')
data=data.replace('Some Parking','1')
data=data.replace('Off street, Detached Garage','1')
data=data.replace('Carport, On street','1')
data=data.replace('On street, Attached Garage','1')


#Replacing for rent
data['Rent'] = data.Rent.str.replace(',','')
data['Rent']=data.Rent.str.replace('$','')

#Replacing for Bathroom
data['Bathroom'] = data.Bathroom.astype(str).str.replace('25.0','2.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('35.0','3.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('45.0','4.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('15.0','1.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('145.0','4.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('55.0','5.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('65.0','6.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('85.0','8.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('95.0','9.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('75.0','7.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('14','4')

#converting Bathroom and Rent to float
data['Bathroom']=data.Bathroom.astype(float)
data['Rent']=data.Rent.astype(float)

df=data[['Area','Bedroom','Bathroom','Parking','zip','Rent']]

df=df.dropna()

plt.scatter(df['Area'],df['Rent'])


km = KMeans(n_clusters=3)
km

y_pred=km.fit_predict(df[['Area','Rent']]) 

df['cluster']=y_pred
df.head(5)

df1 = df[df.cluster==0]
df2 = df[df.cluster==1]
df3 = df[df.cluster==2]

plt.scatter(df1.Area,df1.Rent,color='green')
plt.scatter(df2.Area,df2.Rent,color='Red')
plt.scatter(df3.Area,df3.Rent,color='Blue')

plt.xlabel('Area')
plt.ylabel('Rent')
#
from sklearn.preprocessing import MinMaxScaler
sc_x=MinMaxScaler()
sc_x.fit(df[['Area']])
df['Area']=sc_x.transform(df)

df

from sklearn.preprocessing import MinMaxScaler
sc=MinMaxScaler()
sc.fit(df[['Rent']])
df['Rent']=sc.transform(df)



km = KMeans(n_clusters=3)
km

y_pred=km.fit_predict(df[['Area','Rent']]) 
df['cluster']=y_pred
df.head(5)

def get_score(model,X_train,X_test,y_train,y_test):