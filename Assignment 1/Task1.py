import pandas as pd
import matplotlib.pyplot as plt


data = pd.read_csv("Assignment 1/publ_years.csv")
print(data.head())

# author span
data['span'] = data['LAST_YEAR'] - data['FIRST_YEAR'] + 1
print(data.head())

# min
print("min ",data['span'].min())
# max
print("max ",data['span'].max())
# lower quartil - 25% see https://en.wikipedia.org/wiki/Quartile
print("lower quartil ",data['span'].quantile(q=0.25))
# upper quartile
print("upper quartile ",data['span'].quantile(q=0.75))
# median
print("median ",data['span'].median())
# mean
print("mean ",data['span'].mean())

#show
boxplot = data.boxplot(column=["span"])

#without outliers
data.boxplot(column=["span"], showfliers=False)
plt.show()


