# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="ettingshausen"

VOLUME /usr/lib/kob/console

# Make port 8080 available to the world outside this container
EXPOSE 8669

# The application's jar file which should be configred by docker maven plugin
ARG JAR_FILE

# Add the application's jar to the container
ADD ${JAR_FILE} /app.jar

# timezone
ARG TIME_ZONE=Asia/Shanghai

RUN apk add -U tzdata \
    && cp  /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime

ENV CONFIG_LOCATION=classpath:/application.yml

# Run the jar file
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar /app.jar --spring.config.location=$CONFIG_LOCATION