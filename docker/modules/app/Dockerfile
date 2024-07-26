FROM java:8
ADD mall.jar /mall.jar
ADD application-prod.yml /application-prod.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/mall.jar","--spring.profiles.active=prod"]
MAINTAINER chenlong
