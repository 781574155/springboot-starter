FROM eclipse-temurin:21
ENV TZ=Asia/Shanghai
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]