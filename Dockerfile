FROM openjdk:11 AS sbt-build
RUN echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update
RUN apt-get install sbt
WORKDIR /app/
COPY . .
RUN echo "\n -J-Xss2M" > /etc/sbt/sbtopts
RUN sbt update
RUN sbt assembly

FROM openjdk:11 AS app-run
RUN mkdir /app
WORKDIR /app/
COPY --from=sbt-build /app/target/scala-2.12/bookkeeper-assembly-0.0.1.jar /app/
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "/app/bookkeeper-assembly-0.0.1.jar"]


