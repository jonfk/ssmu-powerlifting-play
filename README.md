SSMU Powerlifting website
=========================
Simple website for ssmu powerlifting club with profiles for club members


Ideas
-----
- Badge system for personal bests, overall best, etc
- Workout tracking?

##TODO before serving to public:

###Setting up the postgresql server:
-Install Postgres
    +```sudo apt-get install postgresql postgresql-contrib```
-Set user for postgres
    +```sudo -u postgres psql postgres```
-Set password for postgres user
    +```\password postgres```
-edit the pg_hba.conf file
    +```find / -name "pg_hba.conf"```
-change peer to md5
-create database
    +```CREATE DATABASE ssmu-powerlifting```
-create tables
    +```psql -U postgres -d ssmupowerlifting -a -f SQL/create.sql```

###Before live run
-change application secret
-change database and database username and password

###Compile for live run
```sbt dist```

###In unzipped directory of program
```mkdir -p images/profiles SQL```

###Run command
```./bin/ssmu-powerlifting -Dhttp.port=1234```