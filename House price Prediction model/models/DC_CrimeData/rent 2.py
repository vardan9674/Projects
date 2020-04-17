#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun May 26 10:02:15 2019

@author: vardanchennupati
"""

#importing libabries
import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt
import re

#importing the dataset
data=pd.read_csv("new5")
df=data.drop(data.columns[[0,2,3,4,11,12,14,15,19,20,21]],axis=1)

#renaming the attributes
df = df.rename(columns = {"zip": "Zip","cost/rent":"Cost","status":"Status","bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","Price/sqft:":"Price_persqft","Year built:":"Yearbuilt","Lot:":"Lot"}) 

#preprocessing

#replacing the atrributes

#cost
df['Cost']= df.Cost.str.replace(',','')
df['Cost']=df.Cost.str.replace('$','')

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


#
df['Parking']=df.Parking.astype('int')
df['Cost']=df.Cost.astype('int')
df['Area']=df.Area.astype(int)
#checking value count
df['Lot'].value_counts()
df.info()

#Lot
meadian_value=df['Lot'].median()
df['Lot']=df['Lot'].fillna(meadian_value)
df['Lot']=df.Lot.astype(int)

#Yearbuilt
most=df['Yearbuilt'].median()
df['Yearbuilt']=df['Yearbuilt'].fillna(most)
df['Yearbuilt']=df.Yearbuilt.astype(int)

#Bedroom
most1=df['Bedroom'].median()
df['Bedroom']=df['Bedroom'].fillna(most1)
df['Bedroom']=df.Bedroom.astype(int)

#Bathroom
most2=df['Bathroom'].median()
df['Bathroom']=df['Bathroom'].fillna(most2)
df['Bathroom']=df.Bathroom.astype(int)

#price/sqft
most3=df['Price_persqft'].median()
df['Price_persqft']=df['Price_persqft'].fillna(most3)
df['Price_persqft']=df.Bathroom.astype(int)

df.info()
'''
#using imputer to for missing values
from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="median",axis=0)
imputer=imputer.fit(df.values[:,10])
df.values[:,10]=imputer.fit_transform(df.values[:,10])

'''
#
X = df[['Bathroom', 'Bedroom','Yearbuilt', 'Price_persqft', 'Area','Lot','Parking']]
Y = df['Cost']




#
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.25, random_state=42)


from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X_train=sc_x.fit_transform(X_train)
X_test=sc_x.transform(X_test)
y_train = y_train.values.reshape(-1,1)
y_train=sc_x.fit_transform(y_train)
y_test = y_test.values.reshape(-1,1)
y_test=sc_x.fit_transform(y_test)

#linerregression
from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
regressor.score(X_test,y_test)

#randomforest
from sklearn.ensemble import RandomForestRegressor
forest_reg = RandomForestRegressor(random_state=42)
forest_reg.fit(X_train, y_train)
print('train score: ',forest_reg.score(X_train,y_train))
print('test score: ',forest_reg.score(X_test, y_test))
y_predict=forest_reg.predict(X_test)


#knn model
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import cross_val_score,cross_val_predict

    knn = KNeighborsClassifier()
    knn_model=knn.fit(X_train, y_train)
    knn_model.score(X_test,y_test)