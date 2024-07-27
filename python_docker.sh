
 # 현 디렉토리 Dockerfile build (linux 에 배포용이므로 platform 선언
docker build --platform linux/amd64 -t malmomalmo .

# 이미지 tag
docker tag malmomalmo jjong5/malmomalmo:latest

# 이미지 push
docker push jjong5/malmomalmo:latest

# 원격 서버에 python_startup.sh 스크립트 파일 실행
sshpass -p 'Y8@!P@-4MyHce' ssh root@223.130.157.144 "bash -c 'sh /home/python/startup.sh'"
