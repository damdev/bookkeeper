FROM instructure/scala-sbt:1.2 AS sbt-build
WORKDIR /app/
COPY . .
RUN sbt package

FROM instructure/java:8-xenial AS app-run
WORKDIR /app/
COPY --from=sbt-build /app/target/scala-2.12/increase-bookkeeper-assembly-*.jar /app/
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "/app/increase-bookkeeper-assembly-*.jar"]


