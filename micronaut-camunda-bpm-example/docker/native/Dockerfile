FROM oracle/graalvm-ce:20.0.0 as builder
RUN gu install native-image
COPY build/libs/micronaut-camunda-bpm-example-0.0.1-SNAPSHOT-all.jar /home/app/micronaut/build/app.jar
COPY src/main/resources/META-INF/native-image/info/novatec/micronaut/camunda/example/reflectionconfig.json /home/app/micronaut
COPY src/main/resources/META-INF/native-image/info/novatec/micronaut/camunda/example/resourceconfig.json /home/app/micronaut
WORKDIR /home/app/micronaut
RUN native-image -jar build/app.jar
EXPOSE 8080
ENTRYPOINT ["./micronaut"]