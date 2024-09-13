# springboot-starter
docker build -t openai36/springboot-starter .
docker tag openai36/springboot-starter:latest registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest
docker push registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest

docker run -it --rm -p 8888:8080 openai36/springboot-starter


sudo docker pull registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest; sudo docker image ls
sudo docker container stop springboot-starter; sudo docker container rm -f springboot-starter; sudo docker container ls
sudo docker run --name springboot-starter  --restart always -p 8888:8080 -v /var/hc/springboot-starter:/var/hc/springboot-starter -d registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest
sudo docker logs -n 100 -f springboot-starter




# 新建项目初始的地方：

修改pom.xml中的artifactId后可用Idea打开项目

全局替换springboot-starter

```
git remote remove origin
git remote add origin git@github.com:781574155/data-input.git
```

README.md中的启动docker容器的命令还要替换端口号8888

创建数据库data-input后即可运行AggregationApplication