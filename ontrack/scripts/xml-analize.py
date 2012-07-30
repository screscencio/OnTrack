import os, sys, getopt, re, time

ATTRIBUTE_END = "\""
VERSION_TAG = "<ontrackXML version=\""
PROJECT_TAG = "<projectRepresentation name=\""
ACTION_TAG = "</userAction>"
PROJECT_AUTH_USER_TAG = "<projectAuthorization userId=\""
PROJECT_AUTH_PROJ_TAG = " projectId=\""
USER_ID_TAG = "<user id=\""
USER_EMAIL_TAG = " email=\""
ACTION_TIMESTAMP_TAG = "<userAction timestamp=\""

def checkFileEnd(xmlText, fileName) :
	if xmlText.find("</ontrackXML>") == -1 :
		raise Exception("The specified file is not a ontrackXML file or the file is incomplete")

	printInfo("The file %s is complete" % fileName)

def getAttribute(xmlText, attributeTag, afterIndex = 0) :
	startIndex = xmlText.find(attributeTag, afterIndex)
	if (startIndex == -1) :
		return "", -1

	startIndex += len(attributeTag)
	endIndex = xmlText.find(ATTRIBUTE_END, startIndex)
	return xmlText[startIndex : endIndex], endIndex + len(ATTRIBUTE_END)

def printProjectName(xmlText) :
	print "[PROJECT NAME]", getAttribute(xmlText, PROJECT_TAG)[0]

def printVersion(xmlText) :
	print "[XML VERSION]", getAttribute(xmlText, VERSION_TAG)[0]

def printNumberOfActions(xmlText) :
	actionsCount = 0
	currentIndex = xmlText.find(ACTION_TAG)

	while currentIndex >= 0:
		actionsCount += 1
		currentIndex = xmlText.find(ACTION_TAG, currentIndex + len(ACTION_TAG))

	print "[ACTIONS COUNT]", actionsCount

def printLastActionTimestamp(xmlText) :
	index = 0
	while True :
		timestamp, index = getAttribute(xmlText, ACTION_TIMESTAMP_TAG, index)
		if index == -1:
			break
		lastTimestamp = timestamp
	print "[LAST_ACTION_TIMESTAMP]", lastTimestamp

def getUser(xmlText, userId) :
	currentUserId, index = getAttribute(xmlText, USER_ID_TAG)
	while int(currentUserId) != userId :
		currentUserId, index = getAttribute(xmlText, USER_ID_TAG, index)

	return getAttribute(xmlText, USER_EMAIL_TAG, index)[0]

def printAuthorizedUsers(xmlText, projectId = None) :
	currentIndex = 0

	for _ in range(100) :
		currentUserId, userIndex = getAttribute(xmlText, PROJECT_AUTH_USER_TAG, currentIndex)
		if (userIndex == -1) :
			break
		currentProjectId, currentIndex = getAttribute(xmlText, PROJECT_AUTH_PROJ_TAG, userIndex)

		if not projectId or int(currentProjectId) == projectId :
			print "[AUTHORIZED USER]", getUser(xmlText, int(currentUserId))

def execute(path) :
	if os.path.isdir(path) :

		expression = re.compile(r"^ontrack_\d+(-\d+-\d+)?\.xml$")
		for entry in os.listdir(path) :
			if expression.match(entry) :
				xmlText = open(os.path.join(path, entry)).read();

				try :
					checkFileEnd(xmlText, entry)
					printProjectName(xmlText)
					#printVersion(xmlText)
					printNumberOfActions(xmlText)
					printLastActionTimestamp(xmlText)
					#printAuthorizedUsers(xmlText)

				except Exception as error :
					print "[ERROR]", str(error)


def printInfo(infoText) :
	print "[INFO]", infoText

def main():
	try:
		_, arg = getopt.getopt(sys.argv[1:], "", [])
		execute(arg[0])
		sys.exit()
	except getopt.error, msg:
		print str(msg)
		sys.exit(2)

if __name__ == "__main__":
    main()
