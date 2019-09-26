import csv
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression

time = []
Ay = []
gx = []
v = []

i = 0
dataBlocker = 2

firstOne = True;

with open('regression.csv') as csvfile:
	reader = csv.reader(csvfile, delimiter=',')
	for line in reader:
		if(not firstOne):
			Ay.append(abs(float(line[3])))
			gx.append(abs(float(line[5])))
			v.append(abs(float(line[8])))
		else:
			firstOne = False;
area = np.pi*3




x = np.array([5, 15, 25, 35, 45, 55]).reshape((-1, 1))
y = np.array([5, 20, 14, 32, 22, 38])
print(x)
print(y)

model = LinearRegression().fit(y,x)

print('slope:', model.coef_)
print('intercept:', model.intercept_)

x = np.array(Ay).reshape(1,-1)
y = np.array(v)

print(x)
print(y)

model = LinearRegression().fit(y,x)

print('slope:', model.coef_)
print('intercept:', model.intercept_)
# Plot
plt.scatter( y, x, alpha=0.5)
plt.title('Acceleration Y-axis vs Velocity')
plt.xlabel('Velocity[km/h]')
plt.ylabel('y')
plt.show()
		
