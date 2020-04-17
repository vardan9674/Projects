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
data=pd.read_csv('new5.csv')
data1=pd.read_csv('New_Crime_Watch.csv')

#Droppping unessary columns

df=data.drop(data.columns[[0,1,2,3,4,6,7,11,12,14,15,19,20,21]],axis=1)
df.describe()
data.isnull().sum()
df.isnull().sum()
#Renaming the index 
df = df.rename(columns = {"zip": "Zip","cost/rent":"Cost","status":"Status","bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","Price/sqft:":"Price_persqft","Year built":"Yearbuilt","Lot:":"Lot"}) 

#replacing values

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


#replacing na with zeros for converting float to int

#Lot
df['Lot']=df.Lot.str.replace(',','')
df['Lot']=df.Lot.str.replace('sqft','')
df['Lot']=df.Lot.str.replace('.','')
df['Lot']=df.Lot.str.replace('acres','')
df['Lot'] = df['Lot'].fillna(0)
df['Lot'] = df['Lot'].astype(int)

df['Lot'] = df['Lot'].replace('0', np.nan)




#
df['Parking']=df.Parking.astype(int)
#bathroom
df['Bathroom'] = df['Bathroom'].fillna(-1)
df['Bathroom'] = df['Bathroom'].astype(int)
df['Bathroom'] = df['Bathroom'].replace('-1', np.nan)
#bedroom
df['Bedroom'] = df['Bedroom'].fillna(-1)
df['Bedroom'] = df['Bedroom'].astype(int)
df['Bedroom'] = df['Bedroom'].replace('-1', np.nan)
df['Bedroom']=df.Bedroom.astype(float)

#yearbulit

df['Yearbuilt'] = df['Yearbuilt'].fillna(-1)
df['Yearbuilt'] = df['Yearbuilt'].astype(int)
df['Yearbuilt'] = df['Yearbuilt'].replace('-1', np.nan)

#removing elements from the dataset
df['Cost'] = df.Cost.str.replace(',','')
df['Cost']=df.Cost.str.replace('$','')
df['Cost']=df.Cost.astype(int)

#sqft
df['Price_persqft'] = df['Price_persqft'].fillna(-1)
df['Price_persqft'] = df['Price_persqft'].astype(int)

df['Price_persqft'] = df['Price_persqft'].replace('-1', np.nan)
df['Price_persqft'] = df.Price_persqft.str.replace(',','')
df['Price_persqft']=df.Price_persqft.str.replace('$','')


#
df.head()
df.info()


values = df.values

# Now impute it
imputer = Imputer()
imputedData = imputer.fit_transform(values)



#skip to labeling part
#taking a look at the cost attribute
df['Cost'].describe()
data['cost/rent'].describe()

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
cols=corr_matrix['Cost'].sort_values(ascending=False)
corr_matrix
most_corr = pd.DataFrame(cols)
most_corr.columns = ['Most Correlated Features']
most_corr


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


#missing values percentage
df_missing=df.isnull().sum()
len(df)
df_missing/len(df)






#X label and y label
X=df.iloc[:,4:]
X = df[['Bedroom', 'Price_persqft', 'Yearbuilt', 'Area','Parking']]
Y = df['Cost']

#run this before u run imputer


#for checking the missing data

print(df.isnull().sum())
print(X.isnull().sum())
print(Y.isnull().sum())
"""
# using for loop for missing values
cnt=0
for row  in df['Yearbuilt']:
    try:
        int(row)
        df.loc[cnt,'Yearbuilt']=np.nan
    except ValueError:
        pass
    cnt+=1
/
"""


# using imputer for missing values
from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="most_frequent",axis=0)
imputer=imputer.fit(X.values[:,:])
X.values[:,:]=imputer.fit_transform(X.values[:,:])

X=X.astype(np.float64)
Y=Y.astype(np.float64)


#checking if missing values are still present
print(X.isnull().sum())

#
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.40, random_state=0)

#dont run this step this is for normalization

from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X_train=sc_x.fit_transform(X_train)
X_test=sc_x.transform(X_test)
#for reshaping the data "if nessary"
y_train = y_train.values.reshape(-1,1)
y_train=sc_x.fit_transform(y_train)
y_test = y_test.values.reshape(-1,1)
y_test=sc_x.fit_transform(y_test)

#random forest model
from sklearn.ensemble import RandomForestRegressor
forest_reg = RandomForestRegressor(random_state=42)
forest_reg.fit(X_train, y_train)
#print the value of random forest value
print('train score: ',forest_reg.score(X_train,y_train))
print('test score: ',forest_reg.score(X_test, y_test))
randomforest=forest_reg.score(X_test, y_test)
y_predict=forest_reg.predict(X_test)


#cross validation for random forest
from sklearn.model_selection import cross_val_score,cross_val_predict
from sklearn import metrics
#scores
scores_dt = cross_val_score(forest_reg, X_train, y_train, cv=10)
scores_dt
scores_dt.mean()   
print("Randomforest",scores_dt.mean())
##
from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
regressor.score(X_test,y_test)

X_train=X_train.values
X_test=X_test.values
y_test=y_test.values
y_train=y_train.values
#####
from sklearn.preprocessing import LabelEncoder
from sklearn import preprocessing
from sklearn import utils
lab_enc = preprocessing.LabelEncoder()
encoded = lab_enc.fit_transform(X_train)
###

from sklearn.svm import SVC
clf = SVC(gamma='auto')
clf.fit(X_train, y_train) 
clf.score(X_test,y_test)

###
from sklearn import neighbors
from sklearn.metrics import mean_squared_error 
from math import sqrt
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import GridSearchCV
params = {'n_neighbors':[2,3,4,5,6,7,8,9]}

knn = neighbors.KNeighborsRegressor()


model = GridSearchCV(knn, params, cv=5)
model.fit(X_train,y_train)
model.best_params_
model.score(X_test,y_test)

print('knn: {}'.format(model.score(X_test, y_test)))
print('rf: {}'.format(forest_reg.score(X_test, y_test)))
print('log_reg: {}'.format(regressor.score(X_test, y_test)))
###
from sklearn import svm
clf = svm.SVC(gamma='scale')
clf.fit(X_train, y_train)  
svm_pred=clf.predict(X_test)
clf.score(X_test,y_test)
####voting classifer
from sklearn.ensemble import VotingClassifier
#create a dictionary of our models
estimators=[('knn',model), ('rf', forest_reg), ('log_reg', regressor)]
#create our voting classifier, inputting our models
ensemble = VotingClassifier(estimators, voting='hard')
#fit model to training data
ensemble.fit(X_train, y_train)
#test our model on the test data
ensemble.score(X_test, y_test)
