#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun May 19 19:56:51 2019

@author: vardanchennupati
"""

#importing the dataset


import pandas as pd
import numpy as np
CD1=pd.read_csv("crime2018.csv")
CD1.head()
CD1=CD1.drop(CD1.columns[[0,1,2,3,8,9,10,11,12,13,14,15,16,17,20,21,22,23,24]],axis=1)
CD2=pd.read_csv("crime2019.csv")
CD2=CD2.drop(CD2.columns[[0,1,2,3,8,9,10,11,12,13,14,15,16,17,20,21,22,23,24]],axis=1)
CD2.head()
#missing values
CD1.isnull().sum()
CD2.isnull().sum()
#correlation
CD1.corr()
CD2.corr()

print(CD1.info())

