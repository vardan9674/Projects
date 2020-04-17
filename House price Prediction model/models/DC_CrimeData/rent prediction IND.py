#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue May 21 11:12:46 2019

@author: vardanchennupati
"""
#
import pandas as pd
import numpy as np
import seaborn as sns
import mpl_toolkits
import matplotlib.pyplot as plt
import re
#
data=pd.read_csv('new5')
cd=pd.read_csv('IMPD_UCR_Current_Year.csv')

#Droppping unessary columns

df=data.drop(data.columns[[0,2,3,4,11,12,14,15,17,19,20,21]],axis=1)
df.describe()

#Renaming the index 
df = df.rename(columns = {"zip": "Zip","cost/rent":"Cost","status":"Status","bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","Price/sqft:":"Price_persqft","Year built:":"Yearbuilt"}) 


#replacing values

df['Parking'].value_counts()
df=df.replace('No Data','0')
df=df.replace('1 space','1')
df=df.replace('2 spaces','2')
df=df.replace('Attached Garage',np.nan)
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
df.head()
print(df.info())

#
df['Parking'] = df.Parking.astype(float)

df.Yearbuilt.astype(float)
#removing elements from the dataset
df['Cost'] = df.Cost.str.replace(',','')
df['Cost']=df.Cost.str.replace('$','').astype(float)
df['Price_persqft'] = df.Price_persqft.str.replace(',','')
df['Price_persqft']=df.Price_persqft.str.replace('$','').astype(float)
#frequency count
data['address'].value_counts()
cd['ADDRESS'].value_counts()


#merging the data
df3=pd.join('cd','data')






#anlysizing the data
df.hist(bins=50, figsize=(20,15))
plt.savefig("attribute_histogram_plots")
plt.show()

#
df['bed'].value_counts().plot(kind='bar')
plt.title('number of Bedroom')
plt.xlabel('bed')
plt.ylabel('Count')
sns.despine

#
plt.scatter(df.bed,df.cost)
plt.title("Bedroom and Price ")
plt.xlabel("bed")
plt.ylabel("cost")
plt.show()
sns.despine

#missing values
df.isnull().sum()

#corr
corr_matrix = df.corr()
corr_matrix['Cost'].sort_values(ascending=False)
corr_matrix



#cluster
len(df['address'].value_counts())
freq = df.groupby('address').count()['Cost']
mean = df.groupby('address').mean()['Price_persqft']
cluster = pd.concat([freq, mean], axis=1)
cluster['Address'] = cluster.index
cluster.columns = ['freq', 'Price_PerSQFT','Address']
cluster.describe()

#checking low price address
lowpriced = cluster[cluster.Price_PerSQFT < 250]
lowpriced.index

#high price address
highpriced = cluster[cluster.Price_PerSQFT >= 327]
highpriced.index

#
X = df[['Bathroom', 'Bedroom', 'Price_persqft', 'Yearbuilt', 'Area','Parking']]
Y = df['Cost']


Y = Y.astype(np.float64)
#

print(X.isnull().sum())
print(Y.isnull().sum())
from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="most_frequent",axis=0)
imputer=imputer.fit(X.values[:,:])
X.values[:, :]=imputer.fit_transform(X.values[:,:])

#checking if missing values are still present
print(X.isnull().sum())
print(Y.isnull().sum())
X=X.astype(np.float64)

#
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.25, random_state=0)

#normlization
from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X_train=sc_x.fit_transform(X_train)
X_test=sc_x.transform(X_test)
#for reshaping the data "if nessary"
y_train = y_train.values.reshape(-1,1)
#fit the the data for normailazation
y_train=sc_x.fit_transform(y_train)
#reshaping the data
y_test = y_test.values.reshape(-1,1)
#fittung the data
y_test=sc_x.fit_transform(y_test)

#random forest
from sklearn.ensemble import RandomForestRegressor
forest_reg = RandomForestRegressor(random_state=42)
forest_reg.fit(X_train, y_train)
#print the value of random forest value
forest_reg.score(X_test, y_test)
randomforest=forest_reg.score(X_test, y_test)
y_predict=forest_reg.predict(X_test)

#
forest_reg.score(y_predict,y_test)

from sklearn.model_selection import cross_val_score,cross_val_predict
from sklearn import metrics
#scores
scores_dt = cross_val_score(forest_reg, X_train, y_train, cv=10)
scores_dt
scores_dt.mean()   
print("Randomforest",scores_dt.mean())


#decission tree
from sklearn.tree import DecisionTreeClassifier 

Dt = DecisionTreeClassifier()
Dt.fit(X_train, y_train)
Dt.score(X_test,y_test)

#stanard scalar
#knn model
###using for loop
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import cross_val_score,cross_val_predict
K_score = 0;
val = 0;
for i in range(1,51):
    knn = KNeighborsClassifier(n_neighbors=i)
    knn_model=knn.fit(X_train, y_train)
    scores_knn = cross_val_score(knn_model, X_train, y_train, cv=10)
    knn_score=scores_knn.mean()
    if(knn_score > K_score):
        K_score=knn_score
        val=i
print("knn_score",K_score)
print("k_value:",val)



knn = KNeighborsClassifier(n_neighbors=20)
knn_model=knn.fit(X_train, y_train)
knn_model.score(X_test,y_test)
    