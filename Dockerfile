FROM gradle:latest AS build
WORKDIR /app

COPY . /app
RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:21

ARG appName

ENV appName ${appName}
ENV appPath /app/libs/venus-1.0.0-SNAPSHOT.jar
ENV LANG en_US.YTF-8
ENV managementPort 10241

WORKDIR /app
COPY --from=build /app/build/libs/ /app/
COPY run.sh /app/

RUN chmod +x /app/run.sh
EXPOSE 10240
CMD ["/app/run.sh"]