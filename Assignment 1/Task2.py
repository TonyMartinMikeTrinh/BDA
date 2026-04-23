import pandas as pd
import matplotlib.pyplot as plt


data = pd.read_csv("Assignment 1/publ_years.csv")
print(data.head())

# author span
data['span'] = data['LAST_YEAR'] - data['FIRST_YEAR'] + 1
print(data.head())

print(data['LAST_YEAR'].max())
#show, 73 bins beacuse min is 0 and max is 72
histogram = data.hist(column=["span",'LAST_YEAR','FIRST_YEAR'], bins=73)
plt.show()

#remove spans less than 1 because LAST YEAR >= First YEAR, so your last publication cannot be at year n while your first year be n+1
print("test",len(data[data["span"] > 40])/len(data))
print("amount to discrad", len(data[data["span"] > 40]))
data = data[data["span"] <= 40] # remove long spans which as ther only make 0.4704314874258819 percent of the whole dataset
data = data[data["span"] >= 1]
data = data[data["LAST_YEAR"] >= 1950]
data = data[data["FIRST_YEAR"] >= 1950]

histogram = data.hist(column=["span",'LAST_YEAR','FIRST_YEAR'], bins=40)
plt.show()