#!/usr/bin/env groovy

def call(Map gitConfig){

    def data = ''

    switch(gitConfig.param){

        case 'currentBranch':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git rev-parse --git-dir='. --abbrev-ref HEAD
                        """
            ).trim()
            break

        case 'longHash':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%H'
                        """
            ).trim()
            break

        case 'shortHash':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%h'
                        """
            ).trim()
            break

        case 'message':
            data = sh(
                returnStdout: true, 
                script: """
                            #!/bin/bash
                            set +x
                            git log -n 1 --pretty=format:'%s'
                        """
            ).trim()
            break

    }

    return data

}