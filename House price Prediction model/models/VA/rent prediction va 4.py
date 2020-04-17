#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jun  3 17:22:18 2019

@author: vardanchennupati
"""
#importing the libraries
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

#importing the data
data= pd.read_csv('Va_Rent_Single.csv')

#renaming the columns
data = data.rename(columns = {"bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","cost/rent":"Rent"}) 

#
data['Parking'].value_counts()

#replacing for attribute parking

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
data=data.replace('7 spaces','7')
data=data.replace('Carport, Attached Garage','1')
data=data.replace('Carport, Detached Garage','1')
data=data.replace('12 spaces','12')
data=data.replace('9 spaces','9')
data=data.replace('Carport, None','1')
data=data.replace('Carport, None','1')
data=data.replace('None, Detached Garage','1')
data=data.replace('None, Attached Garage, Detached Garage','1')
data=data.replace('20 spaces','20')
data=data.replace('13 spaces','13')
data=data.replace('15 spaces','15')
data=data.replace('14 spaces','14')
data=data.replace('16 spaces','16')
data=data.replace('Carport, None, Attached Garage','1')
data=data.replace('Off street, None','1')
data=data.replace('Off street, Attached Garage, Detached Garage','1')
data=data.replace('Off street, None, Attached Garage','1')
data=data.replace('On street, Detached Garage','1')
data=data.replace('23 spaces','23')
data=data.replace('40 spaces','40')
data=data.replace('Off street, On street, Detached Garage','1')
data=data.replace('17 spaces','17')
data=data.replace('22 spaces','22')
data=data.replace('Off street, On street, Detached Garage','1')
data=data.replace('Carport, Off street, Attached Garage','1')
data=data.replace('Carport, Off street, None','1')
data=data.replace('100 spaces','100')
data=data.replace('24 spaces','24')
data=data.replace('151 spaces','151')
data=data.replace('25 spaces','25')
data=data.replace('18 spaces','18')
data=data.replace('69 spaces','69')
data=data.replace('70 spaces','70')
data=data.replace('Off street, On street, Attached Garage, Detached Garage','1')
data=data.replace('Carport, Off street, On street, Detached Garage','1')
data=data.replace('228 spaces','22')
data=data.replace('50 spaces','50')
data=data.replace('1511 spaces','15')
data=data.replace('21 spaces','21')
data=data.replace('28 spaces','28')
data=data.replace('120 spaces','12')
data=data.replace('101 spaces','10')
data=data.replace('52 spaces','52')
data=data.replace('On street, None','1')
data=data.replace('528 spaces','5')
data=data.replace('352 spaces','3')
data=data.replace('On street, Attached Garage, Detached Garage','1')
data=data.replace('Garage','1')
data=data.replace('34 spaces','3')

data['Praking']=data.Parking.astype(int)







#
data['Rent'].value_counts()

#Replacing for rent
data['Rent'] = data.Rent.str.replace(',','')
data['Rent']=data.Rent.str.replace('$','')



data['Bathroom'].value_counts()
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
data['Bathroom']=data.Bathroom.astype(str).str.replace('17.5','7.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('37.5','7.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('42.5','2.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('57.5','7.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('27.5','7.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('18.5','8')
data['Bathroom']=data.Bathroom.astype(str).str.replace('18','8')
data['Bathroom']=data.Bathroom.astype(str).str.replace('22.5','2.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('20','2')
data['Bathroom']=data.Bathroom.astype(str).str.replace('32.5','3.5')
data['Bathroom']=data.Bathroom.astype(str).str.replace('12.0','2')
data['Bathroom']=data.Bathroom.astype(str).str.replace('13.0','3')
data['Bathroom']=data.Bathroom.astype(str).str.replace('27.5','7.5')


#converting Bathroom and Rent to float
data['Bathroom']=data.Bathroom.astype(float)
data['Rent']=data.Rent.astype(float)




#Checking missing values in the data

data_missing_values=data.isnull().sum()

#Creating a new dataframe

df=data[['Area','Bedroom','Bathroom','Parking','zip','Rent']]

#Checking the missing values in df dataframe
df_missing_value=df.isnull().sum()

#As we have very low missing value of 2% it is better to drop the missing value than add them with mean or median

#Droping missing values
df=df.dropna()
df=df.astype(int)
#
plt.scatter(df['Area'],df['Rent'])

km=KMeans(n_clusters=4)
k_pred=km.fit_predict(df[['Area','Rent']])
k_pred

df['cluster']=k_pred

df1=df[df.cluster==0]
df2=df[df.cluster==1]
df3=df[df.cluster==2]
df4=df[df.cluster==3]
plt.scatter(df1.Area,df1['Rent'],color='green')
plt.scatter(df2.Area,df2['Rent'],color='red')
plt.scatter(df3.Area,df3['Rent'],color='blue')
plt.scatter(df4.Area,df4['Rent'],color='orange')



#converting df datafram as int
df=df.astype(int)
df=df[['Area','Bedroom','Bathroom','Parking','zip','cluster','Rent']]

#creating new dataframe using df
df.describe()



# removing outliners
from scipy import stats
z = np.abs(stats.zscore(df['Rent']))
threshold = 7
print(np.where(z > 7))
z1=df.Rent[(z<7)]
sns.boxplot(z1)
df['Rent']=z1

#Area
sns.boxplot(df['Area'])

w= np.abs(stats.zscore(df['Area']))
threshold = 7
print(np.where(w > 7))

z2=df.Area[(w<3)]
sns.boxplot(z2)
df['Area']=z2

b=np.abs(stats.zscore(df['Bathroom']))
threshold = 7
print(np.where(b > 7))
z3=df.Bathroom[(b<7)]
df['Bathroom']=z3
sns.boxplot(z3)


g=np.abs(stats.zscore(df['Bedroom']))
threshold = 3
print(np.where(g > 3))
z4=df.Bedroom[(g<3)]
df['Bedroom']=z3
sns.boxplot(z4)


p=np.abs(stats.zscore(df['Parking']))
threshold = 7
print(np.where(p > 7))
z5=df.Parking[(p<7)]
df['Parking']=z5
sns.boxplot(z5)


df=df.dropna()
#Converting df1 dataframe into numpyarray
df1=df.values
#

#checking for outlinears
X=df1[:,0:6]
y=df1[:,6]

print(X)
print(y)

#boxplot for checking outliners
sns.boxplot(x=df['Rent'])

# Minimum price of the data
minimum_price = np.amin(df['Rent'])

# Maximum price of the data
maximum_price = np.amax(df['Rent'])

# Mean price of the data
mean_price = np.mean(df['Rent'])

# Median price of the data
median_price = np.median(df['Rent'])

# Standard deviation of prices of the data
std_price = np.std(df['Rent'])

print("The Minimum Rent:",minimum_price)
print("The Maximum Rent:",maximum_price)
print("The Mean for Rent:",mean_price)
print("The Median for Rent:",median_price)
print("The Standar Dev for Rent:",std_price)


df['Rent'].hist(bins=20)
#log transformation
df['Rent_log']=np.log(df['Rent'])
df['Rent_log'].hist(bins=20)
#
df['Rent_sqrt']=np.sqrt(df['Rent'])
df['Rent_sqrt'].hist(bins=20)
#
df['Rent_cbrt']=np.cbrt(df['Rent'])
df['Rent_cbrt'].hist(bins=20)

"Area"
#Box Plot For checking outlinears
sns.boxplot(X)
sns.boxplot(y)

# Minimum price of the data
minimum_Area= np.amin(df['Area'])

# Maximum price of the data
maximum_Area = np.amax(df['Area'])

# Mean price of the data
mean_Area= np.mean(df['Area'])

# Median price of the data
median_Area = np.median(df['Area'])

# Standard deviation of prices of the data
std_Area = np.std(df['Area'])

print("The Minimum Area:",minimum_Area)
print("The Maximum Area:",maximum_Area)
print("The Mean for Area:",mean_Area)
print("The Median for Area:",median_Area)
print("The Standar Dev for Area:",std_Area)





"Bedroom"
 
sns.boxplot(df['Bedroom'])
sns.boxplot(df['Bathroom'])
sns.boxplot(df['Parking'])
#lable encode for grenrerating factors for ZipCode
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,4]=labelencoder_x.fit_transform(X[:,4])

from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,5]=labelencoder_x.fit_transform(X[:,5])



X = X.astype(int)
y=y.astype(int)
#feature scaling for normalizng the data as we have OutLiners
from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X=sc_x.fit_transform(X)

# training and testting
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=.3, random_state=70,shuffle=True)

#RANDOMFOREST REGRESSOR
random_forest=RandomForestRegressor(n_estimators=100)
random_forest.fit(X_train,y_train)
rand_pred=random_forest.predict(X_test)
print('train score for random_forest:',random_forest.score(X_train,y_train))
print('test score for random_forest:',random_forest.score(X_test,y_test))

#predicting values for randomForest compare it to y_test
y_pred=random_forest.predict(X_test)

#bagging
bg=BaggingRegressor(RandomForestRegressor(),n_estimators=10)
bg.fit(X_train,y_train)
bg.score(X_train,y_train)
bg.score(X_test,y_test)

#mse in $
mean_absolute_error(y_test,y_pred)

#chceking r^2
from sklearn.metrics import r2_score

r2_score(y_test, y_pred)  

#linear regression
linear=LinearRegression()
linear.fit(X_train,y_train)
linear_pred=linear.predict(X_test)
linear.score(X_test,y_test)
print('train score for Linerregression:',linear.score(X_train,y_train))  
print('test score for Linerregression:',linear.score(X_test,y_test))     


#Adaboosting
regr = AdaBoostRegressor()
regr.fit(X_train, y_train)  
regr.score(X_test,y_test)

#DecisionTreeRegressor
from sklearn.tree import DecisionTreeRegressor
dt=DecisionTreeRegressor()
dt.fit(X_train,y_train)
dt.score(X_test,y_test)

#GradientBoost
from sklearn.ensemble import GradientBoostingRegressor
gb=GradientBoostingRegressor()
gb.fit(X_train,y_train)
gb.score(X_train,y_train)
gb.score(X_test,y_test)


#knn
from sklearn.neighbors import KNeighborsRegressor
knn = KNeighborsRegressor()
knn = KNeighborsRegressor(algorithm='brute')
knn.fit(X_train, y_train)
knn.score(X_train,y_train)
knn.score(X_test,y_test)


#voting
from sklearn.ensemble import VotingRegressor
reg1=GradientBoostingRegressor()
reg2=RandomForestRegressor()
reg3=LinearRegression()
reg4=DecisionTreeRegressor()
reg5 = KNeighborsRegressor()
reg6=AdaBoostRegressor()
ereg =VotingRegressor(estimators=[('gb', reg1), ('rf', reg2)])
ereg =ereg.fit(X_train, y_train)
ereg.score(X_train,y_train)
ereg.score(X_test,y_test)

#predict values from voting method compare it to y_test
vote_pred=ereg.predict(X_test)
#
#mse in $
mse=mean_absolute_error(y_test,vote_pred)
print( "The mean absolute error is:$",mse)
#chceking r^2
from sklearn.metrics import r2_score

print("r_Score:",r2_score(y_test, vote_pred))  