#!/usr/bin/env groovy

def call(Map gitConfig){

    def data = ''

    switch(gitConfig.param){

        case 'longHash':
            data = sh(
                returnStdout: true, 
                script: "git log -n 1 --pretty=format:'%H'"
            ).trim()
            break

        case 'shortHash':
            data = sh(
                returnStdout: true, 
                script: "git log -n 1 --pretty=format:'%h'"
            ).trim()
            break

    }

    return data

}