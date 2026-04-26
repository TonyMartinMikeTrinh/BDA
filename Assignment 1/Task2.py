import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv("publ_years.csv")

# Activity span
data['span'] = data['LAST_YEAR'] - data['FIRST_YEAR'] + 1

# Example of each class that ís removed
ex_invalid = data[data['span'] < 1].head(1)
ex_long = data[data['span'] > 40].head(1)
ex_old = data[(data['FIRST_YEAR'] < 1950) | (data['LAST_YEAR'] < 1950)].head(1)
# Print examples
print(f"Klasse span < 1:\n{ex_invalid[['#KEY', 'FIRST_YEAR', 'LAST_YEAR', 'span']]}\n")
print(f"Klasse span > 40:\n{ex_long[['#KEY', 'FIRST_YEAR', 'LAST_YEAR', 'span']]}\n")
print(f"Klasse vor 1950:\n{ex_old[['#KEY', 'FIRST_YEAR', 'LAST_YEAR', 'span']]}")

# Filter
# Remove spans less than 1 because LAST YEAR >= First YEAR, so your last publication cannot be at year n while your first year be n+1
print("percentage of +40 year span", (data["span"] > 40).sum() / len(data))
print("amount to discrad", (data["span"] > 40).sum())
print("percentage of less than 1 year span", (data["span"] < 1).sum() / len(data))
print("amount to discrad", (data["span"] < 1).sum())
print("percentage of erlier than 1950", (data["FIRST_YEAR"] < 1950).sum() / len(data))
print("amount to discrad", (data["FIRST_YEAR"] < 1950).sum())
data = data[
    data["span"] <= 40]  # Remove long spans which as ther only make 0.4704314874258819 percent of the whole dataset
data = data[data["span"] >= 1]
data = data[data[
                "LAST_YEAR"] >= 1950]  # We consider computing timeline starting at 1950 https://en.wikipedia.org/wiki/Category:Computing_timelines
data = data[data["FIRST_YEAR"] >= 1950]

histogram = data.hist(column=["span", 'LAST_YEAR', 'FIRST_YEAR'], bins=40)
plt.show()

# min
print("min ", data['span'].min())
# max
print("max ", data['span'].max())
# lower quartil
print("lower quartil ", data['span'].quantile(0.25))
# upper quartile
print("upper quartile ", data['span'].quantile(0.75))
# median
print("median ", data['span'].median())
# mean
print("mean ", data['span'].mean())
