#!/usr/bin/env python
# coding: utf-8

# In[1]:


from pandas import Series
import matplotlib.pyplot as plt
from statsmodels.tsa.arima_model import ARIMA
import numpy

plt.style.use('ggplot')
get_ipython().run_line_magic('matplotlib', 'inline')


# In[2]:


# create a differenced series
def difference(dataset, interval=1):
	diff = list()
	for i in range(interval, len(dataset)):
		value = dataset[i] - dataset[i - interval]
		diff.append(value)
	return numpy.array(diff)


# In[3]:


# invert differenced value
def inverse_difference(history, yhat, interval=1):
	return yhat + history[-interval]


# In[5]:


# load dataset
series = Series.from_csv('history3.csv', header=None)
series = series.dropna()


# In[10]:


Y = series.resample('M').mean()
plt.figure(figsize=(15,6))
plt.plot(Y)


# In[7]:


# seasonal difference
X = series.values
days_in_year = 365
differenced = difference(X, days_in_year)


# In[13]:


# fit model
model = ARIMA(differenced, order=(1,0,1))
model_fit = model.fit(disp=0)
# multi-step out-of-sample forecast
forecast = model_fit.forecast(steps=10)[0]


# In[14]:


# invert the differenced forecast to something usable
history = [x for x in X]
day = 1
for yhat in forecast:
	inverted = inverse_difference(history, yhat, days_in_year)
	print('Day %d: %f' % (day, inverted))
	history.append(inverted)
	day += 1

