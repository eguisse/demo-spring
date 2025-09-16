#!/usr/bin/make
# Makefile for project demo-spring

include .env

$(eval export $(shell sed -ne 's/ *#.*$$//; /./ s/=.*$$// p' .env))

SHELL := /bin/bash
# grep the version from the mix file
CURRENT_DIR := $(CURDIR)
ifeq ($(strip $(PROJECT_DIR)), )
PROJECT_DIR := $(CURRENT_DIR)
endif
export PROJECT_DIR
export CURRENT_DIR
export VERSION

# HELP
# This will output the help for each task
.PHONY: help clean build-app build run print-env run-local

help: ## This help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.DEFAULT_GOAL := help


print-env:  ## print environment variables
	echo "PROJECT_DIR: $(PROJECT_DIR)"
	echo "CURRENT_DIR: $(CURRENT_DIR)"
	echo "VERSION: $(VERSION)"
	echo "CURDIR: $(CURDIR)"

clean:  ## gradlew clean
	@echo "start clean"
	./gradlew clean

build-app:  ## Build app using gradlew build
	@echo "start build-site"
	./gradlew bootJar

build-docker:  ## Build the docker image
	@echo "start build"
	docker build -t "demo-spring:snapshot" -f "$(PROJECT_DIR)/Dockerfile" "$(PROJECT_DIR)"

run-docker:  ## Run the docker image on localhost
	@echo "start run"
	if [[ ! -d "$(PROJECT_DIR)/.env" ]] ; then touch "$(PROJECT_DIR)/.env" ; fi
	mkdir -p $(PROJECT_DIR)/data
	docker run -it --rm \
		-p 8080:8080 \
		--name demo-spring \
		--env LOG_LEVEL='INFO' \
		--env-file "$(PROJECT_DIR)/.env" \
		"demo-spring:snapshot"
	@echo "to connect: http://localhost:8080/actuator/health"

run-local:  ## Run the build made locally
	@echo "start run-local"
	if [[ ! -d "$(PROJECT_DIR)/.env" ]] ; then touch "$(PROJECT_DIR)/.env" ; fi
	export $(grep -v '^#' $(PROJECT_DIR)/.env | xargs) && \
	echo "tenant-id=${TENANT_ID}" && \
	java -jar "$(PROJECT_DIR)/build/libs/demo-spring-0.0.1-SNAPSHOT.jar"
	@echo "to connect: http://localhost:8080/demo-spring/api/greeting"

