rm(list=ls())
library(ggplot2)
library(ggpubr)
library(corrplot)
library(plotly)
library(tidyverse)
final <-read.csv("~/Desktop/final.csv")
eastern<- read.csv("~/Desktop/eastern.csv")
View(final)
summary(final)
View(eastern)
summary(eastern)


#scatterplot

pop <- final[!duplicated(final$StateAbbr),]
state <- (final$StateAbbr)

sctplot <- data.frame(pop,state)

ggplot(eastern,aes(x=eastern$StateAbbr,y=eastern$PopulationCount))+
  geom_point(col='Red')+
  labs(x="States",y="Population",title="Population in the eastern states of US")

#boxplot
al <- subset(final$High_Confidence_Limit,final$Data_Value_Type=="Age-adjusted prevalence")
cl <- subset(final$High_Confidence_Limit,final$Data_Value_Type=="Crude prevalence")

boxplot(al,cl,col = c("Red","Blue"),names=c("Age-adjusted prevalence","Crude prevalence"),ylab="HighConfidence")


ahigh <- subset(final$Low_Confidence_Limit,final$Data_Value_Type=="Age-adjusted prevalence")
chigh <- subset(final$Low_Confidence_Limit,final$Data_Value_Type=="Crude prevalence")

boxplot(ahigh,chigh,col = c("yellow","green"),names=c("Age-adjusted prevalence","Crude prevalence"),ylab="LowConfidence")
#corr
co <- data.frame(final$Data_Value,final$PopulationCount)

cor.test(final$Data_Value,final$PopulationCount)
ggplot(final, aes(Data_Value, PopulationCount)) + geom_smooth(method = "lm")
##corr plot

dq <-data.frame(final$High_Confidence_Limit,final$Low_Confidence_Limit,final$Data_Value,na.rm=TRUE)
r <-cor(dq,method = "kendall")
corrplot(r,method = "number")


#######  REGRESSION  ##########

pr <- subset(final$High_Confidence_Limit,final$Category=="Prevention")
ho <- subset(final$Low_Confidence_Limit,final$Category=="Prevention")
ho <- na.omit(ho)
ho <- ho[sample(1:nrow(ho),288400,replace = FALSE),]  
hl <- data.frame(pr,ho)
hlregg <- lm(pr~ho,data=hl)

cruderegg <- lm(crudelow~crudehigh,data=proj)
xx <- data.frame(pr,ho)
summary(ageregg)

summary(cruderegg)

ggplot(xx, aes(x = pr, y = ho)) + 
  geom_point() +
  stat_smooth(method = "lm", col = "red")+
  labs(x="high Confidence",y="low Confidence",title="Prevention relation with high and low confidence(Regression Plot)")


ggplot(age, aes(x = agelow, y = agehigh)) + 
  geom_point() +
  stat_smooth(method = "lm", col = "red")+
  labs(x="AgeAdjPrev LowConfLimit",y="AgeAdj HighConfLimit",title="AgeAdjPrev LowConf Vs HighConf (Regression Plot)")

ggplot(crude, aes(x = crudelow, y = crudehigh)) + 
  geom_point() +
  stat_smooth(method = "lm", col = "red")+
  labs(x="CrudePrev LowConfLimit",y="CrudePrev HighConfLimit",title="CrudePrev LowConf Vs HighConf (Regression Plot)")



