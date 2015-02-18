SSMU Powerlifting website
=========================
Simple website for ssmu powerlifting club with profiles for club members

[![Build Status](https://travis-ci.org/jonfk/ssmu-powerlifting-play.svg?branch=master)](https://travis-ci.org/jonfk/ssmu-powerlifting-play)


Ideas
-----
- Badge system for personal bests, overall best, etc
- Workout tracking?

##TODO before serving to public:

###Setting up the postgresql server:
Install Postgres

    sudo apt-get install postgresql postgresql-contrib

Set user for postgres

    sudo -u postgres psql postgres

Set password for postgres user

    \password postgres

Edit the pg_hba.conf file

    find / -name "pg_hba.conf"

Change peer to md5

Create database

    CREATE DATABASE ssmu-powerlifting

Create tables

    psql -U postgres -d ssmupowerlifting -a -f SQL/create.sql

###Before live run
- change application secret
- change database and database username and password

###Compile for live run
    sbt dist

###In unzipped directory of program
    mkdir -p images/profiles SQL

###Run command
    ./bin/ssmu-powerlifting -Dhttp.port=1234