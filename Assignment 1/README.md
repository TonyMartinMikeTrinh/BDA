# DBLP Publication Span Analysis

## Task 1: Statistical Analysis
- **Data**: DBLP CSV with PERSON_ID, FIRST_YEAR, LAST_YEAR
- **Span Calculation**: `span = LAST_YEAR - FIRST_YEAR + 1`
- **Statistics**: min, max, quartiles, median, mean
- **Visualization**: Box plot (with/without outliers)
- **Insights**: Shows distribution, outliers, and data skewness
- **Issues**: Outliers distort mean; sparse early data

## Task 2: Data Filtering & Average Span
- **Research Question**: Average publication span of scientists?
- **Histograms**: Span, FIRST_YEAR, LAST_YEAR (73 bins initially (as span is between 0-72), 40 after filtering)

### Filtered Classes:
1. **Long spans (>40 years)**: ~0.47% of data, unrealistic careers
   - Impact: Reduces inflated mean, focuses on typical spans
2. **Unreal spans (less than 0 year)**: ~0.043% of data, unrealistic careers
    - Impact: Reduces inflated mean, focuses on typical spans
3. **Pre-1950 publications**: ~0.037% of data, aligns with computing timeline
   - Impact: Removes historical anomalies, improves data homogeneity

### Results:
- Filtered dataset provides representative average for modern CS research
- Files: [Task1.py](Task1.py), [Task2.py](Task2.py)

