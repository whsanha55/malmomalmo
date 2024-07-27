gradle build
sshpass -p 'Y8@!P@-4MyHce' scp build/libs/malmomalmo.jar root@223.130.157.144:/home/malmomalmo/
sshpass -p 'Y8@!P@-4MyHce' ssh root@223.130.157.144 "bash -c 'sh /home/malmomalmo/startup.sh'"
