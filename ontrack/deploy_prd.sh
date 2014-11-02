echo 'Generating package'
mvn package -Pprod -Dmaven.test.skip=true
echo 'sending package'
sftp -i ../key/OntrackEarlyAdopters.pem ubuntu@app.ontrack.io<<EOF
	put target/ontrackExt-0.0.1.war ROOT.war
EOF
echo 'Executing script on server for copy the war file and restart tomcat'
ssh -i ../key/OntrackEarlyAdopters.pem ubuntu@app.ontrack.io 'sh deploy_ontrack.sh'
echo 'deploy executed'
