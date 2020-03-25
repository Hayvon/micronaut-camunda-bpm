#!/bin/sh
cd ../../
docker build -f docker/native/Dockerfile . -t micronaut
docker run -p 8080:8080 micronaut