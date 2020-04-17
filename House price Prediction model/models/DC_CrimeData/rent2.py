#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun May 26 12:25:38 2019

@author: vardanchennupati
"""

import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt
import re

#importing the dataset
data=pd.read_csv("new5")
df=data.drop(data.columns[[0,2,3,4,7,11,12,14,15,19,20,21]],axis=1)

#renaming the attributes
df = df.rename(columns = {"zip": "Zip","cost/rent":"Cost","status":"Status","bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","Price/sqft:":"Price_persqft","Year built:":"Yearbuilt","Lot:":"Lot"}) 

#preprocessing

#replacing the atrributes

#cost
df['Cost']= df.Cost.str.replace(',','')
df['Cost']=df.Cost.str.replace('$','')

#status
df['Status']=df.Status.str.replace('For sale by owner','For sale')
#price_persqft
df['Price_persqft']=df.Price_persqft.str.replace('$','')
df['Price_persqft']=df.Price_persqft.str.replace(',','')

#Parking

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


#parking
"""
df['Parking']=df.Parking.replace('No Data',np.nan)
df['Parking']=df.Parking.str.replace('1 space','1')
df['Parking']=df.Parking.str.replace('2 spaces','2')
df['Parking']=df.Parking.str.replace('Attached Garage','1')
df['Parking']=df.Parking.str.replace('3 spaces','3')
df['Praking']=df.Parking.str.replace('4 spaces','4')
df['Parking']=df.Parking.str.replace('Carport','1')
df['Parking']=df.Parking.str.replace('Off street, On street','0')
df['Parking']=df.Parking.str.replace('6 spaces','6')
df['Parking']=df.Parking.str.replace('None','0')
df['Parking']=df.Parking.str.replace('5 spaces','5')
df['Parking']=df.Parking.str.replace('8 spaces','8')
df['Parking']=df.Parking.str.replace('off street','0')
df['Parking']=df.Parking.str.replace('Off street, Attached Garage','1')
df['Parking']=df.Parking.str.replace('Off street','0')
df['Parking']=df.Parking.str.replace('On street','1')
df['Parking']=df.Parking.str.replace('Detached Garage','1')
df['Parking']=df.Parking.str.replace('11 spaces','11')
"""
#Lot

df['Lot']=df.Lot.str.replace(',','')
df['Lot']=df.Lot.str.replace('sqft','')
df['Lot']=df.Lot.str.replace('.','')
df['Lot']=df.Lot.str.replace('acres','')

#dropping the missing values

df=df.dropna()

#
df['Parking']=df.Parking.astype(int)
df['Cost']=df.Cost.astype(int)
df['Area']=df.Area.astype(int)
df['Lot']=df.Lot.astype(int)
df['Yearbuilt']=df.Yearbuilt.astype(int)
df['Price_persqft']=df.Bathroom.astype(int)
df['Bathroom']=df.Bathroom.astype(int)
df['Bedroom']=df.Bedroom.astype(int)


X = df[['Bathroom', 'Bedroom','Yearbuilt', 'Price_persqft', 'Area','Lot','Parking']].values
Y = df['Cost'].values

#
X = X.astype(np.float64)
'''
#label encoder
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,7]=labelencoder_x.fit_transform(X[:,7])
onehotencoder=OneHotEncoder(categorical_features=[2])
X= onehotencoder.fit_transform(X).toarray()
'''

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.25, random_state=0)


from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
regressor.score(X_test,y_test)

#predition 
y_pred = regressor.predict(X_test)

from sklearn.ensemble import RandomForestRegressor
forest_reg = RandomForestRegressor(random_state=42)
forest_reg.fit(X_train, y_train)
#print the value of random forest value
print('train score: ',forest_reg.score(X_train,y_train))
print('test score: ',forest_reg.score(X_test, y_test))
randomforest=forest_reg.score(X_test, y_test)
y_predict=forest_reg.predict(X_test)


##
from sklearn.svm import SVC
 clf = SVC(gamma='auto')
 clf.fit(X_train, y_train) 
clf.score(X_test,y_test)

###
from sklearn.neighbors import KNeighborsClassifier
knn = KNeighborsClassifier()

knn.fit(X_train, y_train)

