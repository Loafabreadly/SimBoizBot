FROM amazoncorretto:17-alpine3.17
# JAR File Name & Location
ENV JAR_NAME=SimBoizBot-all.jar
ENV HOME_DIR=/app

# Setup
RUN mkdir $HOME_DIR
COPY $JAR_NAME ${HOME_DIR}/${JAR_NAME}

# Launch with args
ENTRYPOINT java -jar ${HOME_DIR}/${JAR_NAME}