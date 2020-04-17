#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Jun  5 10:05:23 2019

@author: vardanchennupati
"""
import numpy as np
import pandas as pd
import matplotlib.pylab as plt
%matplotlib inline
from matplotlib.pylab import rcParams
from datetime import datetime
from sklearn import preprocessing

data= pd.read_csv('history.csv',parse_dates=["date"],index_col="date")

data.describe()


data.isnull().sum()
data=data.dropna()

data=data[['price']]






data['date']=pd.to_datetime(data['date'],infer_datetime_format=True)

data['price'] = data.price.str.replace(',','')
data['price']=data.price.str.replace('$','')
data['price']=data.price.str.replace('2900/mo','2900')
data['price']=data.price.astype(int)


data.index

df2018=data['2018']
df2018.price.mean()

df2019=data['2019-01']
df2019.price.mean()


meanOfMonthlyData=data.price.resample('M').mean()

meanOfMonthlyData.plot()


df00_19=data['2000':'2019']
meanOf2000_2019=df00_19.price.resample('M').mean()
meanOf2000_2019.plot()

data=data.iloc[:240,:]

data.plot()

minmaxscaler=preprocessing.MinMaxScaler(feature_range=(1,100))
data['price']=minmaxscaler.fit_transform(data['price'])

data.plot()


indexdf=data.set_index(['date'])


from matplotlib.pyplot import figure
figure(num=None, dpi=50,figsize=[20,7], facecolor='w', edgecolor='k')
plt.xlabel('date')
plt.ylabel('price')
plt.plot(indexdf)


#
print(data.head())

price_difference=data.diff(periods=1)
price_difference=price_difference[1:]


from statsmodels.graphics.tsaplots import plot_acf
plot_acf(data)


data['date'].min(),data['date'].max()







