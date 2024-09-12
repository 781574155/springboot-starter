# springboot-starter
docker build -t openai36/springboot-starter .
docker tag openai36/springboot-starter:latest registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest
docker push registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest

docker run --rm openai36/springboot-starter


sudo docker pull registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest; sudo docker image ls
sudo docker container stop springboot-starter; sudo docker container rm -f springboot-starter; sudo docker container ls
sudo docker run --name springboot-starter  --restart always -p 8080:8080 -v /var/hc/springboot-starter:/var/hc/springboot-starter -d registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest
sudo docker logs -n 100 -f springboot-starter




# 新建项目替换的地方：
- pom.xml
- README.md
- README.md的命令还要替换端口号