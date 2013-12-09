import os, sys, getopt, re, codecs
import xml.etree.ElementTree as ET
from datetime import datetime
from datetime import timedelta
from dateutil import tz
from copy import copy

INDENTATION = '    '
MAX_DEPTH = 10
IGNORED_INVITORS = ['admin@ontrack.com', 'ontrack@oncast.com.br', 'samuel.crescencio@oncast.com.br']
IGNORED_DOMAINS = ['ontrack.com', 'oncast.com.br']
PERSONAL_EMAIL_DOMAINS = ['@gmail.', '@hotmail.', '@live.', '@yahoo.', '@bol.', '@terra.', '@uol.', '@aol.', '@icloud.']
PROFILES = ['GUEST', 'CONTRIBUTOR', 'PEOPLE_MANAGER', 'PROJECT_MANAGER', 'ACCOUNT_MANAGER', 'SYSTEM_ADMIN']
PRICE_PER_USER = 20.00
REGISTRATION_TOLERANCE_IN_DAYS = 10

data = None

class Company :

	def __init__(self, domain) :
		if '@' in domain :
			self.name = domain[:domain.find('@')]
		else :
			self.name = domain[:domain.find('.')]
		self.domain = domain
		self.users = []
		self.lastActionTimestamp = None

	def setLastActionTimestamp(self, timestamp) :
		if self.lastActionTimestamp is None or self.lastActionTimestamp < timestamp :
			self.lastActionTimestamp = timestamp

	def addUser(self, user) :
		if(user not in self.users) :
			self.users.append(user);

	def getRootUsers(self) :
		rootUsers = []
		for user in self.users :
			if self.dontHasParentInThisCompany(user) :
				rootUsers.append(user)
		return rootUsers

	def dontHasParentInThisCompany(self, user) :
		if not user.parent :
			return True
		parent = user.parent[0]
		company = data.getCompany(parent.email)
		return company != self

	def __eq__(self, other) :
		return self.domain == other.domain

	def __hash__(self) :
		return hash(self.domain)

	def printReport(self, indentation = '') :
		printl( "%s%s (%i):" % (indentation, self.domain, len(self.users)) )
		for user in self.getRootUsers() :
			user.printReport(indentation + INDENTATION)

class Membership :
	def __init__(self) :
		self.invitationTimestamp = None
		self.lastActionTimestamp = None
		self.profile = 'GUEST'
		self.remotionTimestamp = None

	def updateInvitationTimestamp(self, timestamp) :
		if self.invitationTimestamp :
			return
		self.invitationTimestamp = timestamp

	def shouldBeCharged(self) :
		if self.profile == 'GUEST' or self.invitationTimestamp is None or self.invitationTimestamp > billingEndDate:
			return False
		if self.remotionTimestamp is None :
			return True
		invitation = self.invitationTimestamp
		if invitation < billingStartDate :
			invitation - billingStartDate
		delta = self.remotionTimestamp - invitation
		return delta.days > REGISTRATION_TOLERANCE_IN_DAYS

