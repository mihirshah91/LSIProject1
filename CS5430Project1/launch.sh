#!/bin/bash


#serverCount=3
file="/Users/Shiva/Documents/Masters/Assignements/LSI/Pro1b/ini.sh"
key="sp2339"
war_file="/Users/Shiva/Documents/Masters/Assignements/LSI/Pro1b/CS5300Project1.war"
war_file_name="CS5300Project1.war"
ini_script="ini.sh"
servercountfile="servercount.txt"
Fcountfile="fcount.txt"
bucket_name="s3://edu-cornell-cs-cs5300s16-sp2339"
ami_id="ami-XXXX"
ami_type = "t1.micro"
#sudo rm -rf "$key.pem"

sudo aws configure set aws_access_key_id XXXXXXXXXXXXXX
sudo aws configure set aws_secret_access_key XXXXXXXXX

sudo aws configure set default.region us-east-1
sudo aws configure set preview.sdb true

echo "Please enter the number of instances yo want to launch (N) : "
read serverCount
echo $serverCount > $servercountfile
aws s3 cp $servercountfile "$bucket_name/$servercountfile"


echo "Please enter the number of Resilience (F): "
read Fcount
echo $Fcount > $Fcountfile
aws s3 cp $Fcountfile "$bucket_name/$Fcountfile"


aws s3 cp $war_file "$bucket_name/$war_file_name"
aws s3 cp $ini_script "$bucket_name/$ini_script"

#aws ec2 create-key-pair --key-name $key --query 'KeyMaterial' --output text > "$key.pem"
#chmod 400 "$key.pem"

sleep 3s


aws ec2 run-instances --image-id $ami_id --count $serverCount --instance-type ami_type --user-data "file://$file" --key-name "$key"