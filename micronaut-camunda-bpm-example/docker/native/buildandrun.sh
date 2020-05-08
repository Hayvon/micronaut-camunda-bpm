#!/bin/sh
cd ../../
docker build -f docker/native/Dockerfile . -t micronaut
docker run --name micronaut-bpm -v C:/dockerVolume:/mnt -p 8080:8080 micronaut