class Project :

	def __init__(self, id, name) :
		self.id = id
		self.name = name
		self.users = {}
		self.lastActionTimestamp = None
		self.creator = None
		self.creationTimestamp = None

	def setProjectCreator(self, invitedUser) :
		if self.creator :
			return
		self.creator = invitedUser

	def setCreationTimestamp(self, timestamp) :
		if not self.creationTimestamp or self.creationTimestamp > timestamp :
			self.creationTimestamp = timestamp

	def setLastActionTimestamp(self, timestamp) :
		if not self.lastActionTimestamp or self.lastActionTimestamp < timestamp :
			self.lastActionTimestamp = timestamp

	def addUser(self, user) :
		if user not in self.users :
			self.users[user] = Membership()

	def updateUserInvitationTimestamp(self, user, timestamp) :
		if user in self.users :
			self.users[user].updateInvitationTimestamp(timestamp)

	def updateUserProfile(self, user, profile, timestamp):
		if user in self.users :
			self.users[user].profile = profile

	def updateUserRemotionTimestamp(self, user, timestamp) :
		if user in self.users :
			self.users[user].remotionTimestamp = timestamp

	def updateUserLastActionTimestamp(self, user, timestamp) :
		if user in self.users :
			self.users[user].lastActionTimestamp = timestamp	

	def getFirstUser(self) :
		firstUser = None
		firstTimestamp = datetime.utcnow()
		for user, membership in self.users.items() :
			timestamp = membership.invitationTimestamp
			if timestamp and firstTimestamp > timestamp :
				firstTimestamp = timestamp
				firstUser = user
		return firstUser if firstUser else self.creator

	def __eq__(self, other) :
		return self.id == other.id

	def __hash__(self) :
		return hash(self.id)

	def printReport(self, indentation = ''):
		ind = indentation + INDENTATION
		printl( "%s%s" % (indentation, self.name) )
		printl( "%sID: %s" % (ind, self.id) )
		printl( "%sCreated by %s%s" % (ind, self.creator.email if self.creator else "UNKNOWN", formatTime(self.creationTimestamp)) )
		printl( "%sLast update%s" % (ind, formatTime(self.lastActionTimestamp)) )
		printl( "%sTotal of %d users" % (ind, len(self.users)) )
		printl( "%sProbable company: %s" % (ind, data.getCompany(self.getFirstUser().email).domain if self.getFirstUser() else "UNKNOWN"))
		printl( "%sMain users" % ind )
		if self.creator :
			self.creator.printForProject(self, ind + INDENTATION)
		else :
			printl(ind + INDENTATION + "Project creator not found, sorry")
		printl( "%sUsers" % ind )
		for user, membership in self.users.items() :
			printl("%s%s #%s%s%s%s" % (ind + INDENTATION, user.email, membership.profile, formatTime(membership.invitationTimestamp), formatTime(membership.lastActionTimestamp, " [No Interaction]"), formatTime(membership.remotionTimestamp)))

class User :
	
	def __init__(self, id, email) :
		self.name = email[:email.find('@')];
		self.email = email
		self.id = id
		self.lastActionTimestamp = None
		self.parent = None
		self.childrem = {}
		self.projects = []
		self.creationTimestamp = None

	def addProject(self, project) :
		if project not in self.projects :
			self.projects.append(project)

	def setCreationTimestamp(self, timestamp) :
		if not self.creationTimestamp or self.creationTimestamp > timestamp :
			self.creationTimestamp = timestamp

	def setLastActionTimestamp(self, timestamp) :
		if not self.lastActionTimestamp or self.lastActionTimestamp < timestamp :
			self.lastActionTimestamp = timestamp

	def addChild(self, child, timestamp) :
		if child not in self.childrem :
			self.childrem[child] = timestamp

	def removeChild(self, child) :
		if child in self.childrem :
			del self.childrem[child]

	def setParent(self, newParent, timestamp) :
		if self.verifyHasDescendants(newParent, timestamp) :
			return
				
		if not self.parent or self.parent[1] > timestamp :
			if self.parent :
				self.parent[0].removeChild(self)
			self.parent = (newParent, timestamp)
			newParent.addChild(self, timestamp)

	def verifyHasDescendants(self, descendant, timestamp) :
		for child, t in self.childrem.items() :
			if child == descendant :
				if t < timestamp :
					return True
				else :
					self.removeChild(descendant)
					descendant.parent = None
					return False
			else :
				child.verifyHasDescendants(descendant, timestamp)

	def __eq__(self, other) :
		return self.id == other.id

	def __hash__(self) :
		return hash(self.id)

	def printForProject(self, project, indentation = '') :
		ind = indentation
		if project in self.projects :
			printl( indentation + self.email )
			ind = indentation + INDENTATION

		for child in self.childrem.keys() :
			child.printForProject(project, ind)

	def printReport(self, indentation = '') :
		if checkMaxIndentation(indentation) :
			return

		projectsCountStr = " {%i}" % len(self.projects) if self.projects else ''
		timeStr = formatTime(self.lastActionTimestamp)
		childremStr = ':' if self.childrem else ''
		printl( indentation + self.name + projectsCountStr + timeStr + childremStr )
		for child in self.childrem.keys() :
			child.printReport(indentation + INDENTATION)

