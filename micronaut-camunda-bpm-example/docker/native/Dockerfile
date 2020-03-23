FROM oracle/graalvm-ce:20.0.0 as builder
EXPOSE 8080
RUN gu install native-image
COPY build/libs/micronaut-camunda-bpm-example-0.1-SNAPSHOT-all.jar /home/app/micronaut/build/app.jar
COPY src/main/resources/reflectionconfig.json /home/app/micronaut
COPY src/main/resources/resourceconfig.json /home/app/micronaut
## COPY src/main/resources/proxyconfig.json /home/app/micronaut
WORKDIR /home/app/micronaut
RUN native-image -jar build/app.jar
RUN ./micronaut

##Not working
##FROM frolvlad/alpine-glibc
##RUN apk add lib32stdc++6
#EXPOSE 9080
#COPY --from=builder /home/app/micronaut /app/micronaut
#ENTRYPOINTdocker ["/app/micronaut/micronaut", "-Xmx512m"]