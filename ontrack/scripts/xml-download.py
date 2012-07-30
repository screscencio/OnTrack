import os, sys, getopt, re
from time import gmtime, strftime, time

MAX_NUMBER_OF_PROJECTS = 50
baseUrl = 'localhost:8888'
fromProject = 1
untilProject = -1
useHttps = False
user = "admin@ontrack.com"
password = "ontrackpoulain"

def printTimeSpent(startTime) :
	timeSpent = time() - startTime
	print "[TIME SPENT] %02d:%02d" % ( int(timeSpent / 60), int(timeSpent % 60) )

def getXmlPath(projectId) :
	return os.path.join(folder, "ontrack_%02d.xml" % projectId)

def download(projectId) :
	xmlPath = getXmlPath(projectId)
	print "[DOWNLOADING]", xmlPath 
	os.system('curl --basic%s -u %s:%s %s/application/xml/download?projectId=%d > %s' % (ssl3, user, password, protocol, projectId, xmlPath))
	xml = open(xmlPath)
	xmlContent = xml.read()
	if (not xmlContent.endswith("</ontrackXML>")) :
		raise Exception("[ERROR] Xml download failed, see file %s" % xmlPath)

def setup() :
	global folder, ssl3, protocol
	timeStr = strftime("%Y-%m-%d", gmtime())
	folder = 'ontrack_' + timeStr
	
	if useHttps :
		ssl3 = " -3"
		protocol = "https"
	else :
		ssl3 = ""
		protocol = "http"
	protocol = protocol + "://" + baseUrl

def prepareFolder() :
	global folder
	sufix = ""
	for i in range(30) :
		try :
			os.mkdir(folder + sufix)
			break
		except OSError :
			sufix = "_" + str(i + 1)
	folder = folder + sufix

def execute():
	prepareFolder()
	
	startTime = time()
	message = "[SUCCESS] Operation finished successfully."
	projectId = 1;
	try:
		if untilProject >= fromProject :
			for projectId in range(fromProject, untilProject + 1):
				download(projectId)
				printTimeSpent(startTime)
		else :
			for projectId in range(fromProject, fromProject + MAX_NUMBER_OF_PROJECTS) :
				download(projectId)
				printTimeSpent(startTime)
	except Exception as e :
		xmlPath = getXmlPath(projectId)
		if not open(xmlPath).read().find("No project representation with id '%d' was found." % projectId) == -1 :
			os.remove(xmlPath)
			print "\n[INFO] The file %s was removed because the project %d was not found on the server" % (xmlPath, projectId)
			projectId -= 1;
		else :
			printTimeSpent(startTime)
			message = "[ERROR] Operation failed \n" + str(e)

	print "[INFO] Downloaded %d projects" % (projectId - fromProject + 1)
	print message

def usage():
	print '''
		Usage :
		python xml-download.py [options...]

		Options:
		 -b, --base-url \t\tthe base to be used for xml export; [localhost:8888]
		 -f, --first-project \t\tthe first project id to be exported, inclusive [1]
		 -h, --help \t\tshow this help
		 -l, --last-project \t\tthe last project id to be exported, inclusive, -1 for all projects [-1]
		 -s, --use-https \t\tuse https connection
		 -u, --user \t\tuser to be used on basic authentication [admin@ontrack.com]
		 -p, --password \t\tpassword to be used on basic authentication [ontrackpoulain]
	'''

def main():
	global baseUrl, fromProject, untilProject, useHttps, user, password

	try:
		opts, _ = getopt.getopt(sys.argv[1:], "hb:sf:l:u:p:", ["help", "base-url", "use-https", "first-project", "last-project", "--user", "--password"])
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

		if o in ("-f", "--first-project") :
			fromProject = int(a)

		if o in ("-l", "--last-project") :
			untilProject = int(a)

		if o in ("-u", "--user") :
			user = a

		if o in ("-p", "--password") :
			password = a

	setup()
	execute()
	sys.exit()


if __name__ == "__main__":
    main()