class OnTrackData :

	def __init__(self):
		self.users = {}
		self.companies = {}
		self.projects = {}

	def getProject(self, projectId, projectName = None) :
		if projectId not in self.projects :
			if projectName is None :
				raise Exception('Project not Found, Inconsistency!')
			self.projects[projectId] = Project(projectId, projectName)
		return self.projects[projectId]

	def getUser(self, id, email = None) :
		if id not in self.users :
			if email is None :
				raise Exception('User not Found, Inconsistency!')
			self.users[id] = User(id, email)
		return self.users[id]

	def getCompany(self, email) :
		domain = extractDomainFromEmail(email)
		for d in PERSONAL_EMAIL_DOMAINS :
			if d in email :
				domain = email
		return self.getCompanyByDomain(domain)

	def getCompanyByDomain(self, domain) :
		if domain not in self.companies :
			self.companies[domain] = Company(domain)
		return self.companies[domain]

	def printReport(self, indentation = '') :
		printl( indentation + "COMPANIES (%d):" % len(self.companies) )
		for company in self.companies.values() :
			company.printReport(indentation + INDENTATION)

		printl( indentation + "PROJECTS (%d):" % len(self.projects) )
		for project in self.projects.values() :
			project.printReport(indentation + INDENTATION)

	def printPayment(self, indentation = '') :
		printl( indentation + "PAYMENTS [%s to %s]:" % (billingStartDate.strftime('%Y/%m/%d'), billingEndDate.strftime('%Y/%m/%d')) )
		payingUsersByCompany = {}
		for project in self.projects.values() :
			if project.getFirstUser() is None :
				continue
			company = self.getCompany(project.getFirstUser().email)
			company.setLastActionTimestamp(project.lastActionTimestamp)
			if company not in payingUsersByCompany :
				payingUsersByCompany[company] = []
			users = payingUsersByCompany[company]
			for user, membership in project.users.items() :
				if membership.shouldBeCharged() and user not in users :
					users.append(user)
		ind = indentation + INDENTATION
		total = 0.0
		for company, payingUsers in payingUsersByCompany.items() :
			if company.domain in IGNORED_DOMAINS or company.lastActionTimestamp < billingStartDate :
				continue
			payingUsersCount = len(payingUsers)
			price = payingUsersCount * PRICE_PER_USER
			total += price
			printl( "%s%s: R$ %.2f (%i users)%s" % (ind, company.domain, price, payingUsersCount, formatTime(company.lastActionTimestamp)) )
			for user in payingUsers :
				printl(ind + INDENTATION + user.email)
		printl( "%sTotal: R$ %.2f" % (indentation, total) )


def extractDomainFromEmail(email) :
	return email[email.find('@') + 1:]

def printl(message) :
	outFile.write(message + '\n')
	print message

def checkMaxIndentation(indentation) :
	result = len(indentation) > MAX_DEPTH * len(INDENTATION)
	if result :
		printl( indentation + '...' )
	return result

def createUserMap(root):
	for user in root.iter('userData'):
		email = user.get('email')
		company = data.getCompany(email)
		company.addUser(data.getUser(user.find('id').get('id'), email))

def toDatetime(dateStr) :
	return datetime.strptime(dateStr, '%Y-%m-%d %H:%M:%S.%f %Z')

def formatTime(d, emptyStr='') :
	if not d :
		return emptyStr

	diff = datetime.utcnow() - d
	differenceInMonths = diff.days / 30
	differenceStr = "%i months ago" % differenceInMonths if differenceInMonths > 1 else d.strftime('%Y-%m-%d')
	return " [%s]" % differenceStr

def processTeamInvite(project, action, user, timestamp) :
	invitedUserId = action.find('userId').get('id')
	invitedUser = data.getUser(invitedUserId)
	invitedUser.setCreationTimestamp(timestamp)
	project.setCreationTimestamp(timestamp)
	project.setProjectCreator(invitedUser)
	project.updateUserInvitationTimestamp(invitedUser, timestamp)
	if user != invitedUser :
		invitedUser.setParent(user, timestamp)

