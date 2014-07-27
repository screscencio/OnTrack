import os, sys, getopt, re
from time import gmtime, strftime, time

baseUrl = 'localhost:8888'
useHttps = False
user = "admin@ontrack.com"
password = "ontrackpoulain"
listProjects="list-projects"
folder=""

def printTimeSpent(startTime) :
	timeSpent = time() - startTime
	print "[TIME SPENT] %02d:%02d" % ( int(timeSpent / 60), int(timeSpent % 60) )

def getXmlPath(projectId) :
	return os.path.join(folder, "ontrack_%s.xml" % projectId)

def executeCommand(command) :
	#print "[EXECUTING]", command
	os.system(command)

def download(projectId) :
	xmlPath = getXmlPath(projectId)
	print "[DOWNLOADING]", xmlPath 
	executeCommand('curl --max-time 1800%s -u %s:%s %s/application/xml/download?projectId=%s > %s' % (ssl3, user, password, protocol, projectId, xmlPath))
	xml = open(xmlPath)
	xmlContent = xml.read()
	if (not xmlContent.endswith("</ontrackXML>")) :
		raise Exception("[ERROR] Xml download failed, see file %s" % xmlPath)

def setup() :
	global folder, ssl3, protocol
	if not folder :
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

def revertFiles() :
	os.remove(os.path.join(folder, "projectList.txt"))
	try :
		os.remove(os.path.join(folder, "users.xml"))
	except :
		pass
	os.rmdir(folder)

def downloadProjectsList() :
	projectsListPath = os.path.join(folder, "projectList.txt")

	print "\n[INFO] retrieving the projects list"
	executeCommand('curl --basic%s -u %s:%s %s/application/xml/download?list-projects > %s' % (ssl3, user, password, protocol, projectsListPath))
	projectsList = open(projectsListPath).read()
	if (not projectsList or 'Error' in projectsList) :
		print "[Error] There is no projects available"
		return []
	print "\n[INFO] projects list retieved successfully."
	return projectsList.split(",")

def downloadUsers() :
	usersFilePath = os.path.join(folder, "users.xml")

	print "\n[INFO] retrieving the users list"
	executeCommand('curl --basic%s -u %s:%s %s/application/xml/download?list-users > %s' % (ssl3, user, password, protocol, usersFilePath))
	xmlContent = open(usersFilePath).read()
	succeeded = xmlContent.endswith("</ontrackXML>")
	if (succeeded) :
		print "\n[INFO] users list retieved successfully."
	else :
		print "[Error] Could not retrieve the users list"
	return succeeded

def execute(projects):
	startTime = time()
	message = "[SUCCESS] Operation finished successfully."

	prepareFolder()

	if (not projects) :
		projects = downloadProjectsList()
		if not projects or not downloadUsers():
			revertFiles()
			return

	try:
		for projectId in projects:
			if (projectId) :
				download(projectId)
				printTimeSpent(startTime)
	except Exception as e :
		xmlPath = getXmlPath(projectId)
		if not open(xmlPath).read().find("No project representation with id '%s' was found." % projectId) == -1 :
			os.remove(xmlPath)
			print "\n[INFO] The file %s was removed because the project %s was not found on the server" % (xmlPath, projectId)
		else :
			printTimeSpent(startTime)
			message = "[ERROR] Operation failed check error file at '%s'\n\n%s" % (xmlPath, str(e))

	print "[INFO] Downloaded %d projects" % (len(projects) - 1)
	print message

def usage():
	print '''
		Usage: python xml-download-uuid.py [options ...] [projectId ...]

		[projectId] :
		  The project ids to be downloaded separated by white space
		  Downloads all projects if not specified

		Options :
		 -b, --base-url <url> \t\tthe base to be used for xml export without the protocol; [localhost:8888]
		 -h, --help \t\tshow this help
		 -s, --use-https \t\tuse https connection
		 -u, --user <username> \t\tusername to be used on basic authentication [admin@ontrack.com]
		 -p, --password <password> \t\tpassword to be used on basic authentication [ontrackpoulain]
		 -o, --out-dir <dir> \t\tthe folder in with the ontrack migration should be downloaded [ontrack_aaaa_mm_dd]
	'''

def main():
	global baseUrl, fromProject, untilProject, useHttps, user, password, folder

	try:
		opts, args = getopt.getopt(sys.argv[1:], "hb:su:p:o:", ["help", "base-url", "use-https", "--user", "--password", "--out-dir"])
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
			
		if o in ("-o", "--out-dir") :
			folder = a

	setup()
	execute(args)
	sys.exit()


if __name__ == "__main__":
    main()
