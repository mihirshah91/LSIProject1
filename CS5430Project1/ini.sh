#!/bin/bash

DIR="/home/ec2-user/"
FILE="/home/ec2-user/reboot.txt"
DATAFILE="/home/ec2-user/IPTable.json"
IPFILE="/home/ec2-user/local-ipv4"
AMIFILE="/home/ec2-user/ami-launch-index"
WARFILE_LOCATION="s3://edu-cornell-cs-cs5300s16-sp2339/CS5300Project1.war"
INSTALLATION_SCRIPT_LOCATION="s3://edu-cornell-cs-cs5300s16-sp2339/ini.sh"
FcountfileLocation="s3://edu-cornell-cs-cs5300s16-sp2339/fcount.txt"
servercountfileLocation="s3://edu-cornell-cs-cs5300s16-sp2339/servercount.txt"
serverCountFile="/var/tmp/servercount.txt"


sudo aws configure set aws_access_key_id XXXXXXXXXXXXX
sudo aws configure set aws_secret_access_key XXXXXXXXXX

sudo aws configure set default.region us-east-1
sudo aws configure set preview.sdb true




sudo rm $IPFILE
sudo wget http://169.254.169.254/latest/meta-data/local-ipv4
ipaddr=`cat local-ipv4`

echo "ipaddress= " 
echo $ipaddr
sudo rm $AMIFILE
sudo wget http://169.254.169.254/latest/meta-data/ami-launch-index
item=`cat ami-launch-index`
#item=2
echo $item > $AMIFILE 

echo "serverid="
echo $item

aws s3 cp $servercountfileLocation /var/tmp/servercount.txt
servercount=`cat $serverCountFile`
echo "serverCount"
echo $servercount



if [ -e $FILE ]
then
	count=`cat $FILE`
	((count++))
	echo $count > $FILE
else
	echo "0" > $FILE
fi



# entry=$(sudo aws sdb select --select-expression "select count(*) from IPTable where itemName()="$item" " | )

if [ `(sudo aws sdb select --select-expression "select count(*) from IPTable where itemName()='$item'" | grep -Po '(?<="Value": ")[^"]*')` -eq 0 ]
then
sudo aws sdb put-attributes --domain-name IPTable --item-name $item  --attributes Name=IP,Value=$ipaddr
fi	

while [ $(sudo aws sdb select --select-expression "select count(*) from IPTable" | grep -Po '(?<="Value": ")[^"]*') -lt $servercount ]

 do
   	echo "hi"
        sleep 3s
 done

sudo aws sdb select --select-expression "select * from IPTable" > $DATAFILE

sudo yum remove java-1.7.0-openjdk -y
sudo yum install java-1.8.0 -y
sudo yum -y install tomcat8-webapps tomcat8-docs-webapp tomcat8-admin-webapps

aws s3 cp $WARFILE_LOCATION /tmp/CS5300Project1.war
sudo mv /tmp/CS5300Project1.war /usr/share/tomcat8/webapps/

aws s3 cp $INSTALLATION_SCRIPT_LOCATION /var/tmp/ini.sh
aws s3 cp $FcountfileLocation /var/tmp/fcount.txt



sudo service tomcat8 start


sudo chmod 777 $FILE $DATAFILE $AMIFILE
sudo chmod 755 /home/ec2-user
sudo usermod -a -G ec2-user tomcat
# sudo chown tomcat /home/ec2-user
# sudo chown tomcat /home/ec2-user/*
