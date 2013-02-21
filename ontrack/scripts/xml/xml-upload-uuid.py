import os, sys, getopt, re
from time import time

baseUrl = 'localhost:8888'
useHttps = False
user = "admin@ontrack.com"
password = "ontrackpoulain"
command = 'curl --basic%s -u %s:%s -F "ontrack=@%s;type=text/xml" %s/application/xml/upload > %s'

def executeCommand(command) :
	#print "[EXECUTING]", command
	os.system(command)

def upload(filePath):
	print "[UPLOADING]", filePath

	logPath = filePath + ".log"
	print ""
	executeCommand( command % (ssl3, user, password, filePath, protocol, logPath) )
	print ""

	log = open(logPath)
	logText = log.read()

	if len(logText) == 0 :
		os.remove(logPath)
		print "[DONE]", filePath
	else :
		raise Exception("[ERROR] Check log file at '%s'" % logPath)

def printTimeSpent(startTime) :
	timeSpent = time() - startTime
	print "[TIME SPENT] %02d:%02d" % ( int(timeSpent / 60), int(timeSpent % 60) )

def execute(filesPath):
	startTime = time()

	message = "[SUCCESS] Operation finished successfully"
	try :
		for entry in filesPath :
			if(os.path.isdir(entry)) :
				uploadDir(entry, startTime)
			else :
				upload(entry)
				printTimeSpent(startTime)

		if not filesPath :
			uploadDir(os.curdir, startTime)

	except Exception as e:
		printTimeSpent(startTime)
		message = "[ERROR] Operation failed, please clean the database before trying again"
		message += "\n" + str(e)

	print message

def uploadDir(dirPath, startTime) :
	expression = re.compile(r"^ontrack_[A-Z\-0-9]+\.xml$")
	for entry in os.listdir(os.path.join(dirPath)):
		if expression.match(entry) :
			upload(os.path.join(dirPath, entry))
			printTimeSpent(startTime)

def setup():
	global ssl3, protocol

	if useHttps :
		ssl3 = " -3 -k"
		protocol = "https"
	else :
		ssl3 = ""
		protocol = "http"
	protocol = protocol + "://" + baseUrl

def usage():
	print '''
		Usage :
		  python xml-upload.py [options...] [files...]

		Files: 
		  Specifies files that will be uploaded or directories that contains those files [./]
		  When Specifying a directory the files should match the pattern ^ontrack_[A-Z\-0-9]+\.xml$ otherwise it will be ignored.

		Options:
		  -b, --base-url \t\tthe base to be used for xml upload; [localhost:8888]
		  -h, --help \t\tshow this help
		  -s, --use-https \t\tuse https connection
		  -u, --user \t\tuser to be used on basic authentication [admin@ontrack.com]
		  -p, --password \t\tpassword to be used on basic authentication [ontrackpoulain]
	'''

def main():
	global baseUrl, useHttps, user, password

	try:
		opts, arg = getopt.getopt(sys.argv[1:], "hb:su:p:", ["help", "base-url", "use-https", "user", "password"])
	except getopt.error, msg:
		print str(msg)
		usage()
		sys.exit(2)

	for o, a in opts :
		if o in ('-h', "--help") :
			usage()
			sys.exit()

		if o in ("-b", "--base-url") :
			baseUrl = a

		if o in ("-s", "--use-https") :
			useHttps = True

		if o in ("-u", "--user") :
			user = a

		if o in ("-p", "--password") :
			password = a

	setup()
	execute(arg)
	sys.exit()


if __name__ == "__main__":
    main()