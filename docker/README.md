## How To Create Development Docker environments

# Prerequisites
To use any BjondHealth docker environments you need to
1. Create an account at https://hub.docker.com/
2. Ask Ops to add your user to the Bjond account
3. In your local terminal run the command "docker login" and login with the credentials you created above
4. The above steps are necessary to be able to download Bjond docker private images from the docker hub registry

# BjondHealth

# OpenEMPI

1. You should have a working docker environment with docker-compose installed
2. Checkout the bjondhealth repository git clone git@github.com:Bjond/bjondhealth.git
3. cd bjond-health/docker/bin
4. ./openempi-deploy.sh  - This command will use th default port 8080
5. ./openempi-deploy.sh -p 8090 - You can specify a non default port with the -p switch