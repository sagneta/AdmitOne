![alt text](https://github.com/sagneta/AdmitOne/blob/master/images/AdmitOne.jpg "AdmitOne Inc.")

# AdmitOne
ReactJS demonstration project.

The front-end is my first ReactJS project. I am not by any means a snappy UI developer but I know
javascript. My first impressions of ReactJS are very positive.

The back-end is a J2EE 7 Wildfly 9 application using PicketLink as Security Manager and JPA 2.1

Requires basically are:

* nodejs and npm
* JDK-8
* postgresql >= 9.1 
* gradle >= 3.2 

Note that I added a hokey login button because I think every app should have one. It looks
stupid but it works fine.

## Two Ways to Install and Run

There are two ways to install the project. _The Cool Way_ which requires 
docker and _the Common Method_.

# The Common Method


This method employs a tgz file of a Wildfly installation with the product deployed along
with JDK-8 for Linux systems. It also contains a gradle installation for further builds.
You will _require_ an installation of postgresql and it's location, if not localhost, will
require a change to a single environment variable. Otherwise this is rather simple.

The package.tgz file is located on GoogleDrive as it is large: 

<https://drive.google.com/file/d/0B9-pg4L_L2j0RmNCUmpQOFhDdWs/view?usp=sharing>

Simply unarchive it like so:
```shell
$ tar xvfz package.tgz 
```


You will need to install a postgresql DB 9.1 or greater at localhost. If you wish to have it
hosted elsewhere change the _localhost_ environment variables within ./packaged/run.sh

The following postgresql super users should be constructed:

user: bjondadmin   pass: bjondadmin
user: bjondhealth  pass: bjondhealth

(I didn't have time to change my customary setup images.)

Ensure there is nothing running locally bound to port 8080.

Now run the system:

```shell
$ ./run.sh
```

Point your browser at localhost:8080 and use username: *admin* password: *admin*.


# The Cool Way

If you are feeling adventurous and simply wish to see the app running and have disc and 
network bandwidth to spare, try the docker method.

I've only tested this on Ubuntu 16.10 but I imagine it will work on most modern linux installations.

Requirements: (It's possible I missed a few. Forgive me)

* And account on dockerhub.com
* docker >= 1.12.1 
* python 2.7
* python-pip
* docker-compose >= 1.9.0

Don't have anything bound locally on port 8080 (this app) and 5432 (postgresql). 


To actually begin the process:

```shell
$ cd ./docker/bin
$ sudo ./docker.sh
```


It's possible that on your system sudo _won't_ be required but it's probably a safe bet. What will happen is that some 
custom images we use are downloaded: postgresql and wildfly 9. Once that is complete, it's only performed the first time
, the database schema is constructed. Then the application is built, system tested (Arquillian) and filled with enough
canned data to make the *Search Table* do something interesting. 

You will see some errors/warnings in the wildfly setup. Ignore them, I ran out of time to deal with that. 

Now once the application is running successfully you will see something like this:

```
07:29:16,989 INFO  [org.jboss.as.server] (ServerService Thread Pool -- 33) WFLYSRV0010: Deployed "admitone.war" (runtime-name : "admitone.war")
07:29:16,989 INFO  [org.jboss.as.server] (ServerService Thread Pool -- 33) WFLYSRV0010: Deployed "ROOT.war" (runtime-name : "ROOT.war")
07:29:17,097 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
07:29:17,098 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
07:29:17,098 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Full 9.0.2.Final (WildFly Core 1.0.2.Final) started in 9353ms - Started 488 of 688 services (263 services are lazy, passive or on-demand)
```

At this point you have achieved probable success and your name is enshrined among those of the cool kids! In any event,
point your browser at localhost:8080 and use username: *admin* password: *admin*

Once you are done hit control-c in the deploy terminal and it will shutdown the docker composed images. You may repeat this process to the see 
the application once again. It will be faster as the docker images are local now.





