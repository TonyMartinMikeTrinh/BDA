import pandas as pd
import matplotlib.pyplot as plt

# read csv-File
data = pd.read_csv("publ_years.csv")

# Activity Span
data['span'] = data['LAST_YEAR'] - data['FIRST_YEAR'] + 1

# min

print("min: ", data['span'].min())
# max
print("max: ", data['span'].max())
# lower quartil
print("lower quartil: ", data['span'].quantile(0.25))
# upper quartile
print("upper quartile: ", data['span'].quantile(0.75))
# median
print("median: ", data['span'].median())
# mean
print("mean: ", data['span'].mean())

# show
boxplot = data.boxplot(column=["span"])

# without outliers
data.boxplot(column=["span"], showfliers=False)
plt.show()