def getProfileFromAction(action) :
	projectProfile = action.find('projectProfile')
	if projectProfile is not None :
		return projectProfile.text
	newProfile = action.find('newProfile')
	if newProfile is not None :
		return newProfile.text
	canInvite = action.get('canInvite')
	if canInvite is not None  :
		return 'PROJECT_MANAGER' if 'true' == canInvite else 'CONTRIBUTOR'
	readOnly = action.get('readOnly')
	if readOnly is not None  :
		return 'GUEST' if 'true' == readOnly else 'CONTRIBUTOR'
	raise Exception('Could not find profile for ' + str(action.get('class')))

def processProfileUpdateAction(project, action, user, timestamp) :
	invitedUserId = action.find('userId').get('id')
	invitedUser = data.getUser(invitedUserId)
	profile = getProfileFromAction(action);
	project.updateUserProfile(invitedUser, profile, timestamp)

def processTeamRevogueInvitation(project, action, user, timestamp) :
	invitedUserId = action.find('userId').get('id')
	invitedUser = data.getUser(invitedUserId)
	project.updateUserRemotionTimestamp(invitedUser, timestamp)

def handleUserActions(root) :
	for project in root.iter('project') :
		representation = project.find('projectRepresentation')
		project = data.getProject(representation.find('id').get('id'), representation.get('name'))

		for userAction in root.iter('userAction') :
			timestamp = toDatetime(userAction.get('timestamp'))
			project.setLastActionTimestamp(timestamp)

			userId = userAction.find('userId').get('id')
			user = data.getUser(userId);
			user.setLastActionTimestamp(timestamp)
			
			action = userAction.find('action')
			actionClass = action.get('class')
			if 'TeamInviteAction' in actionClass :
				processTeamInvite(project, action, user, timestamp)
				processProfileUpdateAction(project, action, user, timestamp)
			elif 'TeamRevogueInvitationAction' in actionClass :
				processTeamRevogueInvitation(project, action, user, timestamp)
			elif 'TeamDeclare' in actionClass :
				processProfileUpdateAction(project, action, user, timestamp)
			
			project.updateUserLastActionTimestamp(user, timestamp)

def updateUserAuthorizations(root) :
	for representation in root.iter('projectRepresentation') :
		data.getProject(representation.find('id').get('id'), representation.get('name'))

	for auth in root.iter('projectAuthorization') :
		userId = auth.find('userId').get('id')
		projectId = auth.find('projectId').get('id')
		user = data.getUser(userId)
		project = data.getProject(projectId)
		project.addUser(user)
		user.addProject(project)

def analyze(root) :
	createUserMap(root)
	updateUserAuthorizations(root)
	handleUserActions(root)

def execute(path) :
	expression = re.compile(r"^ontrack_[A-Z\-0-9]+\.xml$")

	if os.path.isdir(path) :
		for entry in os.listdir(path) :
			if expression.match(entry) :
				execute(os.path.join(path, entry))

	else :
		tree = ET.parse(path)
		analyze(tree.getroot())

def lastDayOfMonth(date):
    if date.month == 12:
        return date.replace(day=31)
    return date.replace(month=date.month+1, day=1) - timedelta(days=1)

def main():
	global data, outFile, billingStartDate, billingEndDate
	try:
		initialTime = datetime.now()
		outFile = codecs.open("ontrack_payments_%s.txt" % initialTime.strftime('%Y-%m-%d'), 'w', 'utf-8')
		_, arg = getopt.getopt(sys.argv[1:], "", [])
		data = OnTrackData()

		billingMonth = datetime.utcnow().strftime('%Y-%m') if len(arg) < 2 else arg[1]
		billingStartDate = datetime.strptime(billingMonth, '%Y-%m').replace(day = 1)
		billingEndDate = lastDayOfMonth(copy(billingStartDate))
		
		execute(arg[0])
		#data.printReport()
		data.printPayment()
		outFile.close()
		print "[INFO] Finished in %s" % str(datetime.now() - initialTime)
		sys.exit()
	except getopt.error, msg:
		print str(msg)
		outFile.close()
		sys.exit(2)

if __name__ == "__main__":
    main()