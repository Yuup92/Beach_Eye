import pandas as pd  
import numpy as np  
import matplotlib.pyplot as plt  
import seaborn as seabornInstance 
from sklearn.model_selection import train_test_split 
from sklearn.linear_model import LinearRegression
from sklearn import metrics

dataset = pd.read_csv("regression.csv")
dataset.describe()







X = dataset['gx'].values.reshape(-1,1)
y = dataset['Final Velocity'].values.reshape(-1,1)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.0, random_state=0)
regressor = LinearRegression()  
regressor.fit(X, y)

#To retrieve the intercept:
print(regressor.intercept_)
#For retrieving the slope:
print(regressor.coef_)

x1 = []
y2 = []

for i in range(10, 100):
	x1.append(i)
	y2.append(regressor.coef_[0]*i+(regressor.intercept_))



dataset.plot(x='gx', y='Final Velocity', style='o')
plt.plot(x1,y2)
plt.xlim(12,20)
plt.ylim(20,38) 
plt.title('Gyrcoscope X-axis vs Velocity Ball Linear Regression')  
plt.xlabel('Gyrcoscope X-axis [rad/s]')  
plt.ylabel('Velocity Ball [km/h]')  
plt.show()


X = dataset['Ay'].values.reshape(-1,1)
y = dataset['Final Velocity'].values.reshape(-1,1)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.0, random_state=0)
regressor = LinearRegression()  
regressor.fit(X, y)


#To retrieve the intercept:
print(regressor.intercept_)
#For retrieving the slope:
print(regressor.coef_)

x1 = []
y1 = []

for i in range(10, 100):
	x1.append(i)
	y1.append(regressor.coef_[0]*i+(regressor.intercept_))

dataset.plot(x='Ay', y='Final Velocity', style='o')
plt.plot(x1,y1)
plt.xlim(40,90)
plt.ylim(21,35)
plt.title('Acceleromet Y-axis vs Velocity Ball Linear Regression')  
plt.xlabel('Gyrcoscope Y-axis [rad/s]')  
plt.ylabel('Velocity Ball [km/h]')    

plt.show()

