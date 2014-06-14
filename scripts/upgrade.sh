#!/bin/sh

# Rudeltippen Upgrade Script

#variables
base=`pwd`
date=`date +%Y%m%d-%H%m%S`
foldertmp=$base'/tmp/'
folderbackup=$base'/backup/'
version=`grep  -Eo ' [0-9]{1,2}.[0-9]{1,2}.[0-9]{1,2}</a>'  $base/app/views/main.html | cut -c 1- | rev | cut  -c 5- | rev | tr -d ' '`
zipprefix="https://github.com/svenkubiak/Rudeltippen/releases/download/"

# usage function
usage(){
        echo -e "Usage example: $0 2.2.4"
        exit 1
}

# Check for backup folder
function checkBackupFolder() {
if [ -d "$1" ]
  then
    echo "Using existing backup main folder ..."
  else
    echo "Creating backup main folder ..."
    mkdir $folderbackup
  fi
}

# create databse dump
function createdump() {
dbstr=""
  dbstr=`grep -v '#' $base/conf/application.conf |  grep 'prod.db=mysql' | tr -d ' '`
  if [ -z "$dbstr" ];
    then
      echo "Can't find database informations."
      exit 0
    else
      dbstr=${dbstr#*//}

      # extract databse name
      dbname="${dbstr#*/}"
      # workaround to remove trailing ^M from string. I don't no where it come from or a better way to remove it ...
      dbname=`echo $dbname | cat -v | tr -d "[^M]"`

      # extract database host
      dbhost=${dbstr#*@}
      dbhost=${dbhost%/*}

      # extract database user
      dbuser=${dbstr%:*}
      dbuser=${dbuser#*@}

      # extract databse password
      dbpass=${dbstr#*:}
      dbpass=${dbpass%@*}

      # creating dump
      sqlpath=$bapath$dbname'.sql'

      mysqldump --user=$dbuser --password=$dbpass --host=$dbhost --lock-tables=false $dbname > $sqlpath
  fi
}

# get the news version and do upgrade
function downloadandupgrade() {
  zippath=$zipprefix$1/$1.zip
  zipfile=$1"_"$date.zip
  cd $foldertmp
  mkdir $date
  cd $date
  response=`wget $zippath -O $zipfile 2>&1|egrep "HTTP|Length|saved"`

  if [ "$response" == "HTTP request sent, awaiting response... 404 Not Found" ]
    then
      echo "Failed downloading new version. Maybe the given version ($1) isn't correct!?"
      echo "Abbort."
      exit 0
    else
      echo "New Rudeltippen version downloded to $foldertmp$zipfile ..."
      echo "Unzipping new version ..."
      unzip -q $zipfile

      echo "Upgrading to version $1 ..."
      rm -rf ../../app/
      rm -rf ../../conf
      rm -rf ../../public/

      cp -R app/ ../../
      cp -R conf/ ../../
      cp -R public/ ../../

      cp $bapath/conf/application.conf ../../conf/

      echo "Cleaning up ..."
      cd ..
      rm -rf $date
  fi
}

# backup function
function backup() {
  echo "Starting backup ..."
  checkBackupFolder $1
  bapath=$1"rudeltippen_"$version"_"$date/
  mkdir $bapath
  echo "Creating backup at $bapath ..."
  cp -r $base/app/ $base/conf/ $base/logs/ $base/public/ $base/scripts/ $base/tmp/ $bapath
  createdump
}

if [ "$1" ]
  then
    if [ "$1" == $version ]
    then
      echo "You have already Rudeltippen in version $version!"
    else
      read -p"Do you want upgrade Rudeltippen from $version to $1 (y/n)? " response

      if [ "$response" == "y" ];
        then
          cd $base && play stop
          backup $folderbackup
          downloadandupgrade $1
          cd $base && play start
          echo "Upgrade done. "
        else
          echo "Abbort."
          exit 0
      fi
    fi
  else
    usage
fi
