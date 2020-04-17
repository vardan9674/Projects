#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu May 30 10:46:49 2019

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
#importing the data
data= pd.read_csv('VA_rent.csv')

#renaming the columns
data = data.rename(columns = {"bed":"Bedroom","bath":"Bathroom","area":"Area","Parking:":"Parking","cost/rent":"Rent"}) 

#parking
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


#Rent
data['Rent'] = data.Rent.str.replace(',','')
data['Rent']=data.Rent.str.replace('$','')
#Bathroom
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

data['Bathroom']=data.Bathroom.astype(float)
data['Rent']=data.Rent.astype(float)

#missing values in the d

data_missing_values=data.isnull().sum()

#

df=data[['Area','Bedroom','Bathroom','Parking','zip','Rent']]
df_missing_value=df.isnull().sum()

#Droping missing values
df=df.dropna()
#converting df datafram as int
df=df.astype(int)

df.describe()
df1=df.values
#

#checking for outlinears
X=df1[:,0:5]
y=df1[:,5]

X
y
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

sns.boxplot(x=df['Area'])


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
 
sns.boxplot(x=df['Bedroom'])
#lable encode
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,4]=labelencoder_x.fit_transform(X[:,4])

#feature scaling
from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X=sc_x.fit_transform(X)

#min and max for
minmaxscaler=preprocessing.MinMaxScaler(feature_range=(0,2))
X=minmaxscaler.fit_transform(X)
y=y.reshape(-1,1)
y=minmaxscaler.fit_transform(y)
'''
#checking mse values
models={#'linear_regression':sklearn.liner_model.LinearRegression(),
       'elasticnet':ElasticNet(),
       'randomforest':RandomForestRegressor()
       
       }


for name, model in models.items():
    validation_score=cross_val_score(model,X,y,\
                                     cv=10,\
                                     scoring='neg_mean_squared_error')
    print("Model {0} had average error:{1}".format(name,\
          math.sqrt(-np.mean(validation_score))))

#elastic mse
validation_E=cross_val_score(ElasticNet(),X,y,cv=10,scoring='neg_mean_squared_error')

  print("elasticNet model",(math.sqrt(-numpy.mean(validation_E))))


#Randomforest mse
  validation_rand=cross_val_score(RandomForestRegressor(),X,y,cv=10,scoring='neg_mean_squared_error')

 print("Randomforest model",(math.sqrt(-numpy.mean(validation_rand))))
 """
 
 model1=ElasticNet()
 model2=RandomForestRegressor()
 model1.fit(X,y)
 predict=model1.predict(X)
 model2.fit(X,y)
 model1.score(X,y)
 for i in range(10):
     prediction_feature=X[i].reshape(1,-1)
     print('E predicted price{0} and orginal rental{1}'.format(model1,model1(y),model1(X)))
 print(prediction_feature)
 
 model2.score(X,y)
rand_Predict=model2.predict(X)
 
 predict_old=pd.Dataframe('rand_Predict','y')
 
 
 
 #Linear regressoin
 model3=LinearRegression()
 model3.fit(X,y)
 model3.score(X,y)
 model3.predict(X)
 
 """
 '''
 #

 # training and testting
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=.2, random_state=42,shuffle=True)

#RANDOMFOREST REGRESSOR
random_forest=RandomForestRegressor(n_estimators=100)
random_forest.fit(X_train,y_train)
rand_pred=random_forest.predict(X_test)
print('train score for random_forest:',random_forest.score(X_train,y_train))
print('test score for random_forest:',random_forest.score(X_test,y_test))
y_pred=random_forest.predict(X_test)

#bagging
bg=BaggingRegressor(RandomForestRegressor(),n_estimators=10)
bg.fit(X_train,y_train)
bg.score(X_train,y_train)
bg.score(X_test,y_test)
mean_absolute_error(y_test,y_pred)

#linear regression
linear=LinearRegression()
linear.fit(X_train,y_train)
linear_pred=linear.predict(X_test)

print('test score for Linerregression:',linear.score(X_test,y_test))     


#Adaboosting
regr = AdaBoostRegressor(random_state=0, n_estimators=100)
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
gb.score(X_test,y_test)



#elastic
el=ElasticNet()
el.fit(X_train,y_train)
el.score(X_test,y_test)

#KFold
from sklearn.model_selection import KFold
StratifiedKFold(n_splits=2, random_state=None, shuffle=True)
kf=KFold(n_splits=5,shuffle=False)

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
ereg =VotingRegressor(estimators=[('gb', reg1), ('rf', reg2),('dt',reg4)])
ereg =ereg.fit(X_train, y_train)
ereg.score(X_train,y_train)
ereg.score(X_test,y_test)


