import os, sys, getopt, re, codecs, ntpath
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta

SIX_MONTHS = 6 * 31
TIMEDELTA_NEEDED_TO_BE_ARCHIVED = timedelta(SIX_MONTHS)

FILE_REGEX = re.compile(r"^ontrack_[A-Z\-0-9]+\.xml$")

folder = ""

def toDatetime(dateStr) :
	return datetime.strptime(dateStr, '%Y-%m-%d %H:%M:%S.%f %Z')

def archive(path) :
	fileName = ntpath.basename(path)
	os.rename(path, os.path.join(folder, fileName))
	print "[Archived]", fileName

def isOldEnoughtToBeArchived(timestamp) :
	return (datetime.now() - timestamp) > TIMEDELTA_NEEDED_TO_BE_ARCHIVED


def getLastActionTimestamp(root) :
	lastActionTimestamp = datetime.min
	for userAction in root.iter('userAction') :
		timestamp = toDatetime(userAction.get('timestamp'))
		if lastActionTimestamp < timestamp :
			lastActionTimestamp = timestamp 
			
	return lastActionTimestamp

def shouldBeArchived(root) :
	timestamp = getLastActionTimestamp(root)
	return isOldEnoughtToBeArchived(timestamp)

def processFile(path) :
	#print "[INFO] Processing", ntpath.basename(path);
	try :
		tree = ET.parse(path)
		if shouldBeArchived(tree.getroot()) :
			archive(path);
	except Exception as e:
		print "[ERROR] Failed to process", path
		print type(e)
		print e.args
		print e

def execute(path) :
	fileName = ntpath.basename(path)
	if FILE_REGEX.match(fileName) :
		processFile(path)
	elif os.path.isdir(path) :
		for entry in os.listdir(path) :
			execute(os.path.join(path, entry))

def prepareFolder() :
	if not os.path.exists(folder) :
		os.makedirs(folder)

def revertFiles(projectsListPath) :
	os.rmdir(folder)

def setup() :
	global folder

	if not folder :
		timeStr = datetime.strftime(datetime.now(), "%Y-%m-%d")
		folder = 'archived/ontrack_' + timeStr

	prepareFolder();

def main():
	try:
		initialTime = datetime.now()
		_, arg = getopt.getopt(sys.argv[1:], "", [])
		
		setup()
		execute(arg[0])
		print "[INFO] Finished in %s" % str(datetime.now() - initialTime)

		sys.exit()
	except getopt.error, msg:
		print str(msg)
		outFile.close()
		sys.exit(2)

if __name__ == "__main__":
    main()
