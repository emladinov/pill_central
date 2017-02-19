#!/usr/bin/python
import MySQLdb
import datetime
import time
import RPi.GPIO as GPIO
GPIO.cleanup()
GPIO.setmode(GPIO.BCM)
GPIO.setup(13, GPIO.OUT)
GPIO.setup(19, GPIO.OUT)
GPIO.setup(26, GPIO.OUT)
GPIO.setup(21, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.setup(20, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.output(13, False)
GPIO.output(19, False)
GPIO.output(26, False)
GPIO.setmode(GPIO.BCM)

#time.sleep(5)
#print("mark")
def main():
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(13, GPIO.OUT)
	GPIO.setup(19, GPIO.OUT)
	GPIO.setup(26, GPIO.OUT)
	GPIO.setup(21, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setup(20, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	weekdays=[]
	db = MySQLdb.connect(host="pillcentraldev.c9uqhrbiescc.us-east-1.rds.amazonaws.com", user="PCadmin", passwd="PCadmin1", db="PillCentral")
	x = 0
	currenttime = datetime.datetime.now().time()
	currenttime = time.strftime("%H:%M:%S")
	bool = True
	recordlist = []
	S_PK = []
	user_ID = []
	pill_name = []
	position = []
	timedelta = []
	DOW = []
	actualtime = []
#counter = 0
#print "after statement"
	
	conn = db.cursor()
	conn.execute("SELECT VERSION()")
	results = conn.fetchone()
#	if results:
#		print "it works"
#	else:
#		print "bad"
#	except MySQLdb.Error:
#		print "Error"
	checkdays(conn, db, recordlist)
#	parserecords()
	endfunction()

def weekarray():
	weekdays.append("Sunday")
	weekdays.append("Monday")
	weekdays.append("Tuesday")
	weekdays.append("Wednesday")
	weekdays.append("Thursday")
	weekdays.append("Friday")
	weekdays.append("Saturday")
	
def checkdays(conn, db, recordlist):
	param1 = datetime.datetime.today().weekday()
	param2 = (param1 + 1) % 7
	conn = db.cursor()
	conn.execute("SELECT * FROM schedule where DOW = '%s' AND user_ID = '1'" % ((param2)))
	rows = conn.fetchall()

	for row in rows:
		recordlist.append(row)
	parserecords(recordlist)
def parserecords(recordlist):
	timedelta = []
	position = []
	actualtime = []
	for record in recordlist:
		S_PK, user_ID, pill_name, position, timedelta, DOW = zip(*recordlist)			
	for i in timedelta:
		normaltime = (datetime.datetime.min + i).time()
		actualtime.append(normaltime)
	checktime(position, actualtime)

def checktime(position, actualtime):
	GPIO.setup(13, GPIO.OUT)
	GPIO.setup(19, GPIO.OUT)
	GPIO.setup(26, GPIO.OUT)
	GPIO.setup(21, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setup(20, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setmode(GPIO.BCM)
	bool = True
	while(bool):
		counter = 0
		for x in actualtime:
  			currenttime = datetime.datetime.now().time()
			currenttime = time.strftime("%H:%M:%S")
			currenttimedatetime = datetime.datetime.strptime(currenttime, "%H:%M:%S").time()
			if currenttimedatetime == x:
#				print ("Time to take your pill")
#				print ("It is in position %s" % (position[counter]))
				if (position[counter]) == 1:
					GPIO.output(26, True)
				if (position[counter]) == 2:
					GPIO.output(19, True)
				if (position[counter]) == 3:
					GPIO.output(13, True)
				else:
					pass
			if GPIO.input(21):
				GPIO.output(26, False)
			if GPIO.input(20):
				GPIO.output(19, False)
			if GPIO.input(16):
				GPIO.output(13, False)
			counter += 1
		bool = False				
def endfunction():
	GPIO.cleanup()	
while True:
	main()
conn.close()
    

    

    
