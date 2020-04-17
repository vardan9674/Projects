#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Sep 20 10:46:36 2019

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

data= pd.read_csv('Rent_CA.csv')

data = data.rename(columns = {"Bedrooms":"Bedroom","Bathrooms":"Bathroom","ZipCode":"zip","AreaSpace_SQFT":"Area","Parking:":"Parking","cost/rent":"Rent","Price/sqft:":"Price_sqft","Year built:":"Yearbuilt"}) 


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
data=data.replace('228 spaces','228')
data=data.replace('50 spaces','50')
data=data.replace('1511 spaces','151')
data=data.replace('21 spaces','21')
data=data.replace('28 spaces','28')
data=data.replace('120 spaces','120')
data=data.replace('101 spaces','101')
data=data.replace('52 spaces','52')
data=data.replace('On street, None','1')
data=data.replace('528 spaces','5')
data=data.replace('352 spaces','3')
data=data.replace('On street, Attached Garage, Detached Garage','1')
data=data.replace('Garage','1')
data=data.replace('34 spaces','3')
data=data.replace('Carport, Off street, Attached Garage, Detached Garage','1')
data=data.replace('Carport, Off street, Detached Garage','1')
data=data.replace('32 spaces','3')
data=data.replace('Carport, On street, Attached Garage','1')
data=data.replace('--',np.nan)
data=data.replace('Carport, Off street, On street, Attached Garage','1')
data=data.replace('76 spaces','3')
data=data.replace('44 spaces','4')
data=data.replace('Carport, Off street, On street, Attached Garage, Detached Garage','1')

data['Parking'].value_counts()
data['zip'].value_counts()

#Replacing for rent
'''
data['Price'] = data.Price.str.replace(',','')
data['Price']=data.Price.str.replace('$','')
data['Price']=data.Price.str.replace('C','')
'''
#


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


data['Bedroom']=data.Bedroom.astype(float)
data['Bathroom']=data.Bathroom.astype(float)
data['Price']=data.Price.astype(float)


data['Parking']=data.Parking.astype(float)
data['zip']=data.zip.astype(float)
data['Area']=data.Area.astype(float)

#price_persqft


df=data[['Area','Bedroom','Bathroom','Parking','zip','Price']]

###

df.isnull().sum()

df=df.dropna()

df=df.astype(int)



sns.boxplot(df['Area'])
sns.boxplot(df['Price'])



z = np.abs(stats.zscore(df['Price']))
threshold = 7
print(np.where(z > 7))
z1=df.Price[(z<7)]
sns.boxplot(z1)
df['Price']=z1


za = np.abs(stats.zscore(df['Area']))
threshold = 7
print(np.where(za > 6))
z2=df.Area[(za<6)]
sns.boxplot(z2)
df['Area']=z2





#missing values 



#clustering

df=df.dropna()

df=df.astype(int)

plt.scatter(df['Area'],df['Price'] )

km=KMeans(n_clusters=3)
k_pred=km.fit_predict(df[['Price','Area']])
k_pred

df['cluster']=k_pred

df1=df[df.cluster==0]
df2=df[df.cluster==1]
df3=df[df.cluster==2]

plt.scatter(df1.Area,df1['Price'],color='green')
plt.scatter(df2.Area,df2['Price'],color='red')
plt.scatter(df3.Area,df3['Price'],color='blue')


df=df.astype(int)
df=df[['Area','Bedroom','Bathroom','Parking','zip','cluster','Price']]

df1=df.values

#checking for outlinears
X=df1[:,0:6]
y=df1[:,6]

"""
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_x=LabelEncoder()
X[:,6]=labelencoder_x.fit_transform(X[:,6])
"""

from sklearn.preprocessing import StandardScaler
sc_x=StandardScaler()
X=sc_x.fit_transform(X)

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=.2, random_state=42,shuffle=True)

random_forest=RandomForestRegressor(n_estimators=100)
random_forest.fit(X_train,y_train)
rand_pred=random_forest.predict(X_test)
print('train score for random_forest:',random_forest.score(X_train,y_train))
print('test score for random_forest:',random_forest.score(X_test,y_test))

y_pred=random_forest.predict(X_test)


from sklearn.model_selection import cross_val_score
clf = RandomForestRegressor()
scores = cross_val_score(clf, X_test, y_test, cv=5)
scores.mean() 

#mse in $
mse=mean_absolute_error(y_test,y_pred)
print( "The mean absolute error is:$",mse)
#chceking r^2
from sklearn.metrics import r2_score

print("r_Score:",r2_score(y_test, y_pred))  


bg=BaggingRegressor(RandomForestRegressor(),n_estimators=10)
bg.fit(X_train,y_train)
bg.score(X_train,y_train)
bg.score(X_test,y_test)

#Adaboosting
regr = AdaBoostRegressor()
regr.fit(X_train, y_train)  
regr.score(X_test,y_test)

#Decision
from sklearn.tree import DecisionTreeRegressor
dt=DecisionTreeRegressor()
dt.fit(X_train,y_train)
dt.score(X_test,y_test)

#gradientBoost
from sklearn.ensemble import GradientBoostingRegressor
gb=GradientBoostingRegressor()
gb.fit(X_train,y_train)
gb.score(X_train,y_train)
gb.score(X_test,y_test)

#KNN
from sklearn.neighbors import KNeighborsRegressor
knn = KNeighborsRegressor()
knn = KNeighborsRegressor(algorithm='brute')
knn.fit(X_train, y_train)
knn.score(X_train,y_train)
knn.score(X_test,y_test)


#votingRegressor
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


print('Randomforest Model Score: ',random_forest.score(X_test,y_test))

print('GradientBoostingRegressor: ',gb.score(X_test,y_test))

print('AdaBoosting score: ',regr.score(X_test,y_test))

print('Voting Regressor:',ereg.score(X_test,y_test))

  
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Sep 20 10:37:50 2019

@author: vardanchennupati
"""

