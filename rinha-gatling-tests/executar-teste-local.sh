#!/usr/bin/env bash

# Use este script para executar testes locais
export GATLING_HOME=$HOME/Development/Tools/gatling-charts-highcharts-bundle-3.10.4

RESULTS_WORKSPACE="$(pwd)/load-test/user-files/results"
GATLING_BIN_DIR=$GATLING_HOME/bin
GATLING_WORKSPACE="$(pwd)/load-test/user-files"

runGatling() {
    sh $GATLING_BIN_DIR/gatling.sh -rm local -s RinhaBackendCrebitosSimulation \
        -rd "Rinha de Backend - 2024/Q1: Crébito" \
        -rf $RESULTS_WORKSPACE \
        -sf "$GATLING_WORKSPACE/simulations"
}

startTest() {
    for i in {1..20}; do
        # 2 requests to wake the 2 api instances up :)
        curl --fail http://localhost:9999/clientes/1/extrato && \
        echo "" && \
        curl --fail http://localhost:9999/clientes/1/extrato && \
        echo "" && \
        runGatling && \
        break || sleep 2;
    done
}

startTest
