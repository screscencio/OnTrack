#!/bin/bash

mkdir ~/workspace
mkdir -p ~/Development/lib

sudo apt-get install -y openjdk-6-jdk git chromium-browser unzip maven2

cd ~/Development

wget "http://eclipse.c3sl.ufpr.br/eclipse/downloads/drops4/R-4.2-201206081400/eclipse-SDK-4.2-linux-gtk.tar.gz"
tar -xvf eclipse-SDK-4.2-linux-gtk.tar.gz
rm eclipse-SDK-4.2-linux-gtk.tar.gz

ln -s ~/Development/eclipse/eclipse ~/Desktop/eclipse

cd lib
wget "http://google-web-toolkit.googlecode.com/files/gwt-2.4.0.zip"
unzip gwt-2.4.0.zip
rm gwt-2.4.0.zip

ln -s gwt-2.4.0 gwt

