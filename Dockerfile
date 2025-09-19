# syntax=docker/dockerfile:1.4
###################
# Create openjdk image
###################
FROM ubuntu:22.04 as jdk
# Nota: openjdk docker image is depreciated. So we start with an Ubuntu and add openjdk 17

# Install OpenJDK 21
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -q \
  && apt-get install -q -y --no-install-recommends \
    curl \
    ca-certificates \
    tzdata \
    openjdk-21-jdk \
    locales \
    unzip

# Install graddle
ADD https://services.gradle.org/distributions/gradle-8.14.3-all.zip /tmp/gradle.zip
RUN unzip -d /opt/gradle /tmp/gradle.zip


RUN rm -rf /var/lib/apt/lists/* \
  && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8

RUN useradd --uid 1000 -m -s /sbin/nologin -d /home/app app
RUN mkdir -p /app && chown -R app:app /app && chmod 777 /app

###################
# BUILD THE Java Application
###################
FROM jdk As build

USER app
WORKDIR /app

# copy all
COPY . .

ENV TZ=UTC
ENV LANG=en_US.utf8
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:${PATH}"
ENV JAVA_TOOL_OPTIONS="-Doracle.net.disableOob=true"
ENV PATH="/opt/gradle/gradle-8.14.3/bin:${PATH}"

RUN java -version

RUN /opt/gradle/gradle-8.14.3/bin/gradle bootJar


###################
# Build the final docker image for the Application server FOR PRODUCTION
###################
FROM ubuntu:22.04

ARG APP_VERSION="snapshot"
# Labels
LABEL org.opencontainers.image.authors="emmanuel.guisse@egitc.com"
LABEL org.opencontainers.image.title="demo Springboot Application"
LABEL org.opencontainers.image.ref.name="demo-spring"
LABEL org.opencontainers.image.url="https://github.com/eguisse/demo-spring"
LABEL org.opencontainers.image.source="https://github.com/eguisse/demo-spring"

# Install OpenJDK 21 jre
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -q \
  && apt-get install -q -y --no-install-recommends \
    ca-certificates \
    tzdata \
    openjdk-21-jre \
    locales

RUN rm -rf /var/lib/apt/lists/* \
  && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8

RUN useradd --uid 1000 -m -s /sbin/nologin -d /home/app app
RUN mkdir -p /app && chown -R app:app /app && chmod 777 /app

# Copy the built jar from the build stage
COPY --chown=app:app --from=build /app/build/libs/demo-spring*SNAPSHOT.jar /app/demo-spring.jar


USER app
WORKDIR /app

ENV TZ=UTC
ENV LANG=en_US.utf8
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:${PATH}"
ENV JAVA_TOOL_OPTIONS="-Doracle.net.disableOob=true"

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-Xmx512m"
CMD ["java", "-jar", "/app/demo-spring.jar"]

HEALTHCHECK --interval=1m --timeout=30s --retries=3 CMD curl --fail http://localhost:8080/demo-spring/actuator/health || exit 1

