import csv
import matplotlib
import matplotlib.pyplot as plt

time = []
xd = []
yd = []
zd = []

timeR = []
rX = []
rY = []
rZ = []

dataCounter = 0
dataBlocker = 2

with open('accel_3.txt') as csvfile:
	reader = csv.reader(csvfile, delimiter=',')
	for line in reader:
		if(dataCounter == 0):
			#dataCounter = dataCounter + 1
			time.append(int(line[0]))
			xd.append(abs(float(line[1])))
			yd.append(abs(float(line[2])))
			zd.append(abs(float(line[3])))
		elif(dataCounter > dataBlocker):
			dataCounter = 0
		else:
			dataCounter = dataCounter + 1

with open('gyro_3.txt') as csvfile:
	reader = csv.reader(csvfile, delimiter=',')
	for line in reader:
		timeR.append(int(line[0]))
		rX.append(float(line[1]) * 57.2958)
		rY.append(float(line[2])* 57.2958)
		rZ.append(float(line[3])* 57.2958)

print(xd)
plt.subplot(311)
ax1=plt.subplot(311)
ax1.plot(time, xd, 'r', label='X-axis')
ax1.set(xlabel='time (s)', ylabel='Accel. $(m/s^{2})$',
       title='Swinging a racket around for a 15seconds')
plt.setp(ax1.get_xticklabels(), fontsize=6)
ax1.legend(loc="upper right")

ax2=plt.subplot(312, sharex=ax1)
ax2.plot(time,yd, 'b', label='Y-axis')
ax2.set(xlabel='time (s)', ylabel='Accel.  $(m/s^{2})$')
plt.setp(ax2.get_xticklabels(), fontsize=6)
ax2.legend(loc="upper right")

ax3=plt.subplot(313, sharex=ax1)
ax3.plot(time,zd, 'g', label='Z-axis')
ax3.set(xlabel='time (s)', ylabel='Accel.  $(m/s^{2})$')
plt.setp(ax3.get_xticklabels(), fontsize=6)
ax3.legend(loc="upper right")

#fig.savefig("test.png")
# plt.show()

plt.figure()
plt.plot(timeR, rX, 'r', label='X')
plt.plot(timeR, rY, 'b', label='Y')
plt.plot(timeR, rZ, 'g', label='Z')
ax = plt.subplot(111)
ax.set(xlabel='time (s)', ylabel='Degrees per Second (deg/s)', title='Hitting a ball with a serve')
plt.legend(loc='upper right')
plt.show()





