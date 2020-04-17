#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Apr 18 13:25:37 2019

@author: vardanchennupati
"""

#importing libabrys
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import mpl_toolkits

#importing the dataset

data = pd.read_csv('')
data=data.replace('0',np.nan)
#dropping unwanted coloumns

df1=data.drop(data.columns[[2,3,4,5,9,10,12,13,14,15,16,17,18,19,22,23,24,26,27,28,29,33,34,36]],axis=1)
df1.describe()

#converting sale as factor
df['Status'] = df['Status'].astype('category')
df.dtypes
df['Status'] = df['Status'].cat.codes
#
df['EstimatedRent'] = df['EstimatedRent'].replace(0,np.nan)
df['EstimatedRent'] = df['EstimatedRent'].replace(1,np.nan)
df['YearBuilt'] = df['YearBuilt'].replace(-1,np.nan)
#using one paticular zipcode for predicting 20105
df=df1.iloc[:240,:]


#replacing 0 with nan
df=df.replace("0", np.nan)

#plotting histogram for each variable
df.hist(bins=50, figsize=(20,15))
plt.savefig("attribute_histogram_plots")
plt.show()

#plotting number of bedroom in the data
df['Bedrooms'].value_counts().plot(kind='bar')
plt.title('number of Bedroom')
plt.xlabel('Bedrooms')
plt.ylabel('Count')
sns.despine

#plotting bedroom vs price 
plt.scatter(df.Bedrooms,df.Price)
plt.title("Bedroom and Price ")
plt.xlabel("Bedrooms")
plt.ylabel("Price")
plt.show()
sns.despine

#

#correlation mattrix
corr_matrix = data.corr()
corr_matrix["Price"].sort_values(ascending=False)

#plotting using scatterplot
from pandas.tools.plotting import scatter_matrix
attributes = ["Price", "Bathrooms", "Bedrooms", "ZEstimatePrice"]
scatter_matrix(df[attributes], figsize=(12, 8))
plt.savefig('matrix.png')

#cluster
len(df['Address'].value_counts())
freq = df.groupby('Address').count()['ZipCode']
mean = df.groupby('Address').mean()['Price_PerSQFT']
cluster = pd.concat([freq, mean], axis=1)
cluster['Address'] = cluster.index
cluster.columns = ['freq', 'Price_PerSQFT','Address']
cluster.describe()

#checking low price address
lowpriced = cluster[cluster.Price_PerSQFT < 197]
lowpriced.index

#high price address
highpriced = cluster[cluster.Price_PerSQFT >= 197]
highpriced.index

#
X = df[['Bathrooms', 'Bedrooms', 'Price_PerSQFT', 'YearBuilt', 'AreaSpace_SQFT','Status']]
Y = df['Price']

#dealing with missing values.
"print the number of null values presnet in the data frame"
print(df.isnull().sum())

from sklearn.preprocessing import Imputer
imputer=Imputer(missing_values='NaN', strategy="most_frequent",axis=0)
imputer=imputer.fit(X.values[:,:3])
X.values[:, 3]=imputer.fit_transform(X.values[:,3])

###using knn for missing values

knn_impute(target=df['EstimatedRent']), k_neighbors=10, numeric_distance='euclidean',
                                    categorical_distance='hamming', missing_neighbors_threshold=0.8)

#using knn for preditcing price 
 




###
conv_dates = [1 if values == 2017 else 0 for values in df.YearBuilt ]
df['YearBuilt'] = conv_dates




#train and testing data
#using multilinear regression to test the model

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.25, random_state=0)

#####scaling
from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X_train=sc_x.fit_transform(X_train)
X_test=sc_x.transform(X_test)
y_train=sc_x.fit_transform(y_train)
y_test=sc_x.fit_transform(y_test)

from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)
regressor.score(X_test,y_test)
plt.scatter(y_test,y_train)

#predition 
y_pred = regressor.predict(X_test)

print('Linear Regression R squared": %.4f' % regressor.score(X_test, y_test))
from sklearn.metrics import mean_absolute_error
lin_mae = mean_absolute_error(y_pred, y_test)
print('Linear Regression MAE: %.4f' % lin_mae)

#using randomForestRegressor to check the accuracy of the model.
from sklearn.ensemble import RandomForestRegressor
forest_reg = RandomForestRegressor(random_state=42)
forest_reg.fit(X_train, y_train)
#print the value of random forest value
forest_reg.score(X_test, y_test)
randomforest=forest_reg.score(X_test, y_test)
y_predict=forest_reg.predict(X_test)


xlab=(")
plt.scatter(y_test, y_predict,  color='black')


