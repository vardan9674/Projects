#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jul  8 08:48:25 2019

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


data= pd.read_csv('NY_Sale_Condo.csv')

data = data.rename(columns = {"Bedrooms":"Bedroom","Bathrooms":"Bathroom","AreaSpace_SQFT":"Area","Parking:":"Parking","cost/rent":"Rent","Price/sqft:":"Price_sqft","Year built:":"Yearbuilt"}) 



#price_persqft
data['Price_Sqft']=data.Price/data.Area

#Replacing for rent
data['Price'] = data.Price.replace(',','')
data['Price']=data.Price.replace('$','')
data['Price']=data.Price.replace('C','')

#
data['YearBuilt']=data.YearBuilt.replace('0',np.nan)

data['Bathroom'] = data.Bathroom.replace('25.0','2.5')
data['Bathroom']=data.Bathroom.replace('35.0','3.5')
data['Bathroom']=data.Bathroom.replace('45.0','4.5')
data['Bathroom']=data.Bathroom.replace('15.0','1.5')
data['Bathroom']=data.Bathroom.replace('145.0','4.5')
data['Bathroom']=data.Bathroom.replace('55.0','5.5')
data['Bathroom']=data.Bathroom.replace('65.0','6.5')
data['Bathroom']=data.Bathroom.replace('85.0','8.5')
data['Bathroom']=data.Bathroom.replace('95.0','9.5')
data['Bathroom']=data.Bathroom.replace('75.0','7.5')
data['Bathroom']=data.Bathroom.replace('14','4')
data['Bathroom']=data.Bathroom.replace('17.5','7.5')
data['Bathroom']=data.Bathroom.replace('37.5','7.5')
data['Bathroom']=data.Bathroom.replace('42.5','2.5')
data['Bathroom']=data.Bathroom.replace('57.5','7.5')
data['Bathroom']=data.Bathroom.replace('27.5','7.5')
data['Bathroom']=data.Bathroom.replace('18.5','8')
data['Bathroom']=data.Bathroom.replace('18','8')
data['Bathroom']=data.Bathroom.replace('22.5','2.5')
data['Bathroom']=data.Bathroom.replace('20','2')
data['Bathroom']=data.Bathroom.replace('32.5','3.5')
data['Bathroom']=data.Bathroom.replace('12.0','2')
data['Bathroom']=data.Bathroom.replace('13.0','3')
data['Bathroom']=data.Bathroom.replace('27.5','7.5')

data['ZipCode']=data.ZipCode.replace('--',np.nan)
data['ZipCode']=data.ZipCode.replace('',np.nan)
data['ZipCode']=data.ZipCode.astype(float)

data['YearBuilt']=data.YearBuilt.replace('No Data',np.nan)

data['Bedroom']=data.Bedroom.astype(float)
data['Bathroom']=data.Bathroom.astype(float)
data['Price']=data.Price.astype(float)
data['Price_Sqft']=data.Price_Sqft.astype(float)
data['YearBuilt']=data.YearBuilt.astype(float)

data['ZipCode']=data.ZipCode.astype(float)
data['Area']=data.Area.astype(float)





df=data[['Area','Price_Sqft','YearBuilt','Bedroom','Bathroom','ZipCode','Price']]

#missing values 
df_MissingValues=df.isnull().sum()
df=df.dropna()
df=df.astype(int)
print("missing values in dataframe are:\n",df.isnull().sum())
###

sns.boxplot(df['Area'])
sns.boxplot(df['Price'])
sns.boxplot(df['YearBuilt'])


#clustering

plt.scatter(df['Area'],df['Price'] )

km=KMeans(n_clusters=3)
k_pred=km.fit_predict(df[['Area','Price']])
k_pred

df['cluster']=k_pred

df1=df[df.cluster==0]
df2=df[df.cluster==1]
df3=df[df.cluster==2]

plt.scatter(df1.Area,df1['Price'],color='green')
plt.scatter(df2.Area,df2['Price'],color='red')
plt.scatter(df3.Area,df3['Price'],color='blue')


df=df.astype(int)
df=df[['Area','Price_Sqft','YearBuilt','Bedroom','Bathroom','ZipCode','cluster','Price']]

df1=df.values

#checking for outlinears
X=df1[:,0:7]
y=df1[:,7]

#
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,5]=labelencoder_x.fit_transform(X[:,5])


from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X=sc_x.fit_transform(X)

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=.3, random_state=42,shuffle=True)

random_forest=RandomForestRegressor(n_estimators=100)
random_forest.fit(X_train,y_train)
rand_pred=random_forest.predict(X_test)
print('train score for random_forest:',random_forest.score(X_train,y_train))
print('test score for random_forest:',random_forest.score(X_test,y_test))

#predicted price
y_pred=random_forest.predict(X_test)

#
print("Predicted Prices for Single Family House in Ca",y_pred)

from sklearn.model_selection import cross_val_score
clf = RandomForestRegressor()
scores = cross_val_score(clf, X_test, y_test, cv=5)

#bagging
bg=BaggingRegressor(RandomForestRegressor(),n_estimators=10)
bg.fit(X_train,y_train)
bg.score(X_train,y_train)
bg.score(X_test,y_test)



#mse in $
mse=mean_absolute_error(y_test,y_pred)
print( "The mean absolute error is:$",mse)
#chceking r^2
from sklearn.metrics import r2_score

print("r_Score:",r2_score(y_test, y_pred))  

print("Predicted Prices for Single Family House in Ca",y_test,y_pred)

print("crossvalidation:",scores.mean